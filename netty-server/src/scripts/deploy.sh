#!/bin/sh

package=target/netty-server-0.1.0-release.tar.gz
dest=172.27.234.152
pre_call="sshpass -f ~/.passwd"

#export SSHPASS=mypassword
echo "remove old package..."
sshpass -f ~/.passwd ssh $dest "netty-server-0.1.0/bin/netty.sh stop \
rm -rf netty-server-0.1.0* \ 
ls"
echo "copy new package..."
sshpass -f ~/.passwd scp $package ${dest}:. 
echo "starting service..."
sshpass -f ~/.passwd ssh $dest "tar xzf netty-server-0.1.0-release.tar.gz"
sshpass -f ~/.passwd ssh $dest "netty-server-0.1.0/bin/netty.sh start"