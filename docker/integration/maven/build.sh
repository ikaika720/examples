#!/bin/sh

dbip=172.24.0.11

tar xzf /shared/jpa-webapp-spring.tar.gz
cd jpa-webapp-spring
sed -i -e 's/\(<jta-data-source>\).*\(<\/jta-data-source>\)/\1java:jboss\/datasources\/MyDB\2/' src/main/resources/META-INF/persistence.xml
mvn package
cp target/jpa-webapp-spring*.war /shared

driver=$(find /root/.m2/repository/org/postgresql/postgresql -name 'postgresql-*jdbc*.jar' -type f)
cp $driver /shared

jar cf /shared/jpa-webapp-spring-loader.jar -C target/test-classes .
