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

### Before running `npm run migrate` for a second time

The `phone_calls` keyspace needs to be cleaned. Run:

- `path-to-grakn-dist-directory/graql console -k phone_calls
- `clean`
- `confirm`
- `exit`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/python/migration/schema.gql`
