FROM ubuntu

RUN DEBIAN_FRONTEND=noninteractive apt-get -y install python-software-properties
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive apt-get -y install oracle-java7-installer oracle-java7-set-default

RUN wget -q http://download.jboss.org/wildfly/8.0.0.Final/wildfly-8.0.0.Final.tar.gz
RUN tar xzf wildfly-8.0.0.Final.tar.gz
RUN ln -s wildfly-8.0.0.Final wildfly

ADD standalone.xml  /wildfly/standalone/configuration/standalone.xml
ADD mgmt-groups.properties /wildfly/standalone/configuration/mgmt-groups.properties
ADD mgmt-users.properties /wildfly/standalone/configuration/mgmt-users.properties

EXPOSE 4712 4713 8009 8080 8443 9990 9993

CMD ["/wildfly/bin/standalone.sh"]

