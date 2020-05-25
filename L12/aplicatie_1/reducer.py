#!/usr/bin/env python
"""reducer.py"""

import sys


current_url = None
current_anchors_list = list()
url = None

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    url, anchor = line.split()

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if current_url != url:
        if current_url is not None:
            # write result to STDOUT
            print('{}\t{}'.format(current_url, current_anchors_list))
        current_url = url
        current_anchors_list = list()
    current_anchors_list.append(anchor)

# do not forget to output the last url if needed!
if len(current_anchors_list) > 0:
    print('{}\t{}'.format(current_url, current_anchors_list))
