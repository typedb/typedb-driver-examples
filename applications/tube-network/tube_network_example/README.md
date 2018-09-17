# Tube Network Example

Here we demonstrate a way of modelling the London Underground Network using data acquired from the Transport for London (TFL) website. 

Most of the code given here is written in Python to give an example of how to use the Grakn Python client.

See the quickstart for how to get going immediately, or read on for more info.


## Quickstart
Use this quickstart to get up and running fast, or read on for more detailed information on each element.

Download Grakn and the Python client:
```bash
$ mkdir grakn_examples_root && cd grakn_examples_root
grakn_examples_root $ wget https://github.com/graknlabs/grakn/releases/download/v1.2.0/grakn-dist-1.2.0.tar.gz
grakn_examples_root $ tar -xf grakn-dist-1.2.0.tar.gz
grakn_examples_root $ pip install grakn
```
Clone the tube network example:
```bash
grakn_examples_root $ git clone https://github.com/graknlabs/grakn_examples.git
```

Import the tube network data into Grakn:
```bash
grakn_examples_root $ ./grakn-dist-1.2.0/graql console -f ./grakn_examples/tube_network_example/src/tube_schema.gql -k tube_example
grakn_examples_root $ cd grakn_examples/
grakn_examples $ python -m tube_network_example.src.migrations.timetable_migration
```
(Henceforth you may need to use the command `python` or `python3` depending on your environment.)

To query Grakn, start a Graql console: 
`grakn_examples_root $./graql console -k tube_example`
then query:
`>>> match $x isa station, has name "Covent Garden Underground Station"; get;`

Fundamental statistics: `grakn_examples $ python -m tube_network_example.src.statistics`

Basic Journey Planner `grakn_examples $ python -m tube_network_example.src.journey_planner`

Analytics and Journey Planner - Visualization `grakn_examples $ python -m tube_network_example.src.visualisation.app`

Stuck somewhere? Read on for more info on each of the parts above.

## Grakn Setup
Before you can use the example, you'll need a Grakn server running. To do this, try following the [Setup Guide](https://dev.grakn.ai/docs/get-started/setup-guide).

We need to tell Grakn the schema elements to build. In general you can do this using the graql console, but to add a whole schema it's easier to pass this to Grakn as a .gql file.
Once Grakn is running on your machine, open up a console and cd into the root directory of your Grakn installation (if you aren't there already).
Then run the following to create a keyspace called `tube_example` and add the schema to it:
```bash
grakn_examples_root $ ./grakn-dist-1.2.0/graql console -f [path-to-grakn_examples]/grakn_examples/tube_network_example/src/tube_schema.gql -k tube_example
```
If successful, you should see the response: `{}`.

## Python Setup
For this demo, you'll need Python 3.6 or greater, with the grakn package installed. 

If you are already familiar with this then all you should need is `$ pip3 install grakn` or `$ pip install grakn`. 

Otherwise, I recommend using virtualenv, to create an isolated environment.

## Downloading Data
The data necessary to build a Grakn of the Tube Network is already included in this repo, the code to acquire it can be found in [src/data_acquisition/download_dataset.py](src/data_acquisition/download_dataset.py)

## Importing Data
We can import this data into the Grakn keyspace we have just created. The name of the keyspace is set in settings.py, so you can change it there if you need to. You don't have to implement settings in this way in your own application.

Check Grakn is up and running: `./grakn server status`

To import, run [timetable_migration.py](src/migrations/timetable_migration.py), either in your IDE, or from the grakn_examples directory as follows:
```bash
grakn_examples $ python -m tube_network_example.src.migrations.timetable_migration
```

This is a custom-built python script that just iterates over the downloaded json data.   

You should see insertions being made into the Grakn, the logs for this will be created in src/migrations/logs, showing the graql query sent, and the response received.

Once complete, you have stored the tube network data in Grakn!

Now you're ready to start playing with the data.

## Querying Grakn
There are several ways to query your newly built knowledge base:
- From the graql console
- Using the Grakn Dashboard
- In code via a client (the Python client in the case of this example)

#### Graql Console
To start a graql console, run the following in the root of your running Grakn installation:
```bash
grakn_examples_root $ ./grakn-dist-1.2.0/graql console -k tube_example
```
Here `tube_example` is the keyspace into which we loaded the data.

Then try:
```bash
>>> match $x isa station, has name "Green Park Underground Station"; get;
``` 

#### Dashboard
To start the visualiser, open a browser and navigate to `http://localhost:4567`

In the Keyspace dropdown box in the top-right corner, select `tube_example` 

Then try:

```bash
>>> match $x isa station, has name "Green Park Underground Station"; get;
``` 


#### Python Client
In Python, access Grakn by instructing the Python client to send strings of graql to the server:
```python
import grakn
client = grakn.Client(uri='http://localhost:4567', keyspace='tube_example')
response = client.execute('match $x isa station, has name "Green Park Underground Station"; get;')
```



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
Remember that in the console your query can only occupy one line. In the dashboard or via a client this isn't the case.

## Statistics and Aggregation
[src/statistics.py](src/statistics.py) gives some quick examples of how you can use this Grakn functionality to analyse the network.

Run from the `grakn_examples` root directory as:
```bash
grakn_examples $ python -m tube_network_example.src.statistics
```

## Journey Planner
[src/journey_planner.py](src/journey_planner.py) demonstrates the basics of computing shortest path(s), which is elaborated in the visualisation example.

Run from the `grakn_examples` root directory as:
```bash
grakn_examples $ python -m tube_network_example.src.journey_planner
```

## Analytics - Tube Map Visualisation
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

Grakn 1.2 returns all paths between stations, rather than a graph to represent those paths. Practically this means that there is a limit on the path length we can examine with this code at present. As a result of researching this kind of use-case, very soon we will be adding functionality to retrieve a graph from `compute path`, at which point we'll be able to plan long routes. 

## Extra - Distances between Neighbouring Stations
Also included is [src/data_acquisition/distance_between_stops.py](src/data_acquisition/distance_between_stops.py) that computes the as-the-crow-flies distance between pairs of stations, where that pair are related by at least one `tunnel`. 

This could be useful for calculating the walking time between neighbouring stations, or approximating average speed of trains between stations, for example.

The script performs this calculation and then adds the distance as an attribute of the `neighbourship` relationship. That relationship was inferred by the `neighbouring` rule, but at this point must be materialised in the database in order to attach the `distance` attribute.  