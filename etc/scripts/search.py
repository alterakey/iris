import re
import sys
import mechanize
import time

class QueryIssuer(object):
    def __new__(cls, code):
        return cls._MockIssuer(code)

    class _MockIssuer(object):
        def __init__(self, code):
            self.code = code

        def issue(self):
            return open('corpse.%s.html' % code, 'r')

    class _RealIssuer(object):
        def __init__(self, code):
            self.code = code

        def issue(self):
            br = mechanize.Browser()
            br.addheaders = [
                ('User-Agent', 'w3m/0.5.1')
            ]
            br.set_handle_robots(False)
            br.open("http://www.google.co.jp/")
            br.select_form(name='f')
            br['q'] = code
            time.sleep(0.8)
            br.submit()
            return br.response()

class GoogleResponseDigester(object):
    def __init__(self, stream, code):
        self.stream = stream
        self.code = code

    def digest(self):
        ret = dict()
        corpse = self.stream.read().decode('CP932', 'ignore')
        for m in re.finditer(u'<li class="g">.*?<h3 class="r"><a href="/url\?.*?q=(.*?)&.*?">(.*?)</a>.*?<span class="st">(.*?)</span>', corpse):
            yield dict(url=m.group(1), linktitle=m.group(2), excerpt=m.group(3))


class Categorizer(object):
    patterns = dict(
        u'精肉|豚|牛|[鳥鶏]|卵|玉子|(?:もも|胸|むね)肉?': u'肉',
        u'畜産|ハム|ソーセージ|ベーコン|チキン|ナゲット|ハンバーグ': u'肉',
        u'水産|鮮魚|': u'魚',
        u'農産|青果|JA|農協|野菜|やさい|くだもの': u'野菜',
        u'生菓子|ケーキ': u'菓子'
    )

    def __init__(self, code):
        self.code = code
        self.category = None

        self.matchers = dict()
        for pat, category in self.patterns:
            self.matchers[re.compile(pat)] = category

    def update(self, digest):
        for matcher, category in self.matchers.iteritems():
            if matcher.search(digest):
                print '%s -> %s' % category

code = sys.argv[1]
with QueryIssuer(code).issue() as query:
    for d in GoogleResponseDigester(query, code).digest():
        print u'%(url)s: %(linktitle)s, %(excerpt)s' % d
