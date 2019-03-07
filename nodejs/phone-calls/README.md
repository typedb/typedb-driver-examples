---
title: Example Queries using the Node.js Client
keywords: graql queries, grakn, node.js client
tags: [example]
sidebar: documentation_sidebar
permalink: /examples/nodejs/queries
folder: examples
symlink: false
---

## Example Queries using the Node.js Client

The `queries.js`, contains query examples that get exectued on the phone_calls knowledge graph.

### Prerequisites

- [Migrating data into the phone_calls Grakn keyspace](https://github.com/graknlabs/examples/tree/master/nodejs/migration)
- Grakn 1.3.0 && Node >= 3.6
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Javascript and Node

### Understanding the code

- Read the **[blog post](...)**
- Study the code/comments in `queries.js`

## Quick Start

Run:

- [migrate data into the phone_calls Grakn keyspace](https://github.com/graknlabs/examples/tree/master/nodejs/migration)
- `path-to-grakn-dist-directory/grakn server start` (if the Grakn server is not yet running)
- `git clone git@github.com:graknlabs/examples.git`
- `cd examples/nodejs/queries`
- `npm run queries`
- follow the instructions to run available Graql queries


---
title: Loading CSV, JSON and XML data into Grakn - an Example
keywords: migration, csv to grakn, json to grakn, xml to grakn, nodejs client
tags: [example]
sidebar: documentation_sidebar
permalink: /examples/nodejs/migration
folder: examples
symlink: false
---

## Loading CSV, JSON and XML data into Grakn using the Node.js Client - an Example

These examples uses the [Grakn Node.js Client](https://github.com/graknlabs/grakn/tree/master/client-nodejs) to load a dataset in CSV, JSON and XML formats into a Grakn keyspace.

### Prerequisites

- Grakn 1.3.0 && Node >= 6.5.0
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Javascript and Node.js

### Understanding the code

- Read the **[blog series](https://medium.com/@soroush_26094/load-csv-json-and-xml-data-into-grakn-1ab5bf70348)**
- Read the comments in `migrate.js`

## Quick Start

Run:

- `git clone git@github.com:graknlabs/examples.git`
- `path-to-grakn-dist-directory/grakn server start`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/nodejs/migration/schema.gql`
- `cd examples/nodejs/migration`
- `cd csv`, `cd json` or `cd xml`
- `npm install`
- `npm run migrate`

### Before running `npm run migrate` for a second time

The `phone_calls` keyspace needs to be cleaned. Run:

- `path-to-grakn-dist-directory/graql console -k phone_calls`
- `clean`
- `confirm`
- `exit`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/nodejs/migration/schema.gql`
