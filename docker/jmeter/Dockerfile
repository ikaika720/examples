FROM ubuntu:precise

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y python-software-properties
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get -y upgrade

RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | /usr/bin/debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y oracle-java8-installer oracle-java8-set-default

RUN wget -q http://www.apache.org/dist/jmeter/binaries/apache-jmeter-2.11.tgz
RUN wget -q https://www.apache.org/dist/jmeter/binaries/apache-jmeter-2.11.tgz.md5
RUN md5sum -c apache-jmeter-2.11.tgz.md5
RUN tar xzf apache-jmeter-2.11.tgz -C /opt && mv /opt/apache-jmeter-* /opt/apache-jmeter
RUN rm /apache-jmeter-*.tgz*

ENV PATH /opt/apache-jmeter/bin:$PATH

