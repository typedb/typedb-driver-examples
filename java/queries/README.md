---
title: Example Queries using the Java Client
keywords: graql queries, grakn, java client
tags: [example]
sidebar: documentation_sidebar
permalink: /examples/java/queries
folder: examples
symlink: false
---

## Example Queries using the Java Client

The `src/main/java/ai.grakn.examples/Queries.java`, contains query examples that get executed on the phone_calls knowledge graph.

### Prerequisites

- [Migrating data into the phone_calls Grakn keyspace](https://github.com/graknlabs/examples/tree/master/java/migration)
- Java 8 (OpenJDK or Oracle Java) with the $JAVA_HOME set accordingly
- If running on Windows version prior to 10, make sure to have Visual Studio C++ Runtime
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Java

### Understanding the code

- Read the **[blog post](...)**
- Study the code/comments in `Queries.java`

## Quick Start

Run:

- [migrate data into the phone_calls Grakn keyspace](https://github.com/graknlabs/examples/tree/master/java/migration)
- `path-to-grakn-dist-directory/grakn server start` (if Grakn server is not yet running)
- `git clone git@github.com:graknlabs/examples.git`
- `cd examples/java/queries`
- `mvn clean compile assembly:single`
- `java -cp target/migrate-xml-to-grakn-1.0-SNAPSHOT-jar-with-dependencies.jar ai.grakn.examples.XmlMigration`
- follow the instructions to run available Graql queries
