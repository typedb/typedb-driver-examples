# TypeDB Examples

[![Grabl](https://grabl.io/api/status/vaticle/typedb-examples/badge.svg)](https://grabl.io/vaticle/typedb-examples)

This repository includes examples that showcase usage of TypeDB Clients in reading from and writing to a TypeDB knowledge graph.

## Explore Schemas
- [Catalogue of Life](catalogue_of_life/schema.tql) — a database containing the information about the taxonomy of life
on Earth, showcasing automated loading of data using [TypeDB-Loader.](https://github.com/typedb-osi/typedb-loader)
- [GitHub](github/schemas/github-schema.tql) — load data from a live repository on GitHub or from a Vaticle GitHub snapshot, and get results via a custom GUI interface that
uses the Java client to fetch the requested data.
- [Phone Calls](phone_calls/schema.tql) — a database of customers of a fictional telecom company and calls they make.
- [XCOM 2](xcom/schema.tql) — a database of interdependent research tasks in the game XCOM 2, featuring automatic inference of available
research based on completed tasks and available items.

## Explore Client Examples

TypeDB officially supports clients for Java, Node.js and Python. Learn more about [TypeDB Clients](http://docs.vaticle.com/docs/client-api/overview).

### Phone Calls

[The Phone Calls example](phone_calls) showcases basic migration of phone call data in various formats (CSV, JSON, and XML)
and performing expressive TypeQL queries using the [Java,](phone_calls/java) [Node.js,](phone_calls/nodejs) and
[Python](phone_calls/python) clients.

#### Data migration
- Java: [CSV](phone_calls/java/CSVMigration.java) | [JSON](phone_calls/java/JSONMigration.java) | [XML](phone_calls/java/XMLMigration.java)
- Node.js: [CSV](phone_calls/nodejs/migrateCsv.js) | [JSON](phone_calls/nodejs/migrateJson.js) | [XML](phone_calls/nodejs/migrateXml.js)
- Python: [CSV](phone_calls/python/migrate_csv.py) | [JSON](phone_calls/python/migrate_json.py) | [XML](phone_calls/python/migrate_xml.py)

#### Query examples
- [Java](phone_calls/java/Queries.java)
- [Node.js](phone_calls/nodejs/queries.js)
- [Python](phone_calls/python/queries.py)

### Github (GUI)

[The github example](github) showcases migration of heavily interconnected github repository data, and provides a visual
interface to explore some sample queries.

### XCOM Project

[The XCOM 2 example](xcom) showcases migration of tree-structured game data and logical inference capabilities of TypeDB.
See [the schema](xcom/schema.tql) for the examples of reasoner rules inferring attributes.

## Explore TypeDB Loader Examples

### Catalogue of Life

[The Catalogue of Life example](catalogue_of_life) showcases simple data preparation and a sample configuration file for
loading a large taxonomic dataset using [TypeDB Loader](https://github.com/typedb-osi/typedb-loader).

## Explore via TypeDB Studio

After loading the data of any of the examples, you can use [TypeDB Studio](https://github.com/vaticle/typedb-studio/releases) to explore
the graph structure of the database.

## Explore TypeDB Bio
[BioGrakn](https://github.com/vaticle/typedb-bio) is a collection of knowledge graphs of biomedical data.

TypeDB Bio provides an intuitive way to query interconnected and heterogeneous biomedical data in one single place. The schema that models the underlying knowledge graph alongside the descriptive query language, TypeQL, makes writing complex queries an extremely straightforward and intuitive process. Furthermore, the automated reasoning capability of TypeDB, allows TypeDB Bio to become an intelligent database of biomedical data that infers implicit knowledge based on the explicitly stored data. TypeDB Bio can understand biological facts, infer based on new findings and enforce research constraints, all at query (run) time.
