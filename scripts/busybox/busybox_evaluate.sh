#!/bin/bash

if [ ! -d busybox-1.18.5 ]; then
    wget --no-check-certificate http://busybox.net/downloads/busybox-1.18.5.tar.bz2
    tar -xvjf busybox-1.18.5.tar.bz2
    rm busybox-1.18.5.tar.bz2
fi

rm -rf challenge

echo locator,location,time,Nall,Nmissed,Ncorrect,Nwrong,mergeEquivalent,mergeSuperCSubSpace,mergeTypeChefSubSpace,mergeDisjointOrOverlapping,isTrue
for LEGACY in '' --legacy; do
for PARSER in typechef superc featurecopp merge; do
for LOCATION in $(cat time_sample.txt); do
    ./busybox_configure.sh --evaluate --parser $PARSER $LEGACY $@ $LOCATION 2>/dev/null
done
done
done