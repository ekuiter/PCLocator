#!/bin/bash

if [ ! -d splc18challengecase ]; then
    git clone https://github.com/paulgazz/splc18challengecase.git
fi

rm -rf challenge

for LEGACY in '' --legacy; do
for PARSER in typechef superc featurecopp merge; do
for LOCATION in $(cat locations.txt); do
    FILE=$(echo $LOCATION | tr ':' '\n' | sed \$d | tr '\n' ':' | sed 's/.$//')
    LINE=$(echo $LOCATION | tr ':' '\n' | tail -n 1)
    ARGS=$@
    if [[ $FILE = *"marlin"* ]]; then
        MODEL=marlin.dimacs
    elif [[ $FILE = *"busybox"* ]]; then
        MODEL=busybox.dimacs
    elif [[ $FILE = *"linux"* ]]; then
        MODEL=linux.dimacs
    fi
    java -jar ../PCLocator.jar --evaluate --configure $MODEL --parser $PARSER $LEGACY $@ $LOCATION 2>/dev/null
done
done
done