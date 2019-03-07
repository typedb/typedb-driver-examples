---
title: Example Queries using the Python Client
keywords: graql queries, grakn, python client
tags: [example]
sidebar: documentation_sidebar
permalink: /examples/python/queries
folder: examples
symlink: false
---

## Example Queries using the Python Client

The `queries.py`, contains query examples that get exectued on the phone_calls knowledge graph.

### Prerequisites

- [Migrating data into the phone_calls Grakn keyspace](https://github.com/graknlabs/examples/tree/master/python/migration)
- Grakn 1.3.0 && Python >= 3.6
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Python

### Understanding the code

- Read the **[blog post](https://medium.com/@soroush_26094/grakn-python-client-migrating-csv-json-and-xml-data-into-grakn-4af10788f4ae)**
- Study the code/comments in `queries.py`

## Quick Start

Run:

- [migrate data into the phone_calls Grakn keyspace](https://github.com/graknlabs/examples/tree/master/python/migration)
- `path-to-grakn-dist-directory/grakn server start` (if Grakn server is not yet running)
- `git clone git@github.com:graknlabs/examples.git`
- `cd examples/python/queries`
- `python3 queries.py`
- follow the instructions to run available Graql queries



---
title: Loading CSV, JSON and XML data into Grakn using the Python Client - an Example
keywords: migration, csv to grakn, json to grakn, xml to grakn, python client
tags: [example]
sidebar: documentation_sidebar
permalink: /examples/python/migration
folder: examples
symlink: false
---

## Loading CSV, JSON and XML data into Grakn using the Python Client - an Example

This migration example uses the [Grakn Python Client](https://github.com/graknlabs/grakn/tree/master/client-python) to load a dataset in CSV, JSON or XML format into a Grakn keyspace.

### Prerequisites

- Grakn 1.3.0 && Python >= 3.6
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Python

### Understanding the code

- Read the **[blog post](https://medium.com/@soroush_26094/grakn-python-client-migrating-csv-json-and-xml-data-into-grakn-4af10788f4ae)**
- Read the comments in `migrate.py`

## Quick Start

Run:

- `git clone git@github.com:graknlabs/examples.git`
- `path-to-grakn-dist-directory/grakn server start`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/python/migration/schema.gql`
- `cd examples/python/migration`
- `cd csv`, `cd json` or `cd xml`
- `python3 migrate.py`

### Before running `python3 migrate.py` for a second time

The `phone_calls` keyspace needs to be cleaned. Run:

- `path-to-grakn-dist-directory/graql console -k phone_calls
- `clean`
- `confirm`
- `exit`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/python/migration/schema.gql`
