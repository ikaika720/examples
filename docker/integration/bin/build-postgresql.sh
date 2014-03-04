#!/bin/sh

. ./env.sh

# Build PostgreSQL images.
cd $baseDir/../postgresql-build-from-source
sudo docker build -t $tagprefix/postgresql .
cd $baseDir/postgresql
cp $baseDir/../../jpa-webapp-spring/src/main/sql/*.sql .
sudo docker build -t $tagprefix/int_postgresql .

