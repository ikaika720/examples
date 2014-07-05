#!/bin/sh

. ./env.sh

echo "Starting JMeter test."
sudo docker run -v $baseDir/jmeter:/shared $tagprefix/jmeter jmeter -n \
  -t /shared/basic.jmx -l /shared/basic.jtl -j /shared/basic.log
echo "JMeter test finished."

