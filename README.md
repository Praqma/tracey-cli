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
 
 - To create the `jar` run the following command from this repository: `gradle uberjar` 

### Configuration file

Format as follows

```
broker {
    rabbitmq {
    	connection {
        	host = 'some.host.name'
        	port = 4444
        	userName = 'myuser'
        	password = 's0m3p4ss'
        	automaticRecovery = true
        }
        routingInfo {
        	exchangeName = 'stacie'
        	exchangeType = 'fanout'
        	routingKey = ''
        	deliveryMode = 1
        	headers {
        		someKey = 'someValue'
        		someKey1 = 0
        	}
        }
    }
}
```

