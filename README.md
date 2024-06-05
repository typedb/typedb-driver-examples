# TypeDB Driver Examples

[![Factory](https://factory.vaticle.com/api/status/vaticle/typedb-driver-examples/badge.svg)](https://factory.vaticle.com/vaticle/typedb-driver-examples)

## Table of contents

- [Examples in this repository](#examples-in-this-repository)
- [Data visualisation](#data-visualisation)
- [Additional examples](#additional-examples)

## Examples in this repository

This repository includes examples that showcase usage of TypeDB Drivers in reading from and writing to a TypeDB 
database.

### [Biology: Catalogue of Life](biology/catalogue_of_life)

[Catalogue of Life](https://www.catalogueoflife.org/) is a database of over 4.5 million currently known taxa in biology,
compiled from over a hundred different sources. The example showcases simple data preparation and a sample configuration
file for loading a large taxonomic dataset using [TypeDB Loader.](https://github.com/typedb-osi/typedb-loader)

### [Commerce: Bookstore](commerce/bookstore)

The bookstore example uses Python to showcase migration of data into TypeDB and executing queries on this data.

The data of the Books, Users and Orders loaded from the `.csv` files in the [data](commerce/bookstore/python/data) 
directory.

Read the [README](commerce/bookstore/README.md) file for instructions. Check [the schema](commerce/bookstore/schema.tql)
or the initial [dataset](commerce/bookstore/python/data) for additional information. All logic accessible in the script
files in the [python](commerce/bookstore/python) directory.

### [Cybersecurity: Cyber Threat Intellingence](cybersecurity/cyber_threat_intelligence)

The Cyber Threat Intelligence example uses Spring Boot to showcase usage of CTI dataset into TypeDB and executing queries on this data.
In this project, we use TypeDB to represent a queryable database of relevant CTI data about some threat actors and their targets.
We can query the database either with a REST API or a GraphQL API through the dedicated web interface.

 
### [Finance: Fraud Detection](finance/fraud_detection)

The Fraud Detection example uses Quarkus, the Supersonic Subatomic Java Framework in order to let us present results with GraphQL.
In this project, we use TypeDB to represent a queryable database of relevant fraud detection data about some transactions between Cardholders and Companies.

### [Gaming: XCOM Project](gaming/xcom)

The XCOM 2 example contains a database of interdependent research tasks in the game XCOM 2, featuring automatic
inference of available research based on completed tasks and available items. See [the schema](gaming/xcom/schema.tql)
for the examples of reasoner rules inferring attributes.

### [Software: GitHub](software/github)

The GitHub example showcases migration of heavily interconnected data from a live repository on GitHub or from a Vaticle
GitHub snapshot, and provides a visual interface to explore some sample queries.

### [Telecom: Phone Calls](telecom/phone_calls)

TypeDB officially supports drivers for Java, Node.js, Python, and more. Learn more about [TypeDB Drivers](https://typedb.com/docs/drivers/overview).

The Phone Calls example showcases basic migration of a database of customers of a fictional telecom company and calls
they make in various formats (CSV, JSON, and XML) and expressive TypeQL queries using
the [Java,](telecom/phone_calls/java) [Node.js,](telecom/phone_calls/nodejs) and [Python](telecom/phone_calls/python)
drivers.

#### Data migration

- Java: [CSV](telecom/phone_calls/java/CSVMigration.java) | [JSON](telecom/phone_calls/java/JSONMigration.java)
  | [XML](telecom/phone_calls/java/XMLMigration.java)
- Node.js: [CSV](telecom/phone_calls/nodejs/migrateCsv.js) | [JSON](telecom/phone_calls/nodejs/migrateJson.js)
  | [XML](telecom/phone_calls/nodejs/migrateXml.js)
- Python: [CSV](telecom/phone_calls/python/migrate_csv.py) | [JSON](telecom/phone_calls/python/migrate_json.py)
  | [XML](telecom/phone_calls/python/migrate_xml.py)

#### Query examples

- [Java](telecom/phone_calls/java/Queries.java)
- [Node.js](telecom/phone_calls/nodejs/queries.js)
- [Python](telecom/phone_calls/python/queries.py)

## Data visualisation

After loading the data for any of the examples, you can use
[TypeDB Studio](https://github.com/vaticle/typedb-studio/releases) to explore the graph structure of the database.

## Additional examples

There are some examples in other repositories that are recommended for more advanced users.

### TypeDB Bio

[TypeDB Bio](https://github.com/vaticle/typedb-bio) is a collection of knowledge graphs of biomedical data.

TypeDB Bio provides an intuitive way to query interconnected and heterogeneous biomedical data in one single place. The
schema that models the underlying knowledge graph alongside the descriptive query language, TypeQL, makes writing
complex queries an extremely straightforward and intuitive process. Furthermore, the automated reasoning capability of
TypeDB, allows TypeDB Bio to become an intelligent database of biomedical data that infers implicit knowledge based on
the explicitly stored data. TypeDB Bio can understand biological facts, infer based on new findings and enforce research
constraints, all at query (run) time.

### TypeDB CTI

[TypeDB CTI](https://github.com/typedb-osi/typedb-cti) is an open source threat intelligence platform for organisations
to store and manage their cyber threat intelligence (CTI) knowledge. It enables threat intel professionals to bring
together their disparate CTI information into one database and find new insights about cyber threats.
