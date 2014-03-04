Docker multiple container integration example
===============
This example uses three containers - 1) database (PostgreSQL), 2) Java application server (Wildfly), 3) virtual client (JMeter).

  JMeter -> Wildfly (172.24.0.41:8080)-> PostgreSQL (172.24.0.11:5432)

To run this example, run bin/start.sh.

This uses [pipework](https://github.com/jpetazzo/pipework) to define the network.

To view the web application pages by your web browser, configure ssh tunnel to the application server.

(putty memo)<br>
 1. Connect to the VirtualBox VM where the containers run by putty.
 2. Right click on the title bar of the terminal, "Change Settings..." -> "Connection" -> "SSH" -> "Tunnels".
  * Source port: 18080
  * Destination: 172.24.0.41:8080
 3. Press "Add" and "Apply".
 3. http://localhost:18080/jpa-webapp-spring/

