#!/bin/sh
cd `dirname $0`
ROOT_PATH=`pwd`
java -Xms256M -Xmx1024M -cp classpath.jar: flockdata.fdataentityinputbean_0_1.FDataEntityInputBean --context=Default "$@" 