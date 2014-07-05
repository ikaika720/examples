#!/bin/sh

. ./env.sh

ifconfig $bridgeName > /dev/null 2>&1
if [ "$?" -eq "0" ]; then
  sudo ip link set dev $bridgeName down
  sudo brctl delbr $bridgeName
fi
sudo brctl addbr $bridgeName
sudo ip addr add $host_ip/$netmask dev $bridgeName
sudo ip link set dev $bridgeName up

