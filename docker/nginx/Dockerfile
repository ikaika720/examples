FROM ubuntu:14.10

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install nginx &&\
  apt-get clean && rm -Rf /var/cache/debconf && rm -Rf /var/cache/apt
