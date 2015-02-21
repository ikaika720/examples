JPA web application examples
============
This examples use JPA 2.1, Spring 4.1, and PostgreSQL 9.4.

Access following link by your web browser after deployment of the application.

http://localhost:8080/jpa-webapp-spring/

## Setup the database connection for Glassfish 4
 1. Copy JDBC driver (e.g. postgresql-9.4-1200-jdbc41.jar) to the library directory of a GlassFish domain which you want to host the application (e.g. $GLASSFISH_DIR/glassfish/domains/domain1/lib).
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

## WildFly 8.2.0.Final memo
Below is assumed standalone mode.

 * Start a server in standalone mode.<br>
   <pre><code>%WF_HOME%\bin\standalone.bat</code></pre>
 * Stop a server in standalone mode.<br>
   Press Ctrl + C.
 * Deploy JDBC Driver<br>
   <pre><code>%WF_HOME%\bin\jboss-cli.bat
   connect localhost
   deploy \path\to\postgresql-9.4-1200.jdbc41.jar</code></pre>
 * Add an admin user<br>
   <pre><code>%WF_HOME%\bin\add-user.bat</code></pre>
 * Set up datasource
  1. Go to http://localhost:9990/
  2. Go Configuration -> Subsystems -> Connector -> Datasources, and click on Add.
 * Deploy an application<br>
   <pre><code>%WF_HOME%\bin\jboss-cli.bat
   deploy \path\to\jpa-webapp.war</code></pre>
 * Update an already deployed application<br>
   <pre><code>%WF_HOME%\bin\jboss-cli.bat
   deploy --force \path\to\jpa-webapp.war</code></pre>
   That works, but is this correct?

For further information, read [WildFly 8 Documentation](https://docs.jboss.org/author/display/WFLY8/Documentation).

## JMeter memo
Use src/test/java/hoge/exp/jpa/DataLoader.java before running src/test/jmeter/performance.jmx.
