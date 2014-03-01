FROM inabatk/wildfly

RUN wget -q http://jdbc.postgresql.org/download/postgresql-9.3-1101.jdbc41.jar

ADD jpa-webapp-spring.war jpa-webapp-spring.war
ADD wf-command.txt wf-command.txt
ADD wf-setup.sh wf-setup.sh
RUN chmod +x /wf-setup.sh; /wf-setup.sh
