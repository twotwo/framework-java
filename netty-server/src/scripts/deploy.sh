#!/bin/sh

# powered by li3huo.com

# package for delopy
package=target/netty-server-0.1.0-release.tar.gz
# user@host
dest_host=ci@172.27.233.221
# cmd prefix: should install sshpass first
# sshpass -p password ssh ci@172.27.233.221
cmd_prefix="sshpass -p password "

echo "remove old package..."
${cmd_prefix} ssh $dest_host "netty-server-0.1.0/bin/netty.sh stop \
rm -rf netty-server-0.1.0* \ 
ls"
echo "copy new package..."
${cmd_prefix} scp $package ${dest_host}:. 
echo "starting service..."
${cmd_prefix} ssh $dest_host "tar xzf netty-server-0.1.0-release.tar.gz"
${cmd_prefix} ssh $dest_host "netty-server-0.1.0/bin/netty.sh start"