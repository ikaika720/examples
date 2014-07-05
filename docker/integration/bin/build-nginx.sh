#!/bin/sh

. ./env.sh

cd $baseDir/../nginx
sudo docker build -t $tagprefix/nginx .

