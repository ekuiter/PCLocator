#!/bin/bash

if [ ! -d splc18challengecase ]; then
    git clone https://github.com/paulgazz/splc18challengecase.git
fi

for location in $(cat locations.txt); do
    echo
    echo "Processing location $location."
    ./vbdb_challenge.sh $location --limit 1
done
