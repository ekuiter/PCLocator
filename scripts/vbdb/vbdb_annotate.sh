#!/bin/bash

if [ ! -d splc18challengecase ]; then
    git clone https://github.com/paulgazz/splc18challengecase.git
fi

mkdir -p annotations_marlin annotations_busybox annotations_linux

for f in splc18challengecase/vbdb/marlin/simple/*.c
do
    echo $f
    java -jar ../PCLocator.jar $f > "pc_marlin/$(basename $f .c).txt" 2>&1
    java -jar ../PCLocator.jar --configure marlin.dimacs $f >> "annotations_marlin/$(basename $f .c).txt" 2>&1
done

for f in splc18challengecase/vbdb/busybox/simple/*.c
do
    echo $f
    java -jar ../PCLocator.jar $f > "pc_busybox/$(basename $f .c).txt" 2>&1
    java -jar ../PCLocator.jar --configure busybox.dimacs $f >> "annotations_busybox/$(basename $f .c).txt" 2>&1
done

for f in splc18challengecase/vbdb/linux/simple/*.c
do
    echo $f
    java -jar ../PCLocator.jar $f > "pc_linux/$(basename $f .c).txt" 2>&1
    java -jar ../PCLocator.jar --configure linux.dimacs $f >> "annotations_linux/$(basename $f .c).txt" 2>&1
done
