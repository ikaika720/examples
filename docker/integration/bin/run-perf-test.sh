#!/bin/sh

. ./env.sh

echo "JMeter test starting."
sudo docker run -v $baseDir/jmeter:/shared $tagprefix/jmeter jmeter -n \
  -t /shared/performance.jmx -l /shared/performance.jtl -j /shared/performance.log
echo "JMeter test finished."

