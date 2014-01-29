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

RUN wget -q http://www.apache.org/dist/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.tar.gz
RUN wget -q https://www.apache.org/dist/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.tar.gz.md5
RUN echo "  apache-maven-3.1.1-bin.tar.gz" | cat apache-maven-3.1.1-bin.tar.gz.md5 - | md5sum -c -
RUN tar xzf /apache-maven-3.1.1-bin.tar.gz

ENV M2_HOME /apache-maven-3.1.1
ENV M2 $M2_HOME/bin
ENV PATH $M2:$PATH
