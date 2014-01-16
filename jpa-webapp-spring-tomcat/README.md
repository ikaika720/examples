An example of JPA, Spring, Hibernate, PostgreSQL and Tomcat
============
This example uses JPA 2.1, Spring 4.0, Hibernate 4.3 and PostgreSQL 9.3 and Tomcat 7.0.

Access following link by your web browser after deployment of the application.

http://localhost:8080/jpa-webapp-spring-tomcat/

## Tomcat set up

Download spring-instrument-tomcat.jar from http://repo1.maven.org/maven2/org/springframework/spring-instrument-tomcat/4.0.0.RELEASE/spring-instrument-tomcat-4.0.0.RELEASE.jar.

Copy the jar file into $CATALINA_HOME/lib.

Read Tomcat section of [Environment-specific configuration](http://docs.spring.io/spring/docs/4.0.0.RELEASE/spring-framework-reference/htmlsingle/#aop-aj-ltw-environments) of Spring's reference.

## Memo
 * `@PrePersist` and `@PreUpdate` are used to update the time stamp of `Account`.
