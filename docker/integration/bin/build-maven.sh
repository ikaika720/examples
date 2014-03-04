#!/bin/sh

. ./env.sh

cd $baseDir/../maven
sudo docker build -t $tagprefix/maven .

