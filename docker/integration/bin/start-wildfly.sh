#!/bin/sh

. ./env.sh

echo "Starting as1."
status=`sudo docker ps -a | grep 'as1 *$' | sed 's/.*\(Up\|Exit\).*/\1/'`
if [ -n $status ]; then
  echo "Removing old as1."
  if [ "$status" = "Up" ]; then
    sudo docker stop as1
  fi
  sudo docker rm as1
  echo "Old as1 removed."
fi
sudo docker run -d -name as1 $tagprefix/int_wildfly
sudo $baseDir/pipework/pipework $bridgeName as1 $as1_ip/$netmask
# Wait Wildfly boot.
sleep 20
echo "as1 started. (IP address=$as1_ip/$netmask)"

