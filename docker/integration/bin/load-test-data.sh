#!/bin/sh

. ./env.sh

chmod +x $baseDir/maven/run_loader.sh
echo "Loading test data."
sudo docker run -v $baseDir/maven:/shared $tagprefix/maven /shared/run_loader.sh
echo "Loaded test data."

