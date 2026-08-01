JPA Web Application Example (Spring 7 & Tomcat)
============
This example is based on Jakarta EE 11, JPA 3.2, Spring Framework 7.0.8, Java 21, Tomcat 11, and PostgreSQL 18.4.

## Building
```shell
./gradlew build
```
`jpa-webapp-spring-tomcat.war` will be created in `build/libs`.

## Available API Endpoints
After deploying the WAR application to Apache Tomcat 11, access the following URLs:

- **List All Accounts:**
  `http://localhost:8080/jpa-webapp-spring-tomcat/webapi/account/list`
- **Create Account:**
  `http://localhost:8080/jpa-webapp-spring-tomcat/webapi/account/new?id=100&balance=500.00`
- **Transfer Balance:**
  `http://localhost:8080/jpa-webapp-spring-tomcat/webapi/account/transfer?accountFrom=100&accountTo=200&amount=50.00&sleep=0`
- **List All Transactions:**
  `http://localhost:8080/jpa-webapp-spring-tomcat/webapi/transaction/list`

## PostgreSQL 18.4 Setup

### Step 1. Create the user and database
Connect to PostgreSQL 18.4 as a superuser (e.g., `postgres`) and execute:

```sql
CREATE USER user1 WITH ENCRYPTED PASSWORD 'password';
CREATE DATABASE mydb OWNER user1;
```

### Using Docker
To run PostgreSQL 18.4 with Docker:

```shell
docker run -d --name postgres \
  -e POSTGRES_USER=user1 \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=mydb \
  -p 5432:5432 \
  postgres:18-alpine
```

## Apache Tomcat 11 Setup & Deployment

1. Copy `build/libs/jpa-webapp-spring-tomcat.war` into `$CATALINA_HOME/webapps/`.
2. Start Apache Tomcat 11:
   ```shell
   $CATALINA_HOME/bin/startup.sh
   ```
