#!/bin/sh
/wildfly/bin/standalone.sh &
sleep 20
/wildfly/bin/jboss-cli.sh -c -u=admin -p=password --file=wf-command.txt
ps -ef | grep java | grep wildfly | awk '{print $2;}' | xargs kill
sleep 10
rm -Rf /wildfly/standalone/configuration/standalone_xml_history
