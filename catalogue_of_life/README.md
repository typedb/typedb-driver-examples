# Catalogue of Life TypeDB example

[Catalogue of Life](https://www.catalogueoflife.org/) is a database of over 4.5 million currently known taxa in biology,
compiled from over a hundred different sources. 

In this example we use [TypeDB-Loader](https://github.com/typedb-osi/typedb-loader) to load this dataset into TypeDB,
which enables us to leverage the rule-based inference capabilities of TypeDB and TypeQL.

## Prerequisites

* System requirements: at least 16 GB of _free_ RAM (this is due to the size of the dataset)
* [TypeDB](https://docs.vaticle.com/docs/running-typedb/install-and-run) v2.11.1
* [TypeDB Loader](https://github.com/typedb-osi/typedb-loader) v1.2.0
* [Bazel](https://bazel.build/install) v5.1.1
* An Internet connection

## Quickstart

1. Checkout this repository: `git clone https://github.com/vaticle/typedb-examples && cd typedb-examples`
2. Start the [TypeDB Server](http://docs.vaticle.com/docs/running-typedb/install-and-run#start-the-typedb-server).
3. Fetch the data and load it into TypeDB: `bazel run //catalogue_of_life:loader`
4. Start TypeDB Console and explore the data. If TypeDB was installed via a package manager, the command is `typedb console --server=localhost:1729`; otherwise, see the docs on [running TypeDB Console.](https://docs.vaticle.com/docs/console/console)

### Example queries

#### Look up a scientific name by a common name

```
> transaction catalogue-of-life data read --infer true
catalogue-of-life::data::read> match (named: $t, name: $n) isa naming;
                               $n has name "Wolf", has language "eng";
                               $t has scientific-name $t-sn;
                               get $t-sn;

{ $t-sn "Canis lupus" isa scientific-name; }
answers: 1, total (with concept details) duration: 32 ms

catalogue-of-life::data::read> match (named: $t, name: $n) isa naming;
                               $n has name "ビレイカギカイメン";
                               $t has scientific-name $t-sn;
                               get $t-sn;

{ $t-sn "Gelliodes callista" isa scientific-name; }
answers: 1, total (with concept details) duration: 31 ms
```

#### List all families of birds

```
> transaction catalogue-of-life data read --infer true
catalogue-of-life::data::read> match
                               (ancestor: $a, descendant: $d) isa ancestry;
                               $a has scientific-name "Aves";
                               $d has taxon-rank "family", has taxon-status "accepted";
                               $d has scientific-name $d-sn;
                               get $d-sn; limit 15;
{ $d-sn "Apterygidae" isa scientific-name; }
{ $d-sn "Psittacidae" isa scientific-name; }
...
```

#### The common taxon of humans (Homo sapiens) and axolotls (Ambystoma mexicanum)

```
> transaction catalogue-of-life data read --infer true
catalogue-of-life::data::read> match
                               (ancestor: $a, descendant: $x, descendant: $y) isa common-taxon;
                               $x has scientific-name "Homo sapiens";
                               $y has scientific-name "Ambystoma mexicanum";
                               $a has scientific-name $a-sn, has taxon-rank $a-rank;
                               get $a-sn, $a-rank;
{
    $a-rank "unranked" isa taxon-rank;
    $a-sn "Tetrapoda" isa scientific-name;
}
answers: 1, total (with concept details) duration: 32 ms
```

## How it works

### Schema

#### Attributes, Entities, & Relations

_Attributes_ are where the data is actually stored in TypeDB, and _entities_ define collections of these attributes.
An entity can have any number of attributes of the same type associated with it. That's how, for instance, a taxon
in this example can be said to inhabit multiple environments (e.g. marine and freshwater).

Entities can be connected by _relations_. The entities play predefined roles in those relations. Relations are what
connects a taxon to its common names in different languages, or define the taxonomic hierarchy all the way from life in
general (Biota) down to a plant variety.

#### Rules

Rules provide a way to infer a relation from other data. These relations aren't stored anywhere between transactions,
which ensures they are never stale. You can think of them as macros that enable you to write more powerful and readable
queries. Inference is a pretty computationally demanding operation, however, so it's gated behind a
parameter (`--infer`) in the console client.

Each rule defines a set of prerequisites and the conclusion that follows. For example, a simple rule that ensures
ancestry is transitive could be defined like so:

```typeql
rule child-of-descendant-is-descendant:
when {
    (ancestor: $a, descendant: $p) isa ancestry;
    (parent: $p, child: $c) isa parenthood;
} then {
    (ancestor: $a, descendant: $c) isa ancestry;
};
```

In perhaps more familiar terms this rule can be read as "when A is an ancestor of P, and P is a parent of C, then A is
an ancestor of C".

You can read about TypeDB schemas in more detail in
[the official documentation](https://docs.vaticle.com/docs/schema/overview).

### TypeDB-Loader

[TypeDB-Loader](https://github.com/typedb-osi/typedb-loader) provides a consistent way of loading large amounts of data
into TypeDB. In this example the database is forcefully recreated from scratch every time loader is called!

The two main parts of the TypeDB-Loader configuration specify the way for it to load entites and their relations. Each
such specification is called a _generator_.

Entity generator specifications are relatively simple. TypeDB-Loader needs to know where to get the `data`,
which `entity` type it is `insert`ing, and in which `column`s to look for the values of the `attribute`s the entity
owns (`ownerships`). If a column in your table is a list, TypeDB-Loader can extract each item into a separate attribute 
using the `listSeparator`.

```json
"taxon": {
  "data": [
    "coldp/NameUsage.tsv"
  ],
  "insert": {
    "entity": "vernacular-name",
    "ownerships": [
      {
        "attribute": "scientific-name",
        "column": "col:scientificName"
      },
      {
        "attribute": "environment",
        "column": "col:environment",
        "listSeparator": ","
      },
      ...
    ]
  }
},
```

Relation generators are only a little more involved. For each of the `role` `players` of the `relation`, TypeDB-Loader 
looks up an entity with a given `type`, such that a certain `attribute` it has matches the specified `column` of your 
`data` file. If a certain `role` is `required` to be fulfilled but did not match any entity in the database, the data 
line is ignored.

```json
"parenthood": {
  "data": [
    "coldp/NameUsage.tsv"
  ],
  "insert": {
    "relation": "parenthood",
    "players": [
      {
        "role": "parent",
        "required": true,
        "match": {
          "type": "taxon",
          "ownerships": [
            {
              "attribute": "taxon-id",
              "column": "col:parentID"
            }
          ]
        }
      },
      ...
    ]
  }
},
```

The detailed documentation on TypeDB-Loader can be found [here](https://github.com/typedb-osi/typedb-loader/wiki).

## Future improvements

* Formalize synonymy between taxa;
* Parse location data as much as possible;
* Fetch `mrgid` region data;
* Parse `source/*.yaml` into the database.
