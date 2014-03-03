#!/bin/sh

tagprefix=inabatk
bridgeName=br1
netmask=24
host_ip=172.24.0.1
db1_ip=172.24.0.11
as1_ip=172.24.0.41

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

# Build Wildfly images.
cd $baseDir/../wildfly
sudo docker build -t $tagprefix/wildfly .
cd $baseDir/wildfly
cp $baseDir/maven/jpa-webapp-spring.war .
sudo docker build --no-cache -t $tagprefix/int_wildfly .

# Download pipework.
cd $baseDir
git clone https://github.com/jpetazzo/pipework.git

# Configure network.
ifconfig $bridgeName > /dev/null 2>&1
if [ "$?" -eq "0" ]; then
  sudo ip link set dev $bridgeName down
  sudo brctl delbr $bridgeName
  sudo brctl addbr $bridgeName
  sudo ip addr add $host_ip/$netmask dev $bridgeName 
  sudo ip link set dev $bridgeName up
fi

# Start a PostgreSQL container.
echo "Starting db1."
if [ ! -z "`sudo docker ps | grep 'db1 *$'`" ]; then
  echo "Removing old db1."
  sudo docker stop db1
  sudo docker rm db1
  echo "Old db1 removed."
fi
sudo docker run -d -name db1 inabatk/int_postgresql
sudo $baseDir/pipework/pipework $bridgeName db1 $db1_ip/$netmask
echo "db1 started. (IP address=$db1_ip/$netmask)"

# Load test data.
chmod +x $baseDir/maven/run_loader.sh
echo "Loading test data."
sudo docker run -v $baseDir/maven:/shared inabatk/maven /shared/run_loader.sh
echo "Loaded test data."

# Start a Wildfly container.
echo "Starting as1."
if [ ! -z "`sudo docker ps | grep 'as1 *$'`" ]; then
  echo "Removing old as1."
  sudo docker stop as1
  sudo docker rm as1
  echo "Old as1 removed."
fi
sudo docker run -d -name as1 $tagprefix/int_wildfly
sudo $baseDir/pipework/pipework $bridgeName as1 $as1_ip/$netmask
echo "as1 started. (IP address=$as1_ip/$netmask)"

