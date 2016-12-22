# device-cli
Simple CLI to communicate with CloudThing.io platform. 

# Build
Project expects you to have Gradle installed in your PATH.
Build from project directory with command: 
```
$ gradle build
```
This command will generate executable `jar` file in `build/libs/` directory

# Usage 
Execute the application from directory contaiting generated `jar` file with command:
```
$ java -jar device-cli-0.1.0.jar
```
It will print all required options and arguments. 

# CloudThing SDK
Application uses [CloudThing device SDK](https://github.com/cloudthing-io/java-sdk.git)
