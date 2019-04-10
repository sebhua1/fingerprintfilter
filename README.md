# fingerprintfilter

Parses PANs XML files from the no-plagiarism and no-obfuscation folder. Creates a list of objects with relevant information
to work with.
Extracts relevant text files and substrings. Filters them and let Sherlock run a plagiarism check. 
Classifies Sherlocks results and calculates precision and recall.

How to make the program run:

1. Delete the "pairs" file in PANs "01-no-plagiarism" and "02-no-obfuscation" folders
2. For Linux: delete ".DS_Store" both folders via terminal
3. Insert relevant directories
4. Use Sherlock by V.Stange available on git
5. Set zerobits and chainlength
6. Choose 1 of 3 filters by commenting out the other 2. Unfiltered: comment out all filters

Run it inside the IDE.
