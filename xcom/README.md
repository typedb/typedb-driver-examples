# XCOM 2 Grakn Example

## Introduction

[XCOM 2](https://en.wikipedia.org/wiki/XCOM_2) is a strategy game developed in 2016 by Firaxis Games, the company co-founded by Sid Meier, creator of the acclaimed Civilization franchise. In its predecessor, [XCOM: Enemy Unknown](https://en.wikipedia.org/wiki/XCOM:_Enemy_Unknown), Earth was ravaged by war with an unknown alien enemy. Humanity was overwhelmed and forced to sue for peace with the alien invaders. XCOM 2 is set in 2035, twenty years after. Your mission is to overthrow the alien rule and set humanity free.

XCOM 2 is divided into two main environments: the "battlescape", where your squad of soldiers engages with a force of aliens at a specific location on the globe, and the "XCOM HQ", where you look after your soldiers, commission scientists to research new technologies to combat the aliens more effectively, and hire engineers to build the new technology.

## Project Description

In this project, we use Grakn to represent a queryable database of relevant data about XCOM technologies.

In XCOM 2, technology must be researched to unlock new weapons and equipment. For example, when the [Beam Weapons](https://xcom.fandom.com/wiki/Beam_Weapons_(research)) research project is completed, the [Plasma Rifle](https://xcom.fandom.com/wiki/Plasma_Rifle_(XCOM_2)) becomes available for purchase. However, techs have prerequisites; you cannot begin research on Beam Weapons until you have finished research on [Magnetic Weapons](https://xcom.fandom.com/wiki/Magnetic_Weapons).

One use case for this data in-game is: we want to display a list of 'available research projects' - that is, the projects that are not completed, and for which we have met all of the prerequisites. We can ask Grakn: given the current state of the game (i.e: the set of techs you have already researched), which techs are currently available for research?

Another potential use-case outside the game might be: a player wants to 'beeline' (i.e: rush) towards a certain critical tech, because they know they need it to build a powerful weapon. In this case, we can ask Grakn: what is the minimal set of techs required in order to unlock the Storm Gun?

Storm Gun requires Beam Weapons, which requires Elerium and Magnetic Weapons; now, what do each of those require... You can certainly find out the answer by browsing the XCOM Wiki, but for a complex example, you may quickly find yourself getting lost in the tree of prerequisite techs. Enter Grakn, which, when the appropriate schema is loaded in, is able to fetch the correct answer in one simple query.

### Quickstart
1. Install the latest version of [Grakn](https://github.com/graknlabs/grakn/releases) that is compatible with the latest version of Client Java (see the [dependency table](http://dev.grakn.ai/docs/client-api/java#dependencies))
2. Clone this repository
3. Start the [Grakn Server](http://dev.grakn.ai/docs/running-grakn/install-and-run#start-the-grakn-server).
4. Via terminal while inside the Grakn distribution, run: `./grakn console -k xcom -f path-to-the-cloned-repo/schemas/xcom-schema.gql`
5. Install [Bazel](https://docs.bazel.build/versions/master/install.html). (you may also declare dependencies and run using Maven. See [Grakn Client Java documentation](http://dev.grakn.ai/docs/client-api/java).)
6. To install all the dependencies, run `bazel build //...`.
7. To migrate the dataset into Grakn, run:
- `bazel run //xcom/java:migration`
8. To initiate the interactive query runner, run `bazel run //xcom/java:queries`.
