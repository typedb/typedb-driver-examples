# Phone Calls TypeDB Example (Java)

The Phone Calls Java example showcases:
1. basic migration of CSV, JSON and XML data into a TypeDB knowledge graph database
2. writing and performing expressive TypeQL queries to gain insights over the dataset

## Prerequisites

* [TypeDB](https://docs.vaticle.com/docs/running-typedb/install-and-run) v2.11.1
* [Bazel](https://bazel.build/install) v5.1.1 (alternatively, declare dependencies manually and run using Maven. See [TypeDB Client Java documentation](http://docs.vaticle.com/docs/client-api/java) for details.)

## Quickstart

1. Checkout this repository: `git clone https://github.com/vaticle/typedb-driver-examples && cd typedb-driver-examples`
2. Start the [TypeDB Server](http://docs.vaticle.com/docs/running-typedb/install-and-run#start-the-typedb-server).
3. Go to the directory where you have your `typedb-all` distribution unarchived, and run: `./typedb console` (or `typedb console`, if TypeDB was installed via a package manager)
4. Load the Phone Calls schema:
```shell
> database create phone_calls
> transaction phone_calls schema write
phone_calls::schema::write> source {path-to-the-cloned-repo}/phone_calls/schema.tql
phone_calls::schema::write*> commit
> exit
```
5. Build the example: `bazel build //telecom/phone_calls/java/...`.
6. Migrate the dataset into TypeDB from any of the 3 supported formats: CSV, JSON, or XML, using any one of the following commands:
- `bazel run //telecom/phone_calls/java:csv-migration`,
- `bazel run //telecom/phone_calls/java:json-migration`, or
- `bazel run //telecom/phone_calls/java:xml-migration`.
7. Launch the interactive query runner: `bazel run //telecom/phone_calls/java:queries`.
