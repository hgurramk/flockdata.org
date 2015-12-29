%~d0
cd %~dp0
java -Xms256M -Xmx1024M -cp classpath.jar; fd.fdatafileprocessor_0_1.FDataFileProcessor --context=Default %* 