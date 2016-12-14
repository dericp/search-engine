# Search Engine

Download the 100k newswire documents as documents.zip and put it in src/main/resources/documents/.
Download the TinyIR jar and place it in lib/.

In source/, execute "sbt compile" then ""sbt run" on the command line.
Run the main method at edu.washington.cs.dericp.Main. Follow the instructions in main to run the test queries.
Building an inverted index from scratch takes a significant amount of memory, but once an inverted index has been
built, it is possible to write it to disk and simply load the inverted index during subsequent runs.
