# Rollup Summary

A program that dislays a ROLLUP summary of a table

## Overview

* Parses standard input into a table
  * The first input line must consist of space-delimited column names
  * Each subsequent input line defines a row
    * Values must be tab-delimited
    * The last value must be a number
    * The other values can be any string
* Accepts space-delimited column name arguments that define how columns will be grouped for aggregation
  * Column names must be a subset of the table's column names
  * Cannot include the name of the last (value) column
  * Cannot include duplicates
* Computes and prints a ROLLUP summary view of the table to standard output

## Running the Program

* Download and install JDK 8 for Linux here: `http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html`
* `cd` to the `Rollup_Summary` directory
* `javac *.java`
* `java Rollup_Summary y m d < input_file`
  * See the `test_data` directory for several example input files that can be used
  
## Testing the Program

* See the `test_data` directory for many test input files, and the corresponding expected output (if applicable)
* Be sure to provide different column name arguments to create additional test cases

## Next Steps

* Implement a suite of JUnit tests that use the test files in the `test_data` directory to test the program automatically
