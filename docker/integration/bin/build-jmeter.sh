#!/bin/sh

. ./env.sh

cd $baseDir/../jmeter
sudo docker build -t $tagprefix/jmeter .

