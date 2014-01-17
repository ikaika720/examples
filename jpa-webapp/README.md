JPA web application examples
============
This examples use JPA 2.1 and PostgreSQL 9.3.

Access following link by your web browser after deployment of the application.

http://localhost:8080/jpa-webapp/webapi/member/1

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
