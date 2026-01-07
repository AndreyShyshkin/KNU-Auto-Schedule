#!/bin/bash
mkdir -p out/production/schedule
# Find all java files
find backend -name "*.java" > sources.txt
# Download postgresql driver if not exists (mocking this step as I cannot download, assuming user has it or I assume libs folder)
# For now, I'll just try to compile without the driver to check syntax errors, 
# BUT the DatabaseService requires the driver in classpath.
# So I will assume the user followed README and has the driver or uses Maven.

# Using javac assuming standard project structure
javac -source 8 -target 8 -d out/production/schedule @sources.txt
