#!/bin/bash

psstr=`ps -ef|grep LogParseServer|grep -v 'grep'`
ii=0
server_pid=0
for s0 in $psstr
do
    if [ $ii -eq 1 ]
        then
        server_pid=$s0
    fi
    let ii=ii+1
done
if [ $server_pid -gt 0 ]
then
    kill -9 $server_pid
    echo 'monitor_platform_server killed.'
else
    echo 'monitor_platform_server is not running.'
fi

