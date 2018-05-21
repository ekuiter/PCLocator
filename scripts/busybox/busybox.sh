#!/bin/bash

if [ ! -d busybox-1.18.5 ]; then
    wget https://busybox.net/downloads/busybox-1.18.5.tar.bz2
    tar -xvjf busybox-1.18.5.tar.bz2
    rm busybox-1.18.5.tar.gz
fi

ARGS=$@

if [ $# -eq 0 ]; then
    ARGS=--help
fi

java -jar ../PCLocator.jar -I busybox-1.18.5/include --platform platform.h --locator kmax --kmaxfile busybox-1.18.5.kmax --projectroot busybox-1.18.5 $ARGS
