#!/bin/bash

if [ ! -d splc18challengecase ]; then
    git clone https://github.com/paulgazz/splc18challengecase.git
fi

ARGS=$@

if [ $# -eq 0 ]; then
    ARGS=--help
fi

java -jar ../PCLocator.jar --configure $ARGS
