## Phone Calls: Migrate and Query Using Client Java

The Phone Calls Java example showcases:
1. basic migration of CSV, JSON and XML data into a TypeDB knowledge graph
2. writing and performing expressive TypeQL queries to gain insights over the dataset

### Quickstart
1. Install the latest version of [TypeDB](https://github.com/vaticle/typedb/releases) that is compatible with the latest version of Client Node.js (see the [dependency table](http://docs.vaticle.com/docs/client-api/java#dependencies))
2. Clone this repository
3. Start the [TypeDB Sever](http://docs.vaticle.com/docs/running-typedb/install-and-run#start-the-typedb-server).
4. Via terminal while inside the TypeDB distribution, run: `./typedb console -k phone_calls -f path-to-the-cloned-repo/schemas/phone-calls-schema.gql`
5. Install [Bazel](https://docs.bazel.build/versions/master/install.html). (you may also declare dependencies and run using Maven. See [TypeDB Client Java documentation](http://docs.vaticle.com/docs/client-api/java).)
6. To install all the dependencies, run `bazel build //...`.
7. To migrate the dataset into TypeDB, run:
- `bazel run //phone_calls/java:csv-migration`,
- `bazel run //phone_calls/java:json-migration`, or
- `bazel run //phone_calls/java:xml-migration`.
8. To initiate the interactive query runner, run `bazel run //phone_calls/java:queries`.