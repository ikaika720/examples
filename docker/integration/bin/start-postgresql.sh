#!/bin/sh

. ./env.sh

echo "Starting db1."
status=`sudo docker ps -a | grep 'db1 *$' | sed 's/.*\(Up\|Exit\).*/\1/'`
if [ -n $status ]; then
  echo "Removing old db1."
  if [ "$status" = "Up" ]; then
    sudo docker stop db1
  fi
  sudo docker rm db1
  echo "Old db1 removed."
fi
sudo docker run -d -name db1 inabatk/int_postgresql
sudo $baseDir/pipework/pipework $bridgeName db1 $db1_ip/$netmask
echo "db1 started. (IP address=$db1_ip/$netmask)"

