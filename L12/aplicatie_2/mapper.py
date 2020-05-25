#!/usr/bin/env python
"""mapper.py"""

import sys
import sqlite3


conn = None
try:
    conn = sqlite3.connect('input/places.sqlite')
    cursor = conn.cursor()
    cursor.execute('SELECT url, visit_count FROM moz_places ORDER BY visit_count DESC LIMIT 50;')
    rows = cursor.fetchall()
    for row in rows:
        print('{}\t{}'.format(row[0].split('?')[0].split('#')[0], row[1]))  # queries and fragments are removed from url
except sqlite3.Error as err:
    print(err)
    exit(-1)
