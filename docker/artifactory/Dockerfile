FROM ubuntu

RUN apt-get -y install python-software-properties
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get -y install oracle-java7-installer oracle-java7-set-default

#RUN DEBIAN_FRONTEND=noninteractive apt-get -y install unzip net-tools
RUN DEBIAN_FRONTEND=noninteractive apt-get -y install unzip
RUN wget -q http://downloads.sourceforge.net/project/artifactory/artifactory/3.1.0/artifactory-3.1.0.zip
RUN unzip artifactory-3.1.0.zip
#RUN artifactory-3.1.0/bin/installService.sh
#RUN echo JAVA_HOME=/usr/lib/jvm/java-7-oracle >> /etc/opt/jfrog/artifactory/default
RUN sed -i -e 's/\(-Xmx\)[0-9]\+[mgMG]\?/\1128m/' /artifactory-3.1.0/bin/artifactory.default
RUN sed -i -e 's/-Xms[0-9]\+[mgMG]\?//' /artifactory-3.1.0/bin/artifactory.default
RUN sed -i -e 's/\(-XX:MaxPermSize=\)[0-9]\+[mgMG]\?/\1128m/' /artifactory-3.1.0/bin/artifactory.default
RUN sed -i -e 's/-XX:PermSize=[0-9]\+[mgMG]\?//' /artifactory-3.1.0/bin/artifactory.default

EXPOSE 8019 8081

CMD ["/artifactory-3.1.0/bin/artifactory.sh"]
