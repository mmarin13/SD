#!/usr/bin/env python
"""reducer.py"""

import sys


current_url = None
current_count = 0
url = None

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    url, count = line.split()

    # convert count (currently a string) to int
    try:
        count = int(count)
    except ValueError:
        # count was not a number, so silently
        # ignore/discard this line
        continue

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if current_url != url:
        if current_url is not None:
            # write result to STDOUT
            print('{}\t{}'.format(current_url, current_count))
        current_count = 0
        current_url = url
    current_count += count

# do not forget to output the last word if needed!
if current_count > 0:
    print('{}\t{}'.format(current_url, current_count))
