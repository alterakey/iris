# -*- coding: utf-8 -*-/
import sys
import re
import unicodedata
import urllib
import sqlalchemy as sa
import os

class CorpseFetcher(object):
    def __new__(cls, page, use_cache=False):
        if not use_cache:
            return cls._Real(page)
        else:
            return cls._Cache(page)

    class _Cache(object):
        def __init__(self, page):
            self.page = page

        def fetch(self):
            return open(u'wp.%s.html' % self.page, 'r')

    class _Real(object):
        def __init__(self, page):
            self.page = page
            self._install_opener()

        def fetch(self):
            return urllib.urlopen('http://ja.wikipedia.org/wiki/%s' % urllib.quote(self.page.encode('UTF-8')))

        def _install_opener(self):
            class Opener(urllib.FancyURLopener):
                version = 'curl/7.27.0'
            urllib._urlopener = Opener()

class CorpseSaver(object):
    def __init__(self, page, stream):
        self.page = page
        self.stream = stream

    def save(self):
        with open(u'wp.%s.html' % self.page, 'w') as f:
            f.write(self.stream.read())

class CorpseDigester(object):
    def __init__(self, corpse):
        self.corpse = corpse

    def digest(self):
        corpse = self.corpse
        corpse = re.sub(u'\n', '', corpse, flags=re.DOTALL)
        corpse = re.sub(u'>\s+<', '><', corpse, flags=re.DOTALL)
        corpse = re.sub(u'<script ?.*?</script>', '', corpse, flags=re.DOTALL)
        corpse = re.sub(u'<img .*?>', '', corpse, flags=re.DOTALL)
        corpse = re.sub(u'<!--.*?-->', '', corpse, flags=re.DOTALL)
        return corpse

class Skimmer(object):
    def __new__(cls, category, *args, **kwargs):
        mapping = {
            u'魚':cls.FishSkimmer,
            u'野菜':cls.VegitSkimmer,
        }
        return mapping[category](*args, **kwargs)

    class FishSkimmer(object):
        BLACKLIST = (u'ページ', u'一覧の一覧', u'UTC', u'閲覧', u'メインページ', u'魚類', u'化石', u'脊椎動物', u'無顎類', u'和名')

        def __init__(self, digest):
            self.digest = digest

        def skim(self):
            for m in re.finditer(u'<a ?.*?href="/wiki/[^:]*?>(.*?)</a>', self.digest):
                name = m.group(1)
                if self.transform:
                    name = self.transform(name)
                if name and name not in self.BLACKLIST:
                    for n in Reader(name).read():
                        yield n

        @staticmethod
        def transform(x):
            return re.sub('[（(].*?[）)]', '', x)

    class VegitSkimmer(FishSkimmer):
        pass

# XXX: Bad name
class Reader(object):
    HIRAGANA = u'あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽぁぃぅぇぉゃゅょっ'
    KATAKANA = u'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポァィゥェォャュョッ'
    def __init__(self, x):
        self.x = x

    def read(self):
        yield self.x
        yield self.as_hiragana()
        yield self.as_katakana()

    def _tr(self, from_, to_):
        def _repl(m):
            try:
                return to_[from_.index(m.group(0))]
            except IndexError:
                return m.group(0)
        return re.sub(u'[%s]' % from_, _repl, self.x)

    def as_hiragana(self):
        return self._tr(self.KATAKANA, self.HIRAGANA)

    def as_katakana(self):
        return self._tr(self.HIRAGANA, self.KATAKANA)

class PythonDictWriter(object):
    def __init__(self, stream, d):
        self.stream = stream
        self.d = d

    def write(self):
        self.stream.write(u'''\
# -*- coding: utf-8 -*-
table = dict(
'''.encode('utf-8'))
        for k, v in self.d.iteritems():
            self.stream.write((u'''\
  u'%s': u'%s',
''' % (k, v)).encode('utf-8'))

        self.stream.write(u'''\
)
'''.encode('utf-8'))

class SQLiteWriter(object):
    def __init__(self, stream, d):
        self.stream = stream
        self.d = d

    def write(self):
        temp_file_name = 'db.temp.sqlite'
        try:
            eng = sa.create_engine('sqlite:///%s' % temp_file_name)
            eng.execute('DROP TABLE IF EXISTS word_blacklist')
            eng.execute('CREATE TABLE word_blacklist (word TEXT)')
            eng.execute('DROP TABLE IF EXISTS word_category')
            eng.execute('CREATE TABLE word_category (word TEXT, category TEXT)')
            eng.transaction(self._write)
            with open(temp_file_name, 'r') as f:
                self.stream.write(f.read())
        finally:
            try:
                os.remove(temp_file_name)
            except os.OSError, e:
                if e.errno != os.ENOENT:
                    raise

    def _write(self, txn):
        for k, v in self.d.iteritems():
            txn.execute('insert into word_category(category, word) values (?, ?)', v, k)

if __name__ == '__main__':
    mapping = dict()
    mode = sys.argv[1]
    descs = (r.decode('UTF-8').split(u':', 1) for r in sys.argv[2:])

    if mode == 'fetch':
        sys.stderr.write('fetching pages')
        for category, src in descs:
            CorpseSaver(src, CorpseFetcher(src).fetch()).save()
            sys.stderr.write('.')
            continue
        sys.stderr.write('done\n')
    elif mode == 'parse':
        sys.stderr.write('parsing pages')
        for category, src in descs:
            f = CorpseFetcher(src, use_cache=True).fetch()
            try:
                for word in Skimmer(category, CorpseDigester(f.read().decode('UTF-8')).digest()).skim():
                    if len(mapping) % 100 == 0:
                        sys.stderr.write('.')
                    mapping[word] = category
            except KeyError:
                sys.stderr.write((u"Unknown category, ignoring: %s (%s)\n" % (category, src)).encode('utf-8'))
        sys.stderr.write('done (%d)\n' % len(mapping))
        SQLiteWriter(sys.stdout, mapping).write()
