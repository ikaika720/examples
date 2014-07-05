FROM ubuntu:14.04

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install -y software-properties-common &&\
  add-apt-repository -y ppa:webupd8team/java &&\
  echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | /usr/bin/debconf-set-selections &&\
  apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y oracle-java8-installer oracle-java8-set-default &&\
  apt-get clean && rm -Rf /var/cache/*

RUN cd /opt &&\
  wget -qO- http://download.jboss.org/wildfly/8.1.0.Final/wildfly-8.1.0.Final.tar.gz | tar xzf - &&\
  mv wildfly-8.1.0.Final wildfly

ADD standalone.xml  /opt/wildfly/standalone/configuration/standalone.xml
ADD mgmt-groups.properties /opt/wildfly/standalone/configuration/mgmt-groups.properties
ADD mgmt-users.properties /opt/wildfly/standalone/configuration/mgmt-users.properties

EXPOSE 4712 4713 8009 8080 8443 9990 9993

CMD ["/opt/wildfly/bin/standalone.sh"]

