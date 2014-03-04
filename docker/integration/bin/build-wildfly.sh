#!/bin/sh

. ./env.sh

cd $baseDir/../wildfly
sudo docker build -t $tagprefix/wildfly .
cd $baseDir/wildfly
cp $baseDir/maven/jpa-webapp-spring.war .
sudo docker build --no-cache -t $tagprefix/int_wildfly .

