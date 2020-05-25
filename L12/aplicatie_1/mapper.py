#!/usr/bin/env python
"""mapper.py"""

import sys
import urllib.request
from bs4 import BeautifulSoup


# input comes from STDIN (standard input)
for line in sys.stdin:
    # remove leading and trailing whitespace
    url = line.strip()
    page = urllib.request.urlopen(url)
    soup = BeautifulSoup(page, features="html.parser")
    for link in soup.find_all('a'):
        # write the results to STDOUT (standard output);
        # what we output here will be the input for the
        # Reduce step, i.e. the input for reducer.py
        anchor = link.get('href')
        if anchor is not None:
            anchor = anchor.strip()
            if '' != anchor:
                print('{}\t{}'.format(url, anchor))
