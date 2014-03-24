FROM ubuntu:precise

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y python-software-properties
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get -y upgrade

RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | /usr/bin/debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y oracle-java8-installer oracle-java8-set-default

RUN wget -q http://www.apache.org/dist/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz
RUN wget -q https://www.apache.org/dist/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz.md5
RUN echo "  apache-maven-3.2.1-bin.tar.gz" | cat apache-maven-3.2.1-bin.tar.gz.md5 - | md5sum -c -
RUN tar xzf /apache-maven-3.2.1-bin.tar.gz -C /opt && mv /opt/apache-maven-* /opt/apache-maven
RUN rm /apache-maven-*.tar.gz*

ENV M2_HOME /opt/apache-maven
ENV M2 $M2_HOME/bin
ENV PATH $M2:$PATH

