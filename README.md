## tracey-cli

Basic command line tool for Tracey. Primarily used as a testbed for tracey

```
usage: tracey [say | listen] (options)
options:

 -c <arg>    Configuration file
 -f          Specify this if the input is a file
 -h,--help   Prints help
 ```
 
### Building the cli
 
 - Clone `https://github.com/Praqma/tracey-core` and run `gradle installToMavenLocal`
 - Clone `https://github.com/Praqma/tracey-broker` and run `gradle installToMavenLocal`
 - To create the `jar` run the following command from this repository: `gradle uberjar` 
