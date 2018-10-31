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

- Read the **[blog post](https://blog.grakn.ai/loading-data-into-a-grakn-knowledge-graph-using-the-java-client-5f2f1a7f9903)**
- Read the comments in `src/main/java/ai.grakn.examples/Migration.java`

## Quick Start

Run:

- `git clone git@github.com:graknlabs/examples.git`
- `path-to-grakn-dist-directory/grakn server start`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/java/migration/schema.gql`
- `cd examples/java/migration`
- for migrating:
    - csv data:
        - `cd csv`
        - `mvn clean compile assembly:single`
        - `java -cp target/migrate-csv-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.CsvMigration`
    - json data:
        - `cd json`
        - `mvn clean compile assembly:single`
        - `java -cp target/migrate-json-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.JsonMigration`
    - xml data:
        - `cd xml`
        - `mvn clean compile assembly:single`
        - `java -cp target/migrate-xml-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.XmlMigration`

### Before running the migration for a second time

The `phone_calls` keyspace needs to be cleaned. Run:

- `path-to-grakn-dist-directory/graql console -k phone_calls`
- `clean`
- `confirm`
- `exit`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/java/migration/schema.gql`
