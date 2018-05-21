#!/bin/bash
rm -rf ../out/artifacts/PCLocator_jar/mock
cp -R . ../out/artifacts/PCLocator_jar
rm ../out/artifacts/PCLocator_jar/post_build.sh
