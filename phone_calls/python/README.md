## Phone Calls: Migrate and Query Using Client Python

The Phone Calls Java example showcases:
1. basic migration of CSV, JSON and XML data into a Grakn knowledge graph
2. writing and performing expressive Graql queries to gain insights over the dataset

### Quickstart
1. Install [the latest Grakn](http://dev.grakn.ai/docs/running-grakn/install-and-run#download-and-install-grakn)
2. Clone this repository
3. Via terminal while inside the Grakn distribution, run: `./grakn console -k phone_calls -f path-to-the-cloned-repo/schemas/phone-calls-schema.gql`
4. Start the [Grakn Sever](http://dev.grakn.ai/docs/running-grakn/install-and-run#start-the-grakn-server).
5. To install all the dependencies, run `pip install -r requirements.txt`.
6. To migrate the dataset into Grakn, run:
- `python migrate_csv.py`,
- `python migrate_json.py`, or
- `python migrate_xml.py`.
7. To initiate the interactive query runner, run `python queries.py`.