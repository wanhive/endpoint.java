# System requirements

* Java SE 8 or above
* maven

# Build and install

- Generate a JAR file:

```
mvn clean package
```

- Install in the local maven repository:

```
mvn clean install
```

# Using the library

To use the installed library in a maven project, add the following dependency to the pom.xml file:

```
		<dependency>
			<groupId>com.wanhive.iot</groupId>
			<artifactId>wanhive-endpoint</artifactId>
			<version>0.10.0</version>
		</dependency>
```

# Getting started

The [ClientTest.java](src/test/java/com/wanhive/iot/test/ClientTest.java) file contains a very basic example that connects to a wanhive network and publishes few messages.

This package also includes

- A configuration file [template](wanhive-client-java.conf)
- A hosts file [template](hosts)
