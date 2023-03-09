#!/bin/bash
java -Dhttps.protocols=TLSv1.1,TLSv1.2 -cp /opt/app-root/data/script/mymassage-url-0.0.1-SNAPSHOT.jar url.CallUrl "https://mymassage.loro.swiss:8443/mymassage/script/emailDaily.xhtml"
