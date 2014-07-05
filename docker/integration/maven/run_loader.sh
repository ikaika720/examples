#!/bin/sh

dbip=172.24.0.11

driver=$(find /shared -name 'postgresql-*jdbc*.jar' -type f)

java -cp /shared/jpa-webapp-spring-loader.jar:${driver} hoge.exp.jpa.DataLoader 10000 jdbc:postgresql://${dbip}/mydb user01 password

