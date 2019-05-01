## Phone Calls: Migrate and Query Using Client Java

The Phone Calls Java example showcases:
1. basic migration of CSV, JSON and XML data into a Grakn knowledge graph
2. writing and performing expressive Graql queries to gain insights over the dataset

### Quickstart
1. Install [Grakn 1.5.2](http://dev.grakn.ai/docs/running-grakn/install-and-run#download-and-install-grakn)
2. Clone this repository
3. Via terminal while inside the Grakn distribution, run: `./grakn console -k phone_calls -f path-to-the-cloned-repo/schemas/phone-calls-schema.gql`
4. Start the [Grakn Sever](http://dev.grakn.ai/docs/running-grakn/install-and-run#start-the-grakn-server).
5. Install [Bazel](https://docs.bazel.build/versions/master/install.html). (you may also declare dependencies and run using Maven. See [Grakn Client Java documentation](http://dev.grakn.ai/docs/client-api/java).)
6. To install all the dependencies, run `bazel build //...`.
7. To migrate the dataset into Grakn, run:
- `bazel run //phone_calls/java:csv-migration`,
- `bazel run //phone_calls/java:json-migration`, or
- `bazel run //phone_calls/java:xml-migration`.
8. To initiate the interactive query runner, run `bazel run //phone_calls/java:queries`.