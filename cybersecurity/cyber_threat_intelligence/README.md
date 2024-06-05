# Fraud Detection TypeDB Example

This project utilizes Spring Boot and GraphQL to access a TypeDB database filled with Cyber Threat Intelligence (CTI) related dataset. 
The application provides a GraphQL API to interact with the CTI data stored in the TypeDB database.


## Introduction

We have a MITRE ATTACK dataset and our application implements some research functions:

- Search entities
- Search relations
- Search schema


We are demonstrating sub-typing, powerful rules and rule combination in our schema.
####
We can also see how easy it is to create complex queries using query composition.
####
We are using the DAO design pattern, we have the following components on which our design depends:

- The model which is transferred from one layer to the other.
- The interfaces which provide a flexible design.
- The interface implementation which is a concrete implementation of the persistence logic.

The query composition can be observed in all DAOs.
```java
String search = "$ta has " + type + " = " + name + ";";
String getQueryStr = "match " + ATTRIBUTED_TO_MATCH + search + "group $id;";
```

## Running the application in dev mode

1. Checkout this repository: `git clone https://github.com/vaticle/typedb-driver-examples && cd typedb-driver-examples/cybersecurity/cyber_threat_intelligence`.
2. Start the [TypeDB Server](http://docs.vaticle.com/docs/running-typedb/install-and-run#start-the-typedb-server). Check that it's listening to address: `0.0.0.0:1729`.
3. You can run your application in dev mode that enables live coding using (you will need at least Java19):
```shell script
mvn clean install
```
4. Run the Spring Boot application: 
```
mvn spring-boot:run.
```
5. The GraphQL API will be available at http://localhost:8080/graphiql.
6. The REST API will be available at ```{{base_url}}```
7. You can now use the chosen interface to query the database.

## TypeDB Description

### Schema

The schema is stored in the `schema_CTI.tql` file under [src/main/resources/](src/main/resources/schema_CTI.tql).

#### Entities

The schema has the following entities:

- stix_core_object
  - stix_sub_object
    - kill_chain_phase
  - stix_cyber_observable_object
    - file
  - stix_domain_object
    - identity
      - class
      - group
      - idUnknown
      - individual
      - system
    - indicator
    - malware
    - threat_actor

#### Relations

The schema has the following relations:
- stix_core_relationship
  - attributed_to
  - created_by
  - hashes
  - impersonates
  - indicates
  - kill_chain_phases
  - sightings
  - targets
  - uses

#### Rules

The fraudDetection schema has three rules to demonstrate rules usability.

The first one is here to create a transitive uses relation between three stix_domain_object linked by uses relations.

```
rule transitive_use:
    when {
        $x isa stix_domain_object, has name $name1;
        $y isa stix_domain_object, has name $name2;
        $z isa stix_domain_object, has name $name3;
        $use1 (used_by: $x, used: $y) isa uses;
        $use2 (used_by: $y, used: $z) isa uses;
    } then {
        (used_by: $x, used: $z) isa uses;
    };
```
The second one works to create a uses relation between two stix_domain_object that are linked by an attributed_to relation and a uses relation.

```
rule attributed_to_when_using:
    when {
        (attributing: $x, attributed: $y) isa attributed_to;
        (used_by: $y, used: $z) isa uses;
    } then {
        (used_by: $x, used: $z) isa uses;
    };
```


The second one works to create a targets relation between two stix_domain_object that are linked by an attributed_to relation and a targets relation.
```
rule attributed_to_when_targeting:
    when {
        (attributing: $x, attributed: $y) isa attributed_to;
        (targeting: $y, targeted: $z) isa targets;
    } then {
        (targeting: $x, targeted: $z) isa targets;
    };
```

