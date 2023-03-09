﻿#!/bin/bash -e
#
echo "Building maven artifacts..." && cd /opt/app-root/application/mymassage-url/ && mvn -e clean install && cp -Rf /opt/app-root/application/mymassage-url/target/mymassage-url-0.0.1-SNAPSHOT.jar /loro/data/script && cd /opt/app-root/application/mymassage-util/ && mvn clean install && cd /opt/app-root/application//mymassage-ejb/ && mvn clean install && cd /opt/app-root/application//mymassage-war/ && mvn clean install && cd /opt/app-root/application//mymassage-ear/ && mvn clean install && cp -Rf /opt/app-root/application/mymassage-ear/target/mymassage-ear-0.0.1-SNAPSHOT.ear /opt/app-root && echo "Ends maven artifacts building..."