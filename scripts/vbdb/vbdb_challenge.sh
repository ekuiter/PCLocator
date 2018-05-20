#!/bin/bash

if [ ! -d splc18challengecase ]; then
    git clone https://github.com/paulgazz/splc18challengecase.git
fi

mkdir -p challenge

if [ $# -eq 0 ]; then
    echo "no time limit given"
else
    for location in $(cat locations.txt); do
        echo $location
        if [[ $location = *"marlin"* ]]; then
            MODEL=marlin.dimacs
        elif [[ $location = *"busybox"* ]]; then
            MODEL=busybox.dimacs
        elif [[ $location = *"linux"* ]]; then
            MODEL=linux.dimacs
        fi
        java -jar ../PCLocator.jar --configure $MODEL --timelimit $@ $location > "challenge/$(echo $location | sed 's#/#_#g' | sed 's#:#_#g').txt" 2>&1
    done
fi
