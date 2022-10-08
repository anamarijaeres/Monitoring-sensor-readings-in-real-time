# Monitoring Sensor Readings in Real Time

The goal of this exercise is to build a decentralized distributed system with equal participants using UDP protocol communication and a Kafka control node using Kafka technology. In doing so, a mechanism for synchronizing the process over time will be applied. This exercise consists of three parts:
- Studies of the attached code examples from the lectures (Kafka and UDP protocol communication). 
- Programming a node that is part of a peer-to-peer system. 
- Kafka control node programming.



## Getting Started

These instructions will give you a copy of the project up and running on
your local machine for development and testing purposes.

### Prerequisites

Requirements for the software and other tools to build, test and push 
- [Java 8 SDK or newer](https://www.oracle.com/es/java/technologies/javase/javase8-archive-downloads.html)
- [Apache Zookeeper](https://zookeeper.apache.org/releases.html)
- [Kafka](https://kafka.apache.org/downloads)

### Installing

A step by step series of examples that tell you how to get a development
environment running.

Kafka is configured using the server.properties configuration file located in KAFKA_HOME\config directory.
    
    C:\kafka\config\server.properties
   
Kafka stores content to the location defined in the server.properties file under the value 'log.dirs'. 
Value set log.dirs arbitrarily, eg KAFKA_HOME\data\kafka. All other settings can remain
on assumed values.



Zookeeper is configured using the zookeeper.properties configuration file, which
located in the KAFKA_HOME\config directory.
    
    C:\kafka\config\zookeeper.properties

Zookeeper stores the content in the location defined in the zookeeper.properties file under the value 'dataDir'.

Set the dataDir value arbitrarily, eg KAFKA_HOME\data\zookeeper. All others the settings can remain at the default values.

Zookeeper and Kafka are started using the terminal. First you need to start Zookeeper, and then
Kafka. The startup scripts are located in the KAFKA_HOME\bin\windows directory. Zookeeper
are started using the 'zookeeper-server-start script' which receives as an argument the path to
configuration files 

    C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties

Kafka is started using the 'kafka-server-start script' which as
argument receives the path to the configuration file

    C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties



## Authors

  - **Ana Marija Eres** 



