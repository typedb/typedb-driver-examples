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

These examples use the [Grakn Python Client](https://github.com/graknlabs/grakn/tree/master/client-python) to load a dataset in CSV, JSON and XML formats into a Grakn keyspace.

### Prerequisites

- Grakn 1.3.0 && Python >= 3.6
- Basic understading of [GRAKN.AI](http://dev.grakn.ai/docs)
- Basic knowledge of Python

### Understanding the code

- Read the [blog series](...)
- Read the comments in `migrate.js`

## Quick Start

Run:

- `git clone git@github.com:graknlabs/examples.git`
- `path-to-grakn-dist-directory/grakn server start`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/python/migration/schema.gql`
- `cd examples/python/migration`
- `cd csv`, `cd json` or `cd xml`
- `npm install`
- `npm run migrate`

### Before running `npm run migrate` for a second time

The `phone_calls` keyspace needs to be cleaned. Run:

- `path-to-grakn-dist-directory/graql console -k phone_calls
- `clean`
- `confirm`
- `exit`
- `path-to-grakn-dist-directory/graql console -k phone_calls -f path-to-cloned-repository/python/migration/schema.gql`
