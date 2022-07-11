# Phone Calls TypeDB Example (Python)

The Phone Calls Python example showcases:
1. basic migration of CSV, JSON and XML data into a TypeDB knowledge graph database
2. writing and performing expressive TypeQL queries to gain insights over the dataset

## Prerequisites

* Install [Python and pip package manager](https://www.python.org/) (we recommend Python 3.10.5 to ensure compatibility)
* Install the latest version of [TypeDB](https://docs.vaticle.com/docs/running-typedb/install-and-run) that is compatible with the latest version of Client Node.js (see the [dependency table](http://docs.vaticle.com/docs/client-api/java#dependencies))

## Quickstart

1. Checkout this repository: `git clone https://github.com/vaticle/typedb-examples && cd typedb-examples`
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
5. Install dependencies: `pip install -r requirements.txt`.
6. Migrate the dataset into TypeDB from any of the 3 supported formats: CSV, JSON, or XML, using any one of the following commands:
- `python migrate_csv.py`,
- `python migrate_json.py`, or
- `python migrate_xml.py`.
7. Launch the interactive query runner: `python queries.py`.
