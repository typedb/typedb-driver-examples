# Tube Network Example

Here we demonstrate a way of modelling the London Underground Network using data acquired from the Transport for London (TFL) website.

Most of the code given here is written in Python to give an example of how to use the Grakn Python client.

See the quickstart for how to get going immediately, or read on for more info.

## Prerequisites
- Grakn >= 1.4.2. Learn more about [installing and running Grakn](http://dev.grakn.ai/docs/running-grakn/install-and-run).
- Python >= 3.6

## Quickstart
Use this quickstart to get up and running fast, or read on for more detailed information on each element.

- Clone this repository: `git clone git@github.com:graknlabs/examples.git`
- Start the Grakn Server: `path-to-grakn-dist-directory/grakn server start`
- Navigate to the `tube_network_example`: `cd path-to-cloned-repository/applications/tube-network`
- Load the schema: `path-to-grakn-dist-directory/graql console -k tube_example -f ./tube_network_example/src/tube_schema.gql`
- Install `grakn` module: `pip install grakn`. Learn more about [Client Python](http://dev.grakn.ai/docs/client-api/python).
- Migrate the dataset: `python3 -m tube_network_example.src.migrations.timetable_migration`. Learn more about [migrating data to Grakn by example](http://dev.grakn.ai/docs/examples/phone-calls-migration-python).
- To continue:
    - Run queries on the London Tube Network using [Graql Console](http://dev.grakn.ai/docs/running-grakn/console) and [Workbase](http://dev.grakn.ai/docs/workbase/overview).
    - or:
        - Retrieve fundamental statistics: `python3 -m tube_network_example.src.statistics`. Learn more about the [Compute Query](http://dev.grakn.ai/docs/query/compute-query).
        - Try the basic journey planner: `python3 -m tube_network_example.src.journey_planner`
        - Interact with the journey planner interface: `python3 -m tube_network_example.src.visualisation.app`

## Downloading Data
The data necessary to build a Grakn of the Tube Network is already included in this repo, the code to acquire it can be found in [src/data_acquisition/download_dataset.py](src/data_acquisition/download_dataset.py).

## Importing Data
We can import this data into the Grakn keyspace we have just created. The name of the keyspace is set in settings.py, so you can change it there if you need to. You don't have to implement settings in this way in your own application.

Check Grakn is up and running: `./grakn server status`

To import, run [timetable_migration.py](src/migrations/timetable_migration.py), either in your IDE, or from the grakn_examples directory as follows:
```bash
python3 -m tube_network_example.src.migrations.timetable_migration
```

This is a custom-built python script that just iterates over the downloaded json data.

You should see insertions being made into the Grakn, the logs for this will be created in src/migrations/logs, showing the graql query sent, and the response received.

Once complete, you have stored the tube network data in Grakn!

Now you're ready to start playing with the data.


## Reasoning
Now you can query the database, you can try asking more complex questions, like asking for time between stations of over 8 minutes:
```bash
# Which stops take over 8 minutes?
>>> match
$s1 isa station, has name $n1;
$s2 isa station, has name $n2;
(beginning: $s1, end: $s2, service: $rs) isa tunnel;
$rs isa route-section, has duration $d; $d > 8;
(route-operator: $tl, section: $rs) isa route;
$tl isa tube-line, has name $tl-name;
limit 30; get $n1, $n2, $tl-name, $d;
```

## Interacting with the Journey Planner
Here we've built a basic demo application to show the analytics capabilities built into Grakn.

Run [src/visualisation/app.py](src/visualisation/app.py) from the `grakn_examples` root directory as:
```bash
grakn_examples $ python -m tube_network_example.src.visualisation.app
```
This may take a couple of minutes to query the database for all of the necessary information and build the network map.

Now you can use a few keyboard shortcuts to see analytics in action. The application will query Grakn live, with will use its scalable built-in OLAP algorithms.

#### Centrality
Keymap:

Drag mouse to pan

`=`/`+` to zoom in

`-`/`_` to zoom out

`d` - `compute centrality of station, in [station, tunnel], using degree;`

`k` - `compute centrality of station, in [station, tunnel], using k-core;`

`r` - `compute centrality of station, in [station, route], using degree;`

From any state, press `c` to clear the map of analytics.

#### Shortest Path
`Shift+click` on a station or the station's name (station _A_). Do the same on another station (station _B_). This will show the shortest path(s) via stations and tunnels to connect _A_ and _B_.

`Shift+click`ing on a further station _C_ will add the path(s) from _B_ to _C_.

So, you should be able to find the shortest path from some station _A_ to some station _E_, via stations _B_, _C_ and _D_.

Use `q` to clear the shortest path(s), or `c` to remove anything that has been drawn on top of the map.

## Extra - Distances between Neighbouring Stations
Also included is [src/data_acquisition/distance_between_stops.py](src/data_acquisition/distance_between_stops.py) that computes the as-the-crow-flies distance between pairs of stations, where that pair are related by at least one `tunnel`.

This could be useful for calculating the walking time between neighbouring stations, or approximating average speed of trains between stations, for example.

The script performs this calculation and then adds the distance as an attribute of the `neighbourship` relationship. That relationship was inferred by the `neighbouring` rule, but at this point must be materialised in the database in order to attach the `distance` attribute.