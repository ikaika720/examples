#!/bin/sh

. ./env.sh

num=1
if [ -n "$1" ]; then
  num=$1
fi

start_as () {
  echo "Starting $1."
  status=$(sudo docker ps -a | grep " *$1 *$")
  if [ -n "$status" ]; then
    echo "Removing old $1."
    sudo docker rm -f $1
    echo "Old $1 removed."
  fi
  sudo docker run -d --name $1 $tagprefix/int_wildfly
  sudo $baseDir/pipework/pipework $bridgeName $1 $2/$netmask
  echo "$1 started. (IP address=$2/$netmask)"
}

ip_pref=`echo $as1_ip | sed 's/\([0-9.]\+\.\)[0-9]\+/\1/'`
ip_suf=`echo $as1_ip | sed 's/[0-9.]\+\.\([0-9]\+\)/\1/'`
last=`expr $num - 1`
for i in `seq 0 $last`
do
  start_as "as`expr $i + 1`" "$ip_pref`expr $ip_suf + $i`"
done

# Wait Wildfly boot.
sleep 20

