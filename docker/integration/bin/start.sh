#!/bin/sh

. ./env.sh

./build-postgresql.sh &&\
./build-maven.sh &&\
./build-app.sh &&\
./download-pipework.sh &&\
./configure-bridge.sh &&\
./start-postgresql.sh &&\
./build-wildfly.sh &&\
./start-wildfly.sh $num_of_as &&\
./start-nginx.sh $num_of_as &&\
./run-basic-test.sh &&\
./load-test-data.sh &&\
./run-perf-test.sh

