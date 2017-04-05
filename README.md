# FansiteAnalysis
This is the **Insight Data Engineer Coding Challenge** in April 2017. The goal of this project is to analyze a web server log file and provide the following four features below.

- **Feature 1:** List the top 10 most active host/IP addresses that have accessed the site.
- **Feature 2:** Identify the 10 resources that consume the most bandwidth on the site.
- **Feature 3:** List the top 10 busiest (or most frequently visited) 60-minute periods.
- **Feature 4:** Detect patterns of three failed login attempts from the same IP address over 20 seconds so that all further attempts to the site can be blocked for 5 minutes.

**Dependency:** Java 7

# Exectuion
```
bash run.sh
```
The `run.sh` script will execute the main class `processLog.Run` using the given input file and create four output file for the results of features. The arguments passing into ```processLog.Run``` are ```inputFile```, ```outputFile```, and ```featureLabel (1-4)```. The shell script runs ```processLog.Run``` and load ```inputFile``` for each of the four feature computations.

There is another main class ```processLog.RunAllAtOnce``` which is not covered in ```run.sh```. It loads the input data once and finish computing all four features at the same time. However, this is memory costing and may cause heap space out of memory.

# Test
The test shell script provided by Insight runes testcases in `insight_testsuite` folder. 
```
cd insight_testsuite
bash run_tests.sh
```

# Classes and Methods



