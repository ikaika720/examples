FROM ubuntu:precise

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y python-software-properties
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get -y upgrade

RUN echo "oracle-java7-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y oracle-java7-installer
RUN update-alternatives --display java

ENV JAVA_HOME /usr/lib/jvm/java-7-oracle
ENV PATH $JAVA_HOME/bin:$PATH

RUN wget -q http://www.apache.org/dist/jmeter/binaries/apache-jmeter-2.11.tgz
RUN wget -q https://www.apache.org/dist/jmeter/binaries/apache-jmeter-2.11.tgz.md5
RUN md5sum -c apache-jmeter-2.11.tgz.md5
RUN tar xzf apache-jmeter-2.11.tgz
RUN rm apache-jmeter-2.11.tgz
RUN ln -s /apache-jmeter-2.11 /apache-jmeter

ENV PATH /apache-jmeter/bin:$PATH

