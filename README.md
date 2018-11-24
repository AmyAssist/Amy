# Amy
[![Build Status](https://travis-ci.com/AmyAssist/Amy.svg?branch=dev)](https://travis-ci.com/AmyAssist/Amy)
[![Coverage Status](https://codecov.io/gh/AmyAssist/Amy/branch/dev/graph/badge.svg)](https://codecov.io/gh/AmyAssist/Amy)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.amyassist%3Aamy&metric=coverage)](https://sonarcloud.io/component_measures?id=io.github.amyassist%3Aamy&metric=Coverage)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=io.github.amyassist%3Aamy&metric=ncloc)](https://sonarcloud.io/dashboard?id=io.github.amyassist%3Aamy)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=io.github.amyassist%3Aamy&metric=bugs)](https://sonarcloud.io/component_measures?id=io.github.amyassist%3Aamy&metric=Reliability)
[![Reliability](https://sonarcloud.io/api/project_badges/measure?project=io.github.amyassist%3Aamy&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=io.github.amyassist%3Aamy&metric=Reliability)

[![Docker](http://dockeri.co/image/amyassist/amy)](https://hub.docker.com/r/amyassist/amy/)


### Personal Assistance System

This is a research project from students of the University of Stuttgart. No functionality is tested. There may be harmful errors.  

In parallel, we are working on a web interface for Amy, which you can find [here](https://github.com/AmyAssist/Amy-Web)


Maven: io.github.amyassist.amy



## Getting Started
To get started with Amy, either clone or download the project from the [main page](https://github.com/AmyAssist/Amy).


### Prerequisites

We handle our dependencies within the project with [Apache Maven](https://maven.apache.org/) so to build the project you have to have Maven installed. You can find the archive to download on [this website](https://maven.apache.org/download.cgi).

Additionaly we need a running MQTT Broker on the system. See the [wiki](https://github.com/AmyAssist/Amy/wiki/MessageHub) for more information.

### Installing and running

To install the project, you need to run maven install on the root directory of the project, so the path you are in should look something like this: `C:/...folder(s).../Amy/`  
Execute `mvn install`, this command will install and test the whole project. 

#### Important: It is imperative that you do this at least once (when downloading a fresh copy of the project) or when you make any change to the code, otherwise your changes will not be applied

After installing, you are able to run the project. Use your favourite Java IDE and run the Main method in the [Main class](core/src/main/java/io/github/amyassist/amy/core/Main.java) that lies in the core project.

As soon as the confirmation `[INFORMATION] :: Speech Recognition activated` pops up in your console you are good to go.  

#### Waking her up and setting her to sleep
Before giving Amy any commands, she first has to be woken up.  
This is accomplished by saying `Amy wake up` - if the command was spoken and heard properly you will receive an acoustic confirmation that she is awake.  
You may now give Amy speech commands.


If you wish that Amy stops listening, simply say `Amy sleep` and she will not react to any commands that are given to her - until you wake her up again


#### Using the console to give Amy commands
There are commands which can be typed into the console that do not trigger any plugin actions. An example would be `?list`.  
This command lists all of the possible commands that you can give Amy.  

If you wish to trigger a plugin action with a command, be careful that you put the word `say` in front of your command, otherwise the errors will haunt you even in your dreams.  
A valid command looks like this: `say how many emails do i have`


## Running the tests

If you wish to run the automated tests, run `mvn test` in your console while being in the root folder of the project (the same as the one you installed in) 

## Development

Documentation is in the [Wiki](https://github.com/AmyAssist/Amy/wiki) of GitHub.

Direct links:
- [Home](https://github.com/AmyAssist/Amy/wiki)
- [Annotations](https://github.com/AmyAssist/Amy/wiki/Annotations)
- [Architecture](https://github.com/AmyAssist/Amy/wiki/Architecture)
- [Dependency Injection](https://github.com/AmyAssist/Amy/wiki/Dependency-Injection-(DI))(incomplete)
- [How to create a plugin](https://github.com/AmyAssist/Amy/wiki/How-to-create-a-plugin)
- [How To Test](https://github.com/AmyAssist/Amy/wiki/How-to-Test)
- [Issues](https://github.com/AmyAssist/Amy/wiki/Issues)
- [Logger](https://github.com/AmyAssist/Amy/wiki/Logger)


## Deployment

Best use our docker compose [project](https://github.com/amyassist/amy-all).
Otherwise build the master node as a jar and every plugin as a jar and place all plugin jars in a directory.
Then create a directory named config and place all required configs in there. In the plugin.config set the plugin path acordingly.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Authors

See the list of [contributors](https://github.com/AmyAssist/Amy/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE Document](LICENSE) for details

## Acknowledgments

* [CMU Sphinx Speech Recognition](https://cmusphinx.github.io/)
* [Mary TextToSpeech](http://mary.dfki.de/)
* for additional acknowledgments see the [Notice file](notice.md)
