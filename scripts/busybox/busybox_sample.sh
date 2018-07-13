#!/bin/bash
# samples random program locations, only non-true locations for correctness
rm -f time_sample.txt
rm -f correctness_sample.txt
i=1
while [ $i -le 1000 ]; do
    FILE=$(find busybox-1.18.5 | grep '\.c$' | grep -v '^busybox-1.18.5/scripts/' | sort -R | head -n 1)
    LINES=$(wc -l $FILE | awk '{print $1}')
    LINE=$(($RANDOM%LINES+1))
    LOCATION=$FILE:$LINE
    echo $LOCATION >> time_sample.txt
    ((i++))
    # TODO: run pclocator, check if isTrue, then add to "correctness_sample.txt"
    RES=$(./busybox_configure.sh --parser featurecopp --evaluate $LOCATION)
    echo $RES
    if [[ "$RES" == *,0 ]]; then
        echo $LOCATION >> correctness_sample.txt
    fi
done
cat time_sample.txt | uniq | tee time_sample.txt
cat correctness_sample.txt | uniq | tee correctness_sample.txt