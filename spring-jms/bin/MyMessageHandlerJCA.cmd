set M2_REPO=%USERPROFILE%\.m2\repository

set CLASSPATH=..\target\classes
set CLASSPATH=%CLASSPATH%;%M2_REPO%\aopalliance\aopalliance\1.0\aopalliance-1.0.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\commons-logging\commons-logging\1.1.1\commons-logging-1.1.1.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\apache\activemq\activemq-broker\5.9.0\activemq-broker-5.9.0.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\apache\activemq\activemq-client\5.9.0\activemq-client-5.9.0.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\apache\activemq\activemq-ra\5.9.0\activemq-ra-5.9.0.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\apache\geronimo\specs\geronimo-j2ee-management_1.1_spec\1.0.1\geronimo-j2ee-management_1.1_spec-1.0.1.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\apache\geronimo\specs\geronimo-jms_1.1_spec\1.1.1\geronimo-jms_1.1_spec-1.1.1.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\apache\geronimo\specs\geronimo-j2ee-connector_1.5_spec\2.0.0\geronimo-j2ee-connector_1.5_spec-2.0.0.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\fusesource\hawtbuf\hawtbuf\1.9\hawtbuf-1.9.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\slf4j\slf4j-api\1.7.5\slf4j-api-1.7.5.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-aop\4.0.0.RELEASE\spring-aop-4.0.0.RELEASE.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-beans\4.0.0.RELEASE\spring-beans-4.0.0.RELEASE.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-context\4.0.0.RELEASE\spring-context-4.0.0.RELEASE.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-core\4.0.0.RELEASE\spring-core-4.0.0.RELEASE.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-expression\4.0.0.RELEASE\spring-expression-4.0.0.RELEASE.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-jms\4.0.0.RELEASE\spring-jms-4.0.0.RELEASE.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-tx\4.0.0.RELEASE\spring-tx-4.0.0.RELEASE.jar

java hoge.exp.spring_jms.MyMessageHandlerJCA
