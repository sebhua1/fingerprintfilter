# fingerprintfilter

Parses PANs XML files from the no-plagiarism and no-obfuscation folder. Creates a list of objects with relevant information
to work with.
Extracts relevant text files and substrings. Filters them and let Sherlock run a plagiarism check. 
Classifies Sherlocks results and calculates precision and recall.

How to make the program run:
Delete the "pairs" file in PANs "01-no-plagiarism" and "02-no-obfuscation" folders
For Linux: delete ".DS_Store" both folders via terminal
Insert relevant directories
Use Sherlock by V.Stange available on git
Set zerobits and chainlength
Choose 1 of 3 filters by commenting out the other 2. Unfiltered: comment out all filters
