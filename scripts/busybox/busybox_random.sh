#!/bin/bash
# derives one configuration for a random program location and measures runtime
FILE=$(find busybox-1.18.5 | grep '\.c$' | grep -v '^busybox-1.18.5/scripts/' | sort -R | head -n 1)
LINES=$(wc -l $FILE | awk '{print $1}')
LINE=$(($RANDOM%LINES+1))
LOCATION=$FILE:$LINE
(time ./busybox_configure.sh --limit 1 $LOCATION) 2>random.log
echo -e $LOCATION '\t' $(grep '^real\s*.*$' random.log | cut -f 2) >> random_measurements.log
