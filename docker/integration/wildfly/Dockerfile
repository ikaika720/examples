FROM inabatk/wildfly

RUN wget -P /tmp -q http://jdbc.postgresql.org/download/postgresql-9.3-1102.jdbc41.jar

ADD jpa-webapp-spring.war /tmp/jpa-webapp-spring.war
ADD wf-command.txt /tmp/wf-command.txt
ADD wf-setup.sh /tmp/wf-setup.sh
RUN cd /tmp && ./wf-setup.sh && rm -Rf *

