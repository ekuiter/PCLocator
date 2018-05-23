#!/bin/bash

if [ ! -d busybox-1.18.5 ]; then
    wget --no-check-certificate http://busybox.net/downloads/busybox-1.18.5.tar.bz2
    tar -xvjf busybox-1.18.5.tar.bz2
    rm busybox-1.18.5.tar.bz2
fi

ARGS=$@

if [ $# -eq 0 ]; then
    echo "no <file>:<line> location given, mandatory for this script"
else
    LOCATION=$1
    FILE=$(echo $LOCATION | tr ':' '\n' | sed \$d | tr '\n' ':' | sed 's/.$//')
    LINE=$(echo $LOCATION | tr ':' '\n' | tail -n 1)
    shift
    ARGS=$@
    if [ -z "$FILE" ]; then
        echo "no <file>:<line> location given, mandatory for this script"
    else
        # includes a workaround for the bug mentioned here:
        # http://lists.busybox.net/pipermail/busybox/2011-July/076295.html
        cp libbb.h busybox-1.18.5/include/libbb.h
        cd busybox-1.18.5
        make defconfig
        cd ..
        ./busybox_configure.sh --format config --limit 1 $ARGS $LOCATION > busybox-1.18.5/.config
        if [ $? -eq 0 ]; then
            cd busybox-1.18.5
            yes "" | make oldconfig
            make
            cd ..
        fi
    fi
fi
