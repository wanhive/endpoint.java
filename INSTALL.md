# System requirement

* JDK 8 or above
* maven (for building)

# Build and Install

To generate a JAR file, invoke:

```
mvn clean package
```

To install into the local maven repository, invoke:

```
mvn clean install
```

To use the installed JAR in a maven project, add the following dependency to the pom.xml file:

```
		<dependency>
			<groupId>com.wanhive.iot</groupId>
			<artifactId>wanhive-endpoint</artifactId>
			<version>0.5.0</version>
		</dependency>
```
