export DERBY_HOME=/opt/data/db-derby-10.12.1.1-bin
cd $DERBY_HOME/bin
./setNetworkServerCP
export DERBY_OPTS=-Dderby.system.home=/opt/data/db-derby-10.12.1.1-bin/databases
./startNetworkServer


./ij
CONNECT 'jdbc:derby://localhost/circuitdb;create=true';

SHOW TABLES;