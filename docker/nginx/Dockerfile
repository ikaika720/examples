FROM ubuntu:14.10

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install wget &&\
  wget -qO- http://nginx.org/keys/nginx_signing.key | apt-key add - &&\
  echo "deb http://nginx.org/packages/ubuntu/ trusty nginx" >> /etc/apt/sources.list &&\
  apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install nginx &&\
  apt-get clean && rm -Rf /var/cache/debconf && rm -Rf /var/cache/apt
