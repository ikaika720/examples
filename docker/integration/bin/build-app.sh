#!/bin/sh

. ./env.sh

cd $baseDir/../../
tar czf $baseDir/maven/jpa-webapp-spring.tar.gz jpa-webapp-spring
cd $baseDir/maven
chmod +x build.sh
if [ -f "jpa-webapp-spring.war" ]
then
  echo "Skip building the application."
else
  sudo docker run -v $baseDir/maven:/shared $tagprefix/maven /shared/build.sh
fi

