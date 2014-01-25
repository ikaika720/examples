FROM ubuntu:precise

RUN apt-get -y install wget
RUN wget -qO- http://nginx.org/keys/nginx_signing.key | apt-key add -
RUN echo "deb http://nginx.org/packages/ubuntu/ precise nginx" >> /etc/apt/sources.list
RUN apt-get update && apt-get -y install nginx
