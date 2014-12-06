#!/bin/sh

. ./env.sh

as_ips="server $as1_ip:8080;"

if [ -n "$1" ]; then
  ip_pref=`echo $as1_ip | sed 's/\([0-9.]\+\.\)[0-9]\+/\1/'`
  ip_suf=`echo $as1_ip | sed 's/[0-9.]\+\.\([0-9]\+\)/\1/'`
  last=`expr $1 - 1`
  for i in `seq 1 $last`
  do  
    as_ips="${as_ips}server $ip_pref`expr $ip_suf + $i`:8080;"
  done
fi

cp $baseDir/nginx/nginx.conf.template $baseDir/nginx/nginx.conf
sed -i "s/\(upstream \+.\+ *{\).*}/\1$as_ips}/" $baseDir/nginx/nginx.conf

echo "Starting lb1."
status=$(sudo docker ps -a | grep ' *lb1 *$')
if [ -n "$status" ]; then
  echo "Removing old lb1."
  sudo docker rm -f lb1
  echo "Old lb1 removed."
fi
sudo docker run -d --name lb1 -v $baseDir/nginx:/shared $tagprefix/nginx nginx -c /shared/nginx.conf
sudo $baseDir/pipework/pipework $bridgeName lb1 $lb1_ip/$netmask
echo "lb1 started. (IP address=$lb1_ip/$netmask)"

