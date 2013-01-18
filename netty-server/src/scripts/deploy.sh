#!/bin/sh

package=target/netty-server-0.1.0-release.tar.gz
dest=172.27.234.152
#export SSHPASS=mypassword
echo "remove old package..."
sshpass -e ssh $dest "netty-server-0.1.0/bin/netty.sh stop \
rm -rf netty-server-0.1.0* \ 
ls"
echo "copy new package..."
sshpass -e scp $package ${dest}:. 
echo "starting service..."
sshpass -e ssh $dest "tar xzf netty-server-0.1.0-release.tar.gz"
sshpass -e ssh $dest "netty-server-0.1.0/bin/netty.sh start"