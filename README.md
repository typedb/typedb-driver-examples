# TypeDB Examples

[![Grabl](https://grabl.io/api/status/vaticle/typedb-examples/badge.svg)](https://grabl.io/vaticle/typedb-examples)

This repository includes examples that showcase usage of TypeDB Clients in reading from and writing to a TypeDB knowledge graph.

## Explore Client Examples

TypeDB officially supports clients for Java, Node.js and Python. Learn more about [TypeDB Clients](http://docs.vaticle.com/docs/client-api/overview).

### Client Java Examples
- [Phone Calls: Migrate and perform expressive queries using Client Java](phone_calls/java/)
- [XCOM Project: Migrate and perform logical queries on tree-structured game data using Client Java](xcom/)

### Client Node.js Examples
- [Phone Calls: Migrate and perform expressive queries using Client Node.js](phone_calls/nodejs/)

### Client Python Examples
- [Phone Calls: Migrate and perform expressive queries using Client Python](phone_calls/python/)

## Explore Use Cases

### Migrate data to TypeDB:

#### serialised:
- Java: [CSV](phone_calls/java/CSVMigration.java) | [JSON](phone_calls/java/JSONMigration.java) | [XML](phone_calls/java/XMLMigration.java)
- Node.js: [CSV](phone_calls/nodejs/migrateCsv.js) | [JSON](phone_calls/nodejs/migrateJson.js) | [XML](phone_calls/nodejs/migrateXml.js)
- Python: [CSV](phone_calls/python/migrate_csv.py) | [JSON](phone_calls/python/migrate_json.py) | [XML](phone_calls/python/migrate_xml.py)

### Perform expressive queries:
- [Java](phone_calls/java/Queries.java)
- [Node.js](phone_calls/nodejs/queries.js)
- [Python](phone_calls/python/queries.py)
    
## Explore TypeDB Bio
[BioGrakn](https://github.com/vaticle/typedb-bio) is a collection of knowledge graphs of biomedical data.

TypeDB Bio provides an intuitive way to query interconnected and heterogeneous biomedical data in one single place. The schema that models the underlying knowledge graph alongside the descriptive query language, TypeQL, makes writing complex queries an extremely straightforward and intuitive process. Furthermore, the automated reasoning capability of TypeDB, allows TypeDB Bio to become an intelligent database of biomedical data that infers implicit knowledge based on the explicitly stored data. TypeDB Bio can understand biological facts, infer based on new findings and enforce research constraints, all at query (run) time.

## Explore Schemas
- [Catalogue of Life](catalogue_of_life/schema.tql)
- [GitHub](github/schemas)
- [Phone Calls](phone_calls/schema.tql)
- [XCOM](xcom/schema.tql)
