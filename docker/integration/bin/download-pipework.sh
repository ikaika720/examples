#!/bin/sh

. ./env.sh

cd $baseDir
if [ -d $baseDir/pipework ]
then
  cd pipework
  git pull
else
  git clone https://github.com/jpetazzo/pipework.git
fi

