[![Build Status](https://api.travis-ci.org/Praqma/tracey-cli-rabbitmq.svg?branch=master)](https://travis-ci.org/Praqma/tracey-cli-rabbitmq)
---
maintainer: andrey9kin, alexsedova
---
## tracey-cli

Basic command line tool for Tracey. Primarily used as a testbed for tracey

```
usage: tracey [say | listen] (options)
options:

 -c <arg>    Configuration file
 -f          Specify this if the input is a file
 -h,--help   Prints help
 ```

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

### Variable expansion

Please note that you can use environment variable names for user name and password to avoid storing them in plain text.
Use the following format - %SOMETEXT%, ${SOMETEXT}, $SOMETEXT, $SOMETEXT$.
See example below

```
broker {
    rabbitmq {
        connection {
            host = 'some.host.name'
            port = 4444
            userName = '${USERNAME}'
            password = '${PASSWORD}'
            automaticRecovery = true
        }
    }
}
```

### Defaults

You don't have to specify all fields in the configuration file - if some piece is not provided then default value will be used.
Check output of the --help to see the actual values.
See example below

```
broker {
    rabbitmq {
        connection {
            host = 'my.host'
            userName = '${USERNAME}'
            password = '${PASSWORD}'
        }
    }
}
```

### Get official build

Official builds are done by [JitPack](https://jitpack.io)

```
export VERSION=39e02b7dd9 #Any commit or tag
curl -o tracey-cli-rabbitmq.jar https://jitpack.io/com/github/Praqma/tracey-cli-rabbitmq/$VERSION/tracey-cli-rabbitmq-$VERSION.jar
```


## Releasing
To release a new version of this CLI on Github release you need to tag the commit to release. This will be picked up by Travis CI.

Github auth for Travis release

Release is done a ReleasePraqma user and was securely created using travis setup releases

```
$ travis setup releases
Detected repository as Praqma/tracey-cli-rabbitmq, is this correct? |yes| yes
Username: ReleasePraqma
Password for ReleasePraqma: **********
File to Upload: build/libs/tracey-cli-rabbitmq.jar
Deploy only from Praqma/tracey-cli-rabbitmq? |yes| yes
Encrypt API key? |yes| yes
```

_Then the `.travis.yml` file is updated, and we changed it slight to not clean binary and then only release on tagging._
