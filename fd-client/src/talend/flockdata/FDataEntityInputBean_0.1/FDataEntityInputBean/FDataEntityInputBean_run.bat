%~d0
cd %~dp0
java -Xms256M -Xmx1024M -cp classpath.jar; flockdata.fdataentityinputbean_0_1.FDataEntityInputBean --context=Default %* 