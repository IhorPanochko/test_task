commands to check 
1. mvn exec:java@searchWithoutRecursion -Dexec.args="C:\ 3 windows"  where params <rootPath> <depth> <mask>
2. mvn exec:java@searchWithThreads -Dexec.args="C:\ 3 windows"  where params <rootPath> <depth> <mask>
3. mvn exec:java@telnet -Dexec.args="C:\ 8080" where params <rootPath> <serverPort>
