# FansiteAnalysis
This is the Insight Data Engineer Coding Challenge in April 2017. The goal of this project is to analyze a web server log file and provide the following four features below.

Feature 1: List the top 10 most active host/IP addresses that have accessed the site.
Feature 2: Identify the 10 resources that consume the most bandwidth on the site.
Feature 3: List the top 10 busiest (or most frequently visited) 60-minute periods.
Feature 4: Detect patterns of three failed login attempts from the same IP address over 20 seconds so that all further attempts to the site can be blocked for 5 minutes.

Dependency: Java 7

# Exectuion
The run.sh script will execute the main class processLog.Run using the given input file and create four output file recording features. Note that the arguments passing into the main class processLog.Run are “input ,” “output,” and “feature label (1-4).” The shell script runs  processLog.Run and load input file for each feature calculation. 
There is another class processLog.RunAllAtOnce which will load the input data once and finish computing all four features at the same time. However, this is memory costing. 


