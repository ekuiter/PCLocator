#!/bin/bash

if [ ! -d splc18challengecase ]; then
    git clone https://github.com/paulgazz/splc18challengecase.git
fi

mkdir -p challenge

if [ $# -eq 0 ]; then
    echo "no <file>:<line> location given, mandatory for this script"
else
    LOCATION=$1
    FILE=$(echo $LOCATION | tr ':' '\n' | sed \$d | tr '\n' ':' | sed 's/.$//')
    LINE=$(echo $LOCATION | tr ':' '\n' | tail -n 1)
    shift
    ARGS=$@
    CC=${CC:-gcc}
    if [ -z "$FILE" ]; then
        echo "no <file>:<line> location given, mandatory for this script"
    else
        LOG_FILE="challenge/$(echo $LOCATION | sed 's#/#_#g' | sed 's#:#_#g').log"
        echo > $LOG_FILE
        echo "Now deriving configurations for $FILE which should contain line $LINE."
        echo "Now deriving configurations for $FILE which should contain line $LINE." >> $LOG_FILE
        LINESTR=$(sed "$LINE!d" $FILE)
        if [[ $FILE = *"marlin"* ]]; then
            MODEL=marlin.dimacs
        elif [[ $FILE = *"busybox"* ]]; then
            MODEL=busybox.dimacs
        elif [[ $FILE = *"linux"* ]]; then
            MODEL=linux.dimacs
        fi
        i=0
        (time java -jar ../PCLocator.jar --configure $MODEL --format flags $ARGS $LOCATION) 2>>$LOG_FILE | while read -r line; do
            PREFIX="challenge/$(echo $LOCATION | sed 's#/#_#g' | sed 's#:#_#g')_$((++i))"
            PREPROCESSED_FILE="$PREFIX"".c"
            COMPILED_FILE=$PREFIX
            CFG_LOG_FILE="$PREFIX"".log"
            echo > $CFG_LOG_FILE
            echo "Processing configuration #$i, see $CFG_LOG_FILE."
            echo "Processing configuration #$i." >> $CFG_LOG_FILE
            echo >> $CFG_LOG_FILE
            echo "Generating preprocessed file $PREPROCESSED_FILE." >> $CFG_LOG_FILE
            echo $CC -E $line $FILE >> $CFG_LOG_FILE
            $CC -E $line $FILE > $PREPROCESSED_FILE
            echo >> $CFG_LOG_FILE
            echo "Trying to compile to $COMPILED_FILE". >> $CFG_LOG_FILE
            echo $CC $line $FILE -o $COMPILED_FILE >> $CFG_LOG_FILE
            $CC $line $FILE -o $COMPILED_FILE >> $CFG_LOG_FILE 2>&1
            if [ $? -ne 0 ]; then
                echo "Compilation failed." >> $CFG_LOG_FILE
            fi
        done
        echo -e $LOCATION '\t' $(grep '^real\s*.*$' $LOG_FILE | cut -f 2) >> challenge/measurements.log
    fi
fi
