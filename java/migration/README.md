`mvn clean compile assembly:single`

`java -cp target/migrate-csv-json-xml-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.Migration`

---
title: Loading CSV, JSON and XML data into Grakn using the Java Client - an Example
keywords: migration, csv to grakn, json to grakn, xml to grakn, java client
tags: [example]
sidebar: documentation_sidebar
permalink: /examples/java/migration
folder: examples
symlink: false
---

## Loading CSV, JSON and XML data into Grakn using the Java Client - an Example

This migration example uses the [Grakn Java Client](https://github.com/graknlabs/grakn/tree/master/client-java) to load a dataset in CSV, JSON or XML format into a Grakn keyspace.

### Prerequisites
- Java 8 (OpenJDK or Oracle Java) with the $JAVA_HOME set accordingly
- If running on Windows version prior to 10, make sure to have Visual Studio C++ Runtime for Visual Studio 2015 installed
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Java

### Understanding the code

- Read the **[blog post](...)**
- Read the comments in `src/main/java/ai.grakn.examples/Migration.java`

## Quick Start

Run:

- `git clone git@github.com:graknlabs/examples.git`
- `path-to-grakn-dist-directory/grakn server start`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/java/migration/src/main/schema.gql`
- `cd examples/java/migration`
- `cd csv`, `cd json` or `cd xml`
- `mvn clean compile assembly:single`
- `java -cp target/migrate-csv-json-xml-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.Migration`

### Before running `java -cp target/migrate-csv-json-xml-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.Migrationy` for a second time

The `phone_calls` keyspace needs to be cleaned. Run:

- `path-to-grakn-dist-directory/graql console -k phone_calls
- `clean`
- `confirm`
- `exit`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/java/migration/schema.gql`
