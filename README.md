# FansiteAnalysis
This is the **Insight Data Engineer Coding Challenge** in April 2017. The goal of this project is to analyze a web server log file and provide the following four features below.

- **Feature 1:** List the top 10 most active host/IP addresses that have accessed the site.
- **Feature 2:** Identify the 10 resources that consume the most bandwidth on the site.
- **Feature 3:** List the top 10 busiest (or most frequently visited) 60-minute periods.
- **Feature 4:** Detect patterns of three failed login attempts from the same IP address over 20 seconds so that all further attempts to the site can be blocked for 5 minutes.

**Dependency:** Java 7

# Data Source
The log data can be downloaded [here](https://drive.google.com/file/d/0B7-XWjN4ezogbUh6bUl1cV82Tnc/view).

# Data Format
The format of log.txt file is assumed to be ASCII. Other format, like UTF-8, may result wrong-data-line-format warning. Each data line follows the pattern `host [dd/MMM/yyyy:HH:mm:ss Z] "request" code(int) bytes(int)`.

# Execution
```
bash run.sh
```
The `run.sh` script will execute the main class `processLog.Run` using the given input file and create four output file for the results of features. The arguments passing into ```processLog.Run``` are ```inputFile```, ```outputFile```, and ```featureLabel (1-4)```. The shell script runs ```processLog.Run``` and load ```inputFile``` for each of the four feature computations.

There is another main class ```processLog.RunAllAtOnce``` which is not executed in ```run.sh```. It loads the input data once and finish computing all four features at the same time. However, this is memory costing and may cause heap space out of memory.

# Test
The test shell script provided by Insight runes testcases in `insight_testsuite` folder. 
```
cd insight_testsuite
bash run_tests.sh
```

# Class and Method Summary
Package processLog

- **Class Run** (main)

  Class Methods
  - `static void main(String[] args)`: Perform feature computation for a specified feature.
  - `ststic void writeInToFile(List<String> result, File outputFile)`: Write result into specified output file.
  - `static void printWarning(List<String> wrongFormat)`: List data lines with wrong format (if any) in the console.
  <br></br>
  
- **Class RunAllAtOnce** (main)

  Class Methods
  - `static void main(String[] args)`: Perform feature computation for All feature at same time (i.e. read through data input file only once).
  - `static void loadData`: Load input file, extract information line by line, and build tables using for computations.   
  - `ststic void writeInToFile(List<String> result, File outputFile)`: Write result into specified output file.
  - `static void printWarning(List<String> wrongFormat)`: list data lines with wrong format (if any) in the console.
  <br></br>
  
- **Class IPFrequency**: Perform Feature 1 computation
  
  Constructor
  - `IPFrequency()`: Construct an empty frequency table.
  - `IPFrequency(InputStreamReader inputFile)`: Construct frequency table using the specified input data file.
  
  Class Methods
  - `void addCount(String host)`: Update the frequency table with a new record.
  - `List<String> getCurrTop(int n)`: Return a list of current top `n` most active host/IP addresses.
  - `List<String> getWrongFormat()`: Return a list of data lines in the input file that have incorrect format.
  <br></br>
  
- **Class ResourceBandwidth**: Perform Feature 2 computation
  
  Constructor
  - `ResourceBandwidth()`: Construct an empty resource consumption table.
  - `ResourceBandwidth(InputStreamReader inputFile)`: Construct resource consumption table using the specified input data file.
  
  Class Methods
  - `void addSource(String resource, Integer bytes)`: Update the resource consumption table with a new record.
  - `List<String> getCurrTop(int n)`: Return a list of current top `n` resources that consume the most bandwidth.
  - `List<String> getWrongFormat()`: Return a list of data lines in the input file that have incorrect format.
  <br></br>
  
- **Class BusyPeriod**: Perform Feature 3 computation
  
  Constructor
  - `BusyPeriod()`: Construct an empty time table.
  - `BusyPeriod(InputStreamReader inputFile)`: Construct the time table using the specified input data file.
  
  Class Methods
  - `void addTimeCount(Long date)`: Update the time table with a new record.
  - `List<String> getBusiest(int n)`: Return a list of current top `n` busiest 60-minute periods.
  - `List<String> getWrongFormat()`: Return a list of data lines in the input file that have incorrect format.
  <br></br>
  
- **Class UserCodeRecord**: Perform Feature 4 computation
  
  Constructor
  - `BusyPeriod()`: Construct an empty host ststus table.
  - `BusyPeriod(InputStreamReader inputFile)`: Construct and update the host status table using the specified input data file.
  
  Class Methods
  - `void updateStatus(String dataLine, String host, int httpCode, Date timeStamp)`: Update the host status table and the blocked list with a new record.
  - `List<String> getBlocked()`: Return a list of blocked requests.
  - `List<String> getWrongFormat()`: Return a list of data lines in the input file that have incorrect format.
