# System requirements

* JDK 8 or above
* maven (for building)

# Build and install

To generate a JAR file, invoke:

```
mvn clean package
```

To install into the local maven repository, invoke:

```
mvn clean install
```

# Using the library

To use the installed library in a maven project, add the following dependency to the pom.xml file:

```
		<dependency>
			<groupId>com.wanhive.iot</groupId>
			<artifactId>wanhive-endpoint</artifactId>
			<version>0.6.0</version>
		</dependency>
```

# Getting started

The [ClientTest.java](https://github.com/wanhive/endpoint.java/blob/main/src/test/java/com/wanhive/iot/test/ClientTest.java) file contains a very basic example that connects to a Wanhive network and publishes few messages.

This package also includes

- A configuration file [template](wanhive-client-java.conf)
- A hosts file [template](hosts)
