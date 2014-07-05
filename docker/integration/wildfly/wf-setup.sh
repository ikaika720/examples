#!/bin/sh
/opt/wildfly/bin/standalone.sh &
sleep 20
/opt/wildfly/bin/jboss-cli.sh -c -u=admin -p=password --file=/tmp/wf-command.txt
ps -ef | grep java | grep wildfly | awk '{print $2;}' | xargs kill
sleep 10
rm -Rf /opt/wildfly/standalone/configuration/standalone_xml_history
