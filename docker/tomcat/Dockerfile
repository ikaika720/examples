FROM ubuntu

RUN DEBIAN_FRONTEND=noninteractive apt-get -y install python-software-properties
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive apt-get -y install oracle-java7-installer oracle-java7-set-default

RUN wget -q https://archive.apache.org/dist/tomcat/tomcat-7/v7.0.52/bin/apache-tomcat-7.0.52.tar.gz.md5
RUN wget -q http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.52/bin/apache-tomcat-7.0.52.tar.gz
RUN md5sum -c apache-tomcat-7.0.52.tar.gz.md5
RUN tar xzf apache-tomcat-7.0.52.tar.gz -C /opt
RUN rm /apache-tomcat-*.tar.gz*
RUN ln -s /opt/apache-tomcat-7.0.52 /opt/apache-tomcat

ADD tomcat-users.xml /opt/apache-tomcat-7.0.52/conf/tomcat-users.xml

EXPOSE 8009 8080

CMD ["/opt/apache-tomcat/bin/catalina.sh","run"]

