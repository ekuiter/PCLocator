#!/bin/bash

# This script is intended for use inside the virtual machine.

# install add-apt-repository to add a repository containing Java 1.8
sudo apt-get install software-properties-common python-software-properties -y
sudo add-apt-repository ppa:webupd8team/java -y
sudo apt-get update -y

# git for cloning the challenge repository
# make etc. for compiling busybox
# Java for PCLocator
sudo apt-get install git make electric-fence libselinux-dev libpam-dev libdmalloc-dev oracle-java8-installer -y
