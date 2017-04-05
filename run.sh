#!/bin/bash
export PATH=$PATH:/cygdrive/C/Program\ Files/Java/jdk1.8.0_66/bin/
javac ./src/processLog/*.java
#Feature 1
java -cp ./src processLog.Run "./log_input/log.txt" "./log_output/hosts.txt" "1"
#Feature 2
java -cp ./src processLog.Run "./log_input/log.txt" "./log_output/resources.txt" "2"
#Feature 3
java -cp ./src processLog.Run "./log_input/log.txt" "./log_output/hours.txt" "3"
#Feature 4
java -cp ./src processLog.Run "./log_input/log.txt" "./log_output/blocked.txt" "4"
