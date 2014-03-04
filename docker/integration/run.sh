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
cp $baseDir/../../jpa-webapp-spring/src/main/sql/*.sql .
sudo docker build -t $tagprefix/int_postgresql .

# Build Maven image and build the application.
cd $baseDir/../maven
sudo docker build -t $tagprefix/maven .
cd $baseDir/../../
tar czf $baseDir/maven/jpa-webapp-spring.tar.gz jpa-webapp-spring
cd $baseDir/maven
chmod +x build.sh
sudo docker run -v $baseDir/maven:/shared $tagprefix/maven /shared/build.sh

# Download pipework.
cd $baseDir
if [ -d $baseDir/pipework ]
then
  cd pipework
  git pull
else
  git clone https://github.com/jpetazzo/pipework.git
fi

# Configure network.
ifconfig $bridgeName > /dev/null 2>&1
if [ "$?" -eq "0" ]; then
  sudo ip link set dev $bridgeName down
  sudo brctl delbr $bridgeName
fi
sudo brctl addbr $bridgeName
sudo ip addr add $host_ip/$netmask dev $bridgeName 
sudo ip link set dev $bridgeName up

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

# Build Wildfly images.
cd $baseDir/../wildfly
sudo docker build -t $tagprefix/wildfly .
cd $baseDir/wildfly
cp $baseDir/maven/jpa-webapp-spring.war .
sudo docker build --no-cache -t $tagprefix/int_wildfly .

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
# Wait Wildfly boot.
sleep 20

# Run JMeter
cd $baseDir/../jmeter
sudo docker build -t $tagprefix/jmeter .
if [ -d $baseDir/jmeter ]; then
  rm -Rf $baseDir/jmeter
fi
echo "Starting JMeter test."
mkdir $baseDir/jmeter
cd $baseDir/jmeter
cp $baseDir/../../jpa-webapp-spring/src/test/jmeter/*.jmx .
sed -i "s/\(<stringProp name=\"HTTPSampler.domain\">\).\+\(<\/stringProp>\)/\1$as1_ip\2/" *.jmx
sudo docker run -v $baseDir/jmeter:/shared $tagprefix/jmeter jmeter -n \
  -t /shared/basic.jmx -l /shared/basic.jtl -j /shared/basic.log
echo "JMeter test finished."

# Load test data.
chmod +x $baseDir/maven/run_loader.sh
echo "Loading test data."
sudo docker run -v $baseDir/maven:/shared inabatk/maven /shared/run_loader.sh
echo "Loaded test data."

#Run JMeter
echo "JMeter test starting."
sudo docker run -v $baseDir/jmeter:/shared $tagprefix/jmeter jmeter -n \
  -t /shared/performance.jmx -l /shared/performance.jtl -j /shared/performance.log
echo "JMeter test finished."

