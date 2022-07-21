# XCOM 2 TypeDB Example

## Introduction

[XCOM 2](https://en.wikipedia.org/wiki/XCOM_2) is a strategy game developed in 2016 by Firaxis Games, the company
co-founded by Sid Meier, creator of the acclaimed Civilization franchise. In its predecessor,
[XCOM: Enemy Unknown](https://en.wikipedia.org/wiki/XCOM:_Enemy_Unknown), Earth was ravaged by war with an unknown
alien enemy. Humanity was overwhelmed and forced to sue for peace with the alien invaders. XCOM 2 is set in 2035,
twenty years after. Your mission is to overthrow the alien rule and set humanity free.

XCOM 2 is divided into two main environments: the "battlescape", where your squad of soldiers engages with a force of
aliens at a specific location on the globe, and the "XCOM HQ", where you look after your soldiers, commission scientists
to research new technologies to combat the aliens more effectively, and hire engineers to build the new technology.

## Project Description

In this project, we use TypeDB to represent a queryable database of relevant data about XCOM technologies.

In XCOM 2, technology must be researched to unlock new weapons and equipment. For example, when the
[Beam Weapons](https://xcom.fandom.com/wiki/Beam_Weapons_(research)) research project is completed, the
[Plasma Rifle](https://xcom.fandom.com/wiki/Plasma_Rifle_(XCOM_2)) becomes available for purchase. However, techs have
prerequisites; you cannot begin research on Beam Weapons until you have finished research on
[Magnetic Weapons](https://xcom.fandom.com/wiki/Magnetic_Weapons). Moreover, you cannot study the body of an Andromedon
([Andromedon Autopsy](https://xcom.fandom.com/wiki/Andromedon_Autopsy)) without first possessing an
[Andromedon Wreck](https://xcom.fandom.com/wiki/Andromedon_Wreck).

One use case for this data in-game is: we want to display a list of 'available research projects' - that is, the
projects that are not completed, and for which we have met all of the prerequisites. We can ask TypeDB: given the
current state of the game (i.e: your current tech level and inventory), which techs are currently available for
research?

| ![research screen](images/xcom2-research-screen.jpg?raw=true) |
|:--:|
| ***There are currently 5 research projects available to work on.*** |

Another potential use-case outside the game might be: a player wants to 'beeline' (i.e: rush) towards a certain critical
tech, because they know they need it to build a powerful weapon. In this case, we can ask TypeDB: what is the minimal
set of techs required in order to unlock the Storm Gun?

Storm Gun requires Beam Weapons, which requires Elerium and Magnetic Weapons; now, what do each of those require...
You can certainly find out the answer by browsing the XCOM Wiki, but for a complex example, you may quickly find
yourself getting lost in the tree of prerequisite techs. Enter TypeDB, which, when the appropriate schema is loaded in,
is able to fetch the correct answer in one simple query.

| ![tech tree](images/tech-tree-mod.jpg?raw=true) |
|:--:|
| ***Instead of using TypeDB, we could install a game mod. But that's just not as cool, is it?*** |

## Prerequisites

* [TypeDB](https://docs.vaticle.com/docs/running-typedb/install-and-run) 2.11.1
* [Bazel](https://bazel.build/install) v5.1.1 (alternatively, declare dependencies manually and run using Maven. See [TypeDB Client Java documentation](http://docs.vaticle.com/docs/client-api/java) for details.)

## Quickstart

1. Checkout this repository: `git clone https://github.com/vaticle/typedb-examples && cd typedb-examples`
2. Start the [TypeDB Server](http://docs.vaticle.com/docs/running-typedb/install-and-run#start-the-typedb-server).
3. Build the example: `bazel build //xcom/...`.
4. Migrate the dataset into TypeDB: `bazel run //xcom:migration`.
5. Launch the interactive query runner: `bazel run //xcom:queries`.
