# -*- coding: utf-8 -*-
import re
import sqlalchemy as sa
import sys
import unicodedata

query = sys.argv[1].decode('UTF-8')

eng = sa.create_engine('sqlite:///db.sqlite')
for word, in eng.execute(u'select word from word_blacklist'):
    query = query.replace(word, u'')

shards = []
run = dict(mode=None, content=[])
for u in query:
    try:
        name = unicodedata.name(u)
        print name
        if 'IDEOGRAPH-' in name or 'IDEOGRAPHIC ITERATION MARK' in name:
            mode = 'ideograph'
        elif 'SPACE' in name:
            continue
        elif 'PROLONGED SOUND MARK' in name:
            pass
        elif 'HIRAGANA' in name:
            mode = 'hiragana'
        elif 'KATAKANA' in name:
            mode = 'katakana'
        else:
            mode = 'other'
        if run['mode'] is None:
            run['mode'] = mode
        if run['mode'] == mode:
            run['content'].append(u)
            continue
    except ValueError:
        pass

    if len(run) > 0:
        k = ''.join(run['content'])
        shards.append(k)
        run['mode'] = mode
        run['content'] = [u]
if len(run) > 0:
    k = ''.join(run['content'])
    shards.append(k)
    run['mode'] = mode
    run['content'] = [u]

shards = filter(lambda x: len(x) > 1, shards)

print shards

candidates = [dict(word=word, category=category, score=0) for word, category in eng.execute("SELECT word_category.* FROM word_category WHERE ? LIKE '%'||word||'%' OR word LIKE '%'||?||'%' ORDER BY length(word) DESC", query, query)]

print candidates

for word in candidates:
    query.search()
