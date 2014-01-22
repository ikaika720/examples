FROM ubuntu

RUN apt-get -y install python-software-properties
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get -y install oracle-java7-installer oracle-java7-set-default

RUN apt-get -y install wget
RUN wget -q https://www.apache.org/dist/tomcat/tomcat-7/v7.0.50/bin/apache-tomcat-7.0.50.tar.gz.md5
RUN wget -q http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.50/bin/apache-tomcat-7.0.50.tar.gz
RUN md5sum -c apache-tomcat-7.0.50.tar.gz.md5
RUN tar xzf apache-tomcat-7.0.50.tar.gz

ADD tomcat-users.xml apache-tomcat-7.0.50/conf/tomcat-users.xml

EXPOSE 8009 8080

CMD ["apache-tomcat-7.0.50/bin/catalina.sh","run"]

