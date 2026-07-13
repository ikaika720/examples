package hoge.exp.jpa.mdb.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class WildFlyIntegrationTest {

        public static Network network = Network.newNetwork();

        @AfterAll
        static void tearDown() {
                network.close();
        }

        @Container
        @SuppressWarnings("resource")
        public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
                        .withNetwork(network)
                        .withNetworkAliases("postgres")
                        .withDatabaseName("mydb")
                        .withUsername("user1")
                        .withPassword("password")
                        .withCommand("-c", "max_prepared_transactions=100");

        @Container
        @SuppressWarnings("resource")
        public static GenericContainer<?> wildfly = new GenericContainer<>("quay.io/wildfly/wildfly:40.0.1.Final-jdk21")
                        .dependsOn(postgres)
                        .withNetwork(network)
                        .withCreateContainerCmdModifier(cmd -> {
                                cmd.withHostName("127.0.0.1");
                                com.github.dockerjava.api.model.HostConfig hostConfig = cmd.getHostConfig();
                                if (hostConfig == null)
                                        hostConfig = new com.github.dockerjava.api.model.HostConfig();
                                hostConfig.withPortBindings(
                                                new com.github.dockerjava.api.model.PortBinding(
                                                                com.github.dockerjava.api.model.Ports.Binding
                                                                                .bindPort(8080),
                                                                new com.github.dockerjava.api.model.ExposedPort(8080)));
                                cmd.withHostConfig(hostConfig);
                        })
                        .withCommand("/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-c",
                                        "standalone-full.xml")
                        .waitingFor(Wait.forLogMessage(".*WFLYSRV0025.*", 1));

        @Test
        void testMessageExchange() throws Exception {
                // Setup database schema and initial data
                setupDatabase();

                // Run CLI commands to configure WildFly after it starts
                wildfly.execInContainer("/opt/jboss/wildfly/bin/add-user.sh", "-a", "-u", "user1", "-p", "password",
                                "-g", "guest");

                // Install PostgreSQL Driver
                wildfly.copyFileToContainer(
                                org.testcontainers.utility.MountableFile
                                                .forHostPath(new File("build/postgresql/postgresql.jar").toPath()),
                                "/tmp/postgresql.jar");

                var modResult = wildfly.execInContainer("/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=module add --name=org.postgresql --slot=main --resources=/tmp/postgresql.jar --dependencies=jakarta.transaction.api");
                if (modResult.getExitCode() != 0)
                        throw new RuntimeException(
                                        "module add failed: " + modResult.getStdout() + modResult.getStderr());

                var drvResult = wildfly.execInContainer("/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=\"postgresql\",driver-module-name=\"org.postgresql\",driver-class-name=org.postgresql.Driver,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)");
                if (drvResult.getExitCode() != 0)
                        throw new RuntimeException(
                                        "jdbc-driver add failed: " + drvResult.getStdout() + drvResult.getStderr());

                // Add XA Datasource pointing to PostgreSQL container
                org.testcontainers.containers.Container.ExecResult dsResult = wildfly.execInContainer(
                                "/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=xa-data-source add --name=MyDB --driver-name=postgresql --jndi-name=java:jboss/datasources/MyDB --xa-datasource-properties=ServerName=postgres,PortNumber=5432,DatabaseName=mydb --user-name=user1 --password=password --max-pool-size=25 --blocking-timeout-wait-millis=3000 --enabled=true");
                if (dsResult.getExitCode() != 0) {
                        throw new RuntimeException("Failed to add XA Datasource.\nStdout: " + dsResult.getStdout()
                                        + "\nStderr: " + dsResult.getStderr());
                }

                var testConn = wildfly.execInContainer("/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=/subsystem=datasources/xa-data-source=MyDB:test-connection-in-pool");
                if (testConn.getExitCode() != 0) {
                        throw new RuntimeException("Failed to connect to XA Datasource.\nStdout: "
                                        + testConn.getStdout() + "\nStderr: " + testConn.getStderr());
                }
                wildfly.execInContainer("/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=jms-queue add --queue-address=MyReqQueue --entries=java:/jms/queue/MyReqQueue");
                wildfly.execInContainer("/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=jms-queue add --queue-address=MyResQueue --entries=java:/jms/queue/MyResQueue");
                // Copy EAR to a temporary location to prevent scanner race conditions
                wildfly.copyFileToContainer(
                                org.testcontainers.utility.MountableFile
                                                .forHostPath(new File("../jpa-mdb-ear/build/libs/jpa-mdb.ear")
                                                                .toPath()),
                                "/tmp/jpa-mdb.ear");

                // Deploy EAR using CLI (blocks until deployment is complete)
                System.err.println("Deploying EAR via jboss-cli...");
                org.testcontainers.containers.Container.ExecResult deployResult = wildfly.execInContainer(
                                "/opt/jboss/wildfly/bin/jboss-cli.sh", "--connect",
                                "--command=deploy /tmp/jpa-mdb.ear");

                if (deployResult.getExitCode() != 0) {
                        System.err.println("EAR deployment FAILED!");
                        System.err.println("CLI Output: " + deployResult.getStdout());
                        System.err.println("CLI Error: " + deployResult.getStderr());
                        System.err.println("WildFly Logs:");
                        System.err.println(wildfly.getLogs());
                        throw new RuntimeException("Deployment failed: " + deployResult.getStderr());
                }
                System.err.println("EAR successfully deployed.");

                String host = "127.0.0.1";
                Integer port = 8080;
                String providerUrl = "remote+http://" + host + ":" + port;

                // Configure system properties used by Client.java
                System.setProperty("java.naming.provider.url", providerUrl);

                // Capture stdout
                PrintStream originalOut = System.out;
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                System.setOut(new PrintStream(outContent));

                try {
                        // Run client with accountId=1 and amount=-10
                        Client.main(new String[] { "1", "-10" });

                        // The client prints the updated account info, let's parse it or assert it
                        String output = outContent.toString();
                        // Expected something like: {"id"=1,"balance"=90.00,"lastUpdated"=...}
                        System.err.println("Client output: " + output);
                        assertTrue(output.contains("\"id\"=1"), "Output should contain account id");
                        assertTrue(output.contains("\"balance\"=90.00"), "Balance should have decreased by 10");
                } finally {
                        System.setOut(originalOut);
                }
        }

        private static void setupDatabase() throws Exception {
                String jdbcUrl = postgres.getJdbcUrl();
                String username = postgres.getUsername();
                String password = postgres.getPassword();

                try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
                     Statement stmt = conn.createStatement()) {
                        // Execute table creation from DDL file
                        String ddl = Files.readString(Paths.get("../jpa-mdb-ejbjar/src/main/sql/create_table_postgresql.sql"));
                        stmt.execute(ddl);

                        // Execute initial data insertion from SQL file
                        String dml = Files.readString(Paths.get("../jpa-mdb-ejbjar/src/main/resources/META-INF/import.sql"));
                        stmt.execute(dml);
                }
        }
}
