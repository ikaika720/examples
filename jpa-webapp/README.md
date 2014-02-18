JPA web application examples
============
This examples use JPA 2.1 and PostgreSQL 9.3.

Access following link by your web browser after deployment of the application.

 * Create an member
  * http://localhost:8080/jpa-webapp/webapi/member/new?id=1&name=andrew&email=andrew@test.org&dateOfBirth=2014-02-17
 * List all members
  * http://localhost:8080/jpa-webapp/webapi/member/
 * Query a member
  * http://localhost:8080/jpa-webapp/webapi/member/1
 * Update a member
  * http://localhost:8080/jpa-webapp/webapi/member/update?id=1&name=andrew1&email=andrew2@test.org&dateOfBirth=2014-03-17

The last number is the ID of a member. In this case the ID is 1. If there is no record specified, the server returns 404 Not Found.

## Setup the database connection for Glassfish 4
 1. Copy JDBC driver (e.g. postgresql-9.3-1100-jdbc41.jar) to the library directory of a GlassFish domain which you want to host the application (e.g. $GLASSFISH_DIR/glassfish/domains/domain1/lib).
 2. Start or restart GlassFish.
 3. Add a JDBC connection pool.
  * Pool Name: MyDBPool
  * Resource Type: javax.sql.ConnectionPoolDataSource
  * Database Driver Vendor: Postgresql
  * Introspect: Enabled
  * Addtional Properties
   * User: user01
   * DatabaseName: mydb
   * Password: password
 4. Add a JDBC resource
  * JNDI Name: jdbc/MyDB
  * Pool Name: MyDBPool

## WildFly 8.0.0.0.Final memo
Below is assumed standalone mode.

 * Start a server in standalone mode.<br>
   <pre><code>%WF_HOME%\bin\standalone.bat</code></pre>
 * Stop a server in standalone mode.<br>
   Press Ctrl + C.
 * Deploy JDBC Driver<br>
   <pre><code>%WF_HOME%\bin\jboss-cli.bat
   connect localhost
   deploy \path\to\postgresql-9.3-1100-jdbc41.jar</code></pre>
 * Add an admin user<br>
   <pre><code>%WF_HOME%\bin\add-user.bat</code></pre>
 * Set up datasource
  1. Go to http://localhost:9990/
  2. Go Profile -> Subsystems -> Connector -> Datasources, and click on Add.
 * Deploy an application<br>
   <pre><code>%WF_HOME%\bin\jboss-cli.bat
   deploy \path\to\jpa-webapp.war</code></pre>
 * Update an already deployed application<br>
   <pre><code>%WF_HOME%\bin\jboss-cli.bat
   deploy --force \path\to\jpa-webapp.war</code></pre>
   That works, but is this correct?

For further information, read [WildFly 8 Documentation](https://docs.jboss.org/author/display/WFLY8/Documentation).
