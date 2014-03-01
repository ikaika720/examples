#!/bin/sh

tagprefix=inabatk

baseDir=$(pwd)/$(dirname $0)

# Build PostgreSQL images.
cd $baseDir/../postgresql-build-from-source
sudo docker build -t $tagprefix/postgresql .
cd $baseDir/postgresql
cp $baseDir/../jpa-webapp-spring/src/main/sql/*.sql .
sudo docker build -t $tagprefix/int_postgresql .

# Build Maven image and build the application.
cd $baseDir/../maven
sudo docker build -t $tagprefix/maven .
cd $baseDir/../../
tar czf $baseDir/maven/jpa-webapp-spring.tar.gz jpa-webapp-spring
cd $baseDir/maven
chmod +x build.sh
sudo docker run -v $baseDir/maven:/shared $tagprefix/maven /shared/build.sh

# Build Wildfly image.
cd $baseDir/../wildfly
sudo docker build -t $tagprefix/wildfly .

# Start a PostgreSQL container.
# The data source definition for the database needs to know the IP address.
pgsqlcid=$(sudo docker run -d $tagprefix/int_postgresql)
pgsqlip=$(sudo docker inspect -format='{{.NetworkSettings.IPAddress}}' $pgsqlcid)

# Build Wildfly image and start the container.
cd $baseDir/wildfly
cp $baseDir/maven/jpa-webapp-spring.war .
sed -i -e "s/\[servername\]/$pgsqlip/" wf-command.txt
sudo docker build --no-cache -t $tagprefix/int_wildfly .
sudo docker run -d $tagprefix/int_wildfly
