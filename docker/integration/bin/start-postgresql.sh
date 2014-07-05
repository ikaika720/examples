#!/bin/sh

. ./env.sh

echo "Starting db1."
status=$(sudo docker ps -a | grep ' *db1 *$')
if [ -n "$status" ]; then
  echo "Removing old db1."
  sudo docker rm -f db1
  echo "Old db1 removed."
fi
sudo docker run -d --name db1 inabatk/int_postgresql
sudo $baseDir/pipework/pipework $bridgeName db1 $db1_ip/$netmask
echo "db1 started. (IP address=$db1_ip/$netmask)"

