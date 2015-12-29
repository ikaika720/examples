JPA Message Driven Bean example
============
This example is based on Java EE 7, JPA 2.1 and JMS 2.0. This works with Wildfly 9.0 and PostgreSQL 9.4.

## Building
```shell
gradle ear
```
`jpa-mdb.ear` should be created in `jpa-mdb-ear/build/libs`.

## Sending a request to the MDB using the client
```shell
$ gradle jpa-mdb-client:distTar
$ cd jpa-mdb-client/build/distributions
$ tar xf jpa-mdb-client.tar
$ cd jpa-mdb-client/bin
$ ./jpa-mdb-client
Dec 21, 2015 3:01:56 AM org.xnio.Xnio <clinit>
INFO: XNIO version 3.3.1.Final
Dec 21, 2015 3:01:56 AM org.xnio.nio.NioXnio <clinit>
INFO: XNIO NIO Implementation Version 3.3.1.Final
Dec 21, 2015 3:01:57 AM org.jboss.remoting3.EndpointImpl <clinit>
INFO: JBoss Remoting version 4.0.9.Final
{"id"=1,"balance"=100.00,"lastUpdated"="2015-12-21T01:25:19.457+0900"}
```
`jpa-mdb-client` accepts accountId and amount. `jpa-mdb-client 1 -10` will decrease the balance of account #1.

## WildFly 9.0 memo

 * Start a server in standalone-full mode. The messaging feature is include in the full mode.<br>
   <pre><code>$WF_HOME/bin/standalone.sh -c standalone-full.xml</code></pre>
 * Stop a server in standalone mode.<br>
   Press Ctrl + C.
 * Configure PostgreSQL JDBC Driver<br>
   <pre><code>$WF_HOME/bin/jboss-cli.sh
   connect
   module add \
     --name=org.postgresql \
     --slot=main \
     --resources=/path/to/postgresql-9.4-1206-jdbc42.jar \
     --dependencies=javax.api,javax.transaction.api
   /subsystem=datasources/jdbc-driver=postgresql:add(driver-name="postgresql",driver-module-name="org.postgresql",driver-class-name=org.postgresql.Driver,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)</code></pre>
 * Add an admin and an application user<br>
   <pre><code>$WF_HOME/bin/add-user.sh</code></pre>
   This example requires an application user belongs to group 'guest'. The default of this example is 'user1'.
 * Set up datasource
   <pre><code>$WF_HOME/bin/jboss-cli.sh
   xa-data-source add \
     --name=MyDB \
     --driver-name=postgresql \
     --jndi-name=java:jboss/datasources/MyDB \
	 --xa-datasource-properties=ServerName=localhost,PortNumber=5432,DatabaseName=mydb \
     --user-name=user1 \
     --password=password \
     --max-pool-size=25 \
     --blocking-timeout-wait-millis=3000 \
     --enabled=true</code></pre>
 * Deploy an application<br>
   <pre><code>$WF_HOME/bin/jboss-cli.sh
   deploy /path/to/jpa-mdb.ear</code></pre>
 * Update an already deployed application<br>
   <pre><code>$WF_HOME/bin/jboss-cli.sh
   deploy /path/to/jpa-mdb.ear --force</code></pre>

For further information, read [WildFly 9 Documentation](https://docs.jboss.org/author/display/WFLY9/Documentation).

## PostgreSQL memo
### Creating the database and table
Use `jpa-mdb-ejbjar/src/main/sql/*.sql`.

### Configuring for XA transactions
Set `max_prepared_transactions` in `postgresql.conf`.
