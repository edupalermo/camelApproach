export DERBY_HOME=/opt/data/db-derby-10.12.1.1-bin
cd $DERBY_HOME/bin
./setNetworkServerCP
export DERBY_OPTS=-Dderby.system.home=/opt/data/db-derby-10.12.1.1-bin/databases
./startNetworkServer


./ij
# CONNECT 'jdbc:derby://localhost/circuitdb;create=true';
CONNECT 'jdbc:derby://localhost/circuitdb';

select * from APP.schema_version;

SHOW TABLES;


# dont word wrap
tput rmam
# Re-enable tput smam

mvn clean install spring-boot:run -Drun.jvmArguments="-Xmx3G" -Dstart-class=org.genetics.camel.Application
mvn clean install spring-boot:run -Dstart-class=org.genetics.circuit.command.DumpDatabase

mvn spring-boot:run -Drun.jvmArguments="-Xmx512m" -Drun.profiles=dev