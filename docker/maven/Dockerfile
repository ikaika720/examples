FROM ubuntu:14.04

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install -y software-properties-common &&\
  add-apt-repository -y ppa:webupd8team/java &&\
  echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | /usr/bin/debconf-set-selections &&\
  apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y oracle-java8-installer oracle-java8-set-default &&\
  apt-get clean && rm -Rf /var/cache/*

RUN cd /tmp &&\
  wget -q http://www.apache.org/dist/maven/maven-3/3.2.2/binaries/apache-maven-3.2.2-bin.tar.gz &&\
  wget -q https://www.apache.org/dist/maven/maven-3/3.2.2/binaries/apache-maven-3.2.2-bin.tar.gz.md5 &&\
  echo "  apache-maven-3.2.2-bin.tar.gz" | cat apache-maven-3.2.2-bin.tar.gz.md5 - | md5sum -c - &&\
  tar xzf apache-maven-3.2.2-bin.tar.gz -C /opt && mv /opt/apache-maven-* /opt/maven &&\
  rm apache-maven*

ENV M2_HOME /opt/maven
ENV M2 $M2_HOME/bin
ENV PATH $M2:$PATH

