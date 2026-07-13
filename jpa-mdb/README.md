JPA Message Driven Bean example
============
This example is based on Jakarta EE 11, JPA 3.2 and JMS 3.1. This works with WildFly 40 and PostgreSQL (or built-in H2 for tests).

## Building
```shell
./gradlew build
```
`jpa-mdb.ear` will be created in `jpa-mdb-ear/build/libs`.

## Sending a request to the MDB using the client
```shell
$ ./gradlew :jpa-mdb-client:distTar
$ cd jpa-mdb-client/build/distributions
$ tar xf jpa-mdb-client.tar
$ cd jpa-mdb-client/bin
$ ./jpa-mdb-client
INFO: WildFly Naming version 2.0.1.Final
INFO: ELY00001: WildFly Elytron version 2.9.0.Final
INFO: XNIO version 3.8.16.Final
INFO: XNIO NIO Implementation Version 3.8.16.Final
INFO: JBoss Remoting version 5.0.31.Final
{"id"=1,"balance"=100.00,"lastUpdated"="2026-07-11T13:25:19.457+0900"}
```
The client script accepts two arguments:
```shell
./jpa-mdb-client <accountId> [<amount>]
```

- **Query Account:** Specify only the `<accountId>` to retrieve the current balance.
  ```shell
  $ ./jpa-mdb-client 1
  {"id"=1,"balance"=100.00,"lastUpdated"="2026-07-12T22:33:52.472"}
  ```

- **Update Balance:** Specify both `<accountId>` and `<amount>` to adjust the balance.
  ```shell
  $ ./jpa-mdb-client 1 -10
  {"id"=1,"balance"=90.00,"lastUpdated"="2026-07-12T22:35:10.891"}
  ```

## Automated Testing
Testcontainers is used to spin up a WildFly 40 container automatically for integration testing:
```shell
./gradlew test
```

### Colima Users
If you are using Colima on macOS, you may need to set the following environment variables before running the tests:
```shell
export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"
export TESTCONTAINERS_RYUK_DISABLED="true"
./gradlew test
```

## WildFly 40 Setup

Follow these steps in order to set up WildFly 40 for this example.

### Step 1. Add an application user (server stopped)

This does not require the server to be running.

```shell
$WF_HOME/bin/add-user.sh -a -u user1 -p password -g guest
```

### Step 2. Install the PostgreSQL JDBC driver (server stopped)

`module add` is a file-system operation and does not require the server to be running.

```shell
$WF_HOME/bin/jboss-cli.sh \
  --command="module add --name=org.postgresql --slot=main \
    --resources=/path/to/postgresql-42.7.13.jar \
    --dependencies=jakarta.transaction.api"
```

### Step 3. Start the server

Start the server in **standalone-full** mode (includes JMS messaging subsystem). Keep this terminal open.

```shell
$WF_HOME/bin/standalone.sh -c standalone-full.xml
```

Wait until you see `WFLYSRV0025: WildFly ... started` in the log before proceeding.

### Step 4. Register the JDBC driver (server running)

The following steps require the server to be running. Open a **new terminal** and run:

```shell
$WF_HOME/bin/jboss-cli.sh --connect \
  --command='/subsystem=datasources/jdbc-driver=postgresql:add( \
    driver-name="postgresql", \
    driver-module-name="org.postgresql", \
    driver-class-name=org.postgresql.Driver, \
    driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)'
```

### Step 5. Add the XA datasource (server running)

```shell
$WF_HOME/bin/jboss-cli.sh --connect \
  --command="xa-data-source add --name=MyDB \
    --driver-name=postgresql \
    --jndi-name=java:jboss/datasources/MyDB \
    --xa-datasource-properties=ServerName=localhost,PortNumber=5432,DatabaseName=mydb \
    --user-name=user1 --password=password \
    --max-pool-size=25 --blocking-timeout-wait-millis=3000 \
    --enabled=true"
```

### Step 6. Add JMS queues (server running)

```shell
$WF_HOME/bin/jboss-cli.sh --connect \
  --command="jms-queue add --queue-address=MyReqQueue --entries=java:/jms/queue/MyReqQueue"
$WF_HOME/bin/jboss-cli.sh --connect \
  --command="jms-queue add --queue-address=MyResQueue --entries=java:/jms/queue/MyResQueue"
```

### Step 7. Deploy the application (server running)

```shell
$WF_HOME/bin/jboss-cli.sh --connect --command="deploy /path/to/jpa-mdb.ear"
```

To update an already deployed application:
```shell
$WF_HOME/bin/jboss-cli.sh --connect --command="deploy /path/to/jpa-mdb.ear --force"
```

### Stopping the server

Press Ctrl + C in the terminal where the server is running.

### Reloading the server

If a CLI command outputs `process-state: reload-required`, the configuration change has not yet taken effect. Run the following to apply it without restarting:

```shell
$WF_HOME/bin/jboss-cli.sh --connect --command="reload"
```

For further information, read [WildFly Documentation](https://docs.wildfly.org/).

## PostgreSQL Setup

### Step 1. Create the user and database

Connect as a superuser (e.g. `postgres`) and run:

```sql
CREATE USER user1 WITH ENCRYPTED PASSWORD 'password';
CREATE DATABASE mydb OWNER user1;
```

> **Note:** The database owner must be `user1` so that Hibernate's `drop-and-create` schema generation can create tables in the `public` schema (PostgreSQL 15+ restricts this to the database owner).

### Step 2. Configure for XA transactions

Set `max_prepared_transactions` in `postgresql.conf` (requires a restart):

```
max_prepared_transactions = 100
```

### Using Docker

To run PostgreSQL with Docker (XA-ready):

```shell
docker run -d --name postgres \
  -e POSTGRES_USER=user1 \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=mydb \
  -p 5432:5432 \
  postgres:18-alpine \
  -c max_prepared_transactions=100
```

### Creating tables manually

If not using Hibernate's `schema-generation` property in `persistence.xml`, create the table manually using `jpa-mdb-ejbjar/src/main/sql/*.sql`.
