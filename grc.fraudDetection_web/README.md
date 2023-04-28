# fraudDetection_web

This project uses Quarkus, the Supersonic Subatomic Java Framework in order to let us present results with GraphQL.
In this project, we use TypeDB to represent a queryable database of relevant fraud detection data about some transactions between Cardholders and Companies.

## Introduction

We have an imaginary transaction dataset and our application implements some very basic functions:

- Search for Bank
- Search for Cardholder
- Search for Company
- Search for Transaction 

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
String getQueryStr = "match " + TX_MATCH + CardholderDao.CARDHOLDER_MATCH + BankDao.BANK_MATCH + MerchantDao.MERCHANT_MATCH;
```

## Running the application in dev mode

1. Checkout this repository: `git clone https://github.com/vaticle/typedb-examples && cd typedb-examples/grc.fraudDetection_web`.
2. Start the [TypeDB Server](http://docs.vaticle.com/docs/running-typedb/install-and-run#start-the-typedb-server). Check that it's listening to address: `0.0.0.0:1729`.
3. You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only
> at http://localhost:8080/q/dev/.

4. You can then choose the GraphQL UI.
5. You can now use this interface to query the database (you can find all available queries in the top right corner).

## TypeDB Description

### Schema

The schema is stored in the `schema.tql` file under [src/main/resources/](src/main/resources/schema.tql).

#### Attributes

The bookstore schema has the following attributes:

- name (string)
- first_name (string)
- last_name (string) 
- job (string) 
- gender (string -> regex) 
- company_type (string) 
- transaction_type (string) 
- street (string) 
- city (string) 
- state (string) 
####
- timestamp (datetime) 
- date_of_birth (datetime) 
####
- card_number (long)  
####
- amount (double)
- longitude (double)
- latitude (double)

#### Entities

The bookstore schema has the following entities:

- Geo_coordinate
- Address
- Card
- Person
- Company
- Bank

#### Relations

The bookstore schema has the following relations:

- bank_account
- transaction
- geolocate
  - locate
- customer_relationship
- unsafe_relationship
- same_place

#### Rules

The fraudDetection schema has three rules to demonstrate rules usability.

The first one is here to create a direct relation between CardHolder and Company with a Transaction exists between them.
```
rule isa_customer_relationship:
    when {
        (owner: $per, attached_card: $car, $gar) isa bank_account;
        (used_card: $car, to: $com) isa transaction;
    } then {
        (buyer: $per, company: $com) isa customer_relationship;
    };
```

The second one works to create a relation between every Company and Person that are in the same geographical area.

```
rule isa_same_place:
    when {
        $geo1 isa Geo_coordinate, has longitude $l1, has latitude $l2;
        $geo2 isa Geo_coordinate, has longitude $l1, has latitude $l2;
        (geo: $geo1, identify: $per, $arg) isa locate;
        (geo: $geo2, identify: $com) isa geolocate;
        $com isa Company;
        $per isa Person;
    } then {
        (person: $per, company: $com, located_com: $geo2, located_per: $geo1) isa same_place;
    };
```


This third rule is using the relation created by the previous rule in order to find Transaction that are happening between
Person and Company that are not is the same area, marking them as potentially not safe
```
rule is_not_safe:
    when {
        $per isa Person;
        $com isa Company;
        (buyer: $per, company: $com) isa customer_relationship;
        not{
            (person: $per, company: $com) isa same_place;
        };
    } then {
        (unsafe_buyer: $per, unsafe_company: $com) isa unsafe_relationship;
    };
```