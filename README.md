# Rollup Summary

A program that displays a ROLLUP summary of a table

## Overview

* Parses standard input into a table
  * The first input line must consist of space-delimited column names
    * Column names must be unique
    * Column names are case-sensitive
    * The last column is the 'value' column, but can be given any name
  * Each subsequent input line defines a row
    * Values must be tab-delimited
    * The last value must be a number
    * The other values can be any string
* Program arguments (space-delimited) are column names (in order) that define how columns will be grouped for aggregation
  * Must be a subset of the table's column names
  * Can be in any order
  * Cannot include the name of the last (value) column
  * Cannot include duplicates
* Computes a ROLLUP summary view of the table and prints it to standard output

## Running the Program

* Download and install JDK 8 for Linux here: `http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html`
* `cd` to the `Rollup_Summary` directory
* `mkdir bin` (if a `bin` directory doesn't already exist)
* `javac src/*/*.java -d bin`
* `cd bin`
* `java main.Rollup_Summary y m d < input_file`
  * E.g. Use `../../test_data/1_example.txt` as `input_file`, or any other input file in the `test_data` directory

## Testing the Program

* See the `test_data` directory for a set of test input files, and the corresponding expected output (if applicable)
* Create additional test cases by providing different column name argument combinations when running the program

## Next Steps

* Implement a suite of JUnit tests that uses the files in the `test_data` directory to test the program automatically
