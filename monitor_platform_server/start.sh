#!/bin/bash

old_path=`pwd`
sh_path=`dirname $0`
cd $sh_path

cls_path=

for jar_name in lib/*.jar; do
    cls_path=$cls_path:$jar_name   
done

rm -rf nohup.out
nohup java -Xmx4G -classpath $cls_path com.renren.wan.logparse.LogParseServer 7990 > nohup.out 2>&1 &

until [ -s nohup.out ]

do
    sleep 1
done

cat nohup.out
 
cd $old_path
