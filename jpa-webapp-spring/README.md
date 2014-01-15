JPA web application examples
============
This examples use JPA 2.1, Spring 4.0, Hibernate 4.3 and PostgreSQL 9.3.

Access following link by your web browser after deployment of the application.

http://localhost:8080/jpa-webapp/webapi/member/1

The last number is the ID of a member. In this case the ID is 1. If there is no record specified, the server returns 404 Not Found.

## Setup the database connection for Glassfish 4
 1. Copy JDBC driver (e.g. postgresql-9.3-1100-jdbc41.jar) to the library directory of a GlassFish domain which you want to host the application (e.g. $GLASSFISH_DIR/glassfish/domains/domain1/lib).
 2. Copy Log4J library (e.g. log4j-1.2.17.jar) to the library directory of the GlassFish (e.g. $GLASSFISH_DIR/glassfish/lib).
 3. Copy src/main/log4j/lo4j.xml to the configuration directory of the GlassFish domain (e.g. $GLASSFISH_DIR/glassfish/domains/domain1/config)
 4. Start or restart GlassFish.
 5. Access the GlassFish administration console (http://localhost:4848/).
 6. Add a JDBC connection pool. Resources -> JDBC -> JDBC Connection Pools. Click on "New...".
  * Pool Name: MyDBPool
  * Resource Type: javax.sql.ConnectionPoolDataSource
  * Database Driver Vendor: Postgresql
  * Introspect: Enabled
  * Addtional Properties
   * User: user01
   * DatabaseName: mydb
   * Password: password
   * Url: (add the database name. e.g. jdbc:postgresql://localhost/mydb?...)
 6. Add a JDBC resource. Resources -> JDBC -> JDBC Resources. Click on "New...".
  * JNDI Name: jdbc/MyDB
  * Pool Name: MyDBPool
 7. Add an JVM option for Log4J. Configurations -> server-config -> JVM Settings. Choose "JVM Options" tab. Click on "Add JVM Option". Specify `-Dlog4j.configuration=file:///${com.sun.aas.instanceRoot}/config/log4j.xml`.
 8. Restart GlassFish.

Read [Configure Log4J for use in GlassFish 3.1](https://blogs.oracle.com/naman/entry/configure_log4j_for_use_in) on Log4J in GlassFish.
