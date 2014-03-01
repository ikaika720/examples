#!/bin/sh

tar xzf /shared/jpa-webapp-spring.tar.gz
cd jpa-webapp-spring
sed -i -e 's/\(<jta-data-source>\).*\(<\/jta-data-source>\)/\1java:jboss\/datasources\/MyDB\2/' src/main/resources/META-INF/persistence.xml
mvn package -DskipTests=true
cp target/jpa-webapp-spring*.war /shared

