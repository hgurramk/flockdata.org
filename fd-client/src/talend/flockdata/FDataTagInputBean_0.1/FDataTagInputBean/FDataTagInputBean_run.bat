%~d0
cd %~dp0
java -Xms256M -Xmx1024M -cp classpath.jar; flockdata.fdatataginputbean_0_1.FDataTagInputBean --context=Default %* 