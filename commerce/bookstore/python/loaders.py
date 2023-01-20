#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

import random, uuid
from typedb.client import TypeDB, SessionType, TransactionType
import config

debug = False  # Default value for debug verbosity flag


class Loader:  # Superclass for all loaders
    def __init__(self, item, filename, verbose = debug):
        self.item = item  # Object (line) from csv file
        self.file = filename  # Filename of the csv file to import from
        self.verbose = verbose


class BookLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "books.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a book
        return "insert $b isa book, has id '" + str(uuid.uuid4()) + "', has ISBN '" + self.item["ISBN"] + \
               "', has name '" + self.item["Book-Title"] + "', has book-author '" + self.item["Book-Author"] + \
               "', has publisher '" + self.item["Publisher"] + "', has price " + str(random.randint(3, 100)) + \
               ", has stock " + str(random.randint(0, 25)) + ";"


class UserLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "users.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a user
        first_names = ("John", "Andy", "Joe", "Bob", "Alex", "Mary", "Alexa", "Monika", "Vladimir", "Tom", "Jerry")
        typeql_insert_query = "insert $u isa user, has id '" + str(uuid.uuid4()) + "', has foreign-id '" + \
                              self.item["User-ID"] + "'"
        if self.item["Age"] != "NULL":  # Check the data before loading it
            typeql_insert_query += ",  has age " + self.item["Age"]  # If we have Age data in the file - we will use it
        else:  # Additional logic for missing data: in this case — we generate random values
            typeql_insert_query += ",  has age " + str(random.randint(18, 105))  # Add random age
        typeql_insert_query += ", has name '" + random.choice(first_names) + "';"  # Add random name
        return typeql_insert_query


class RatingLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "ratings.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a review (review relation)
        typeql_insert_query = "match $u isa user, has foreign-id '" + self.item["User-ID"] + "'; " \
                              "$b isa book, has ISBN '" + self.item["ISBN"] + "'; " \
                              "insert $r (author: $u, product: $b) isa review;" \
                              "$r has rating " + self.item["Book-Rating"] + ";"
        return typeql_insert_query


class OrderLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "orders.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert an order
        i = 0
        typeql_insert_query = "match $u isa user, has foreign-id '" + self.item["User-ID"] + "';"
        for book in random_books(self.verbose):
            i += 1  # counter for the number of books
            if self.verbose: print("Book #" + str(i) + " ISBN: " + book)
            typeql_insert_query += "$b" + str(i) + " isa book, has ISBN '" + book + "';"

        typeql_insert_query += "insert $o isa order, has id '" + self.item["id"] + "', " \
                               "has foreign-user-id '" + self.item["User-ID"] + "', has created-date " + self.item["date"] + ", " \
                               "has status '" + self.item["status"] + "'," \
                               "has delivery-address '" + self.item["delivery_address"] + "', " \
                               "has payment-details '" + self.item["payment_details"] + "';"
        typeql_insert_query += "$o ("
        for j in range(1, i+1):  # for all i books in the order
            typeql_insert_query += "item: $b" + str(j) + ","  # Add every book into the relation
        typeql_insert_query += " author: $u) isa order;"

        return typeql_insert_query


class BookGenreLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "book_genres.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a book/genre association
        typeql_insert_query = "match $b isa book, has ISBN '" + self.item["ISBN"] + "'; " \
                              "$g isa genre-tag; $g '" + self.item["Genre"] + "'; " \
                              "insert $b has $g;"
        return typeql_insert_query


class GenreLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "genres.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert genre-tags
        typeql_insert_query = "insert $g '" + self.item["Genre"] + "' isa genre-tag;"
        return typeql_insert_query


class GenreHierarchyLoader(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "genres.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert genre hierarchy
        if self.item["Genre"] != "NULL":
            typeql_insert_query = "match $g = '" + self.item["Genre"] + "'; $g isa genre-tag;" \
                                 "$p = '" + self.item["Parent"] + "'; $p isa genre-tag;" \
                                 "insert $th (sub-tag: $g, sup-tag: $p) isa tag-hierarchy;"
        else:
            typeql_insert_query = ""
        return typeql_insert_query


def random_books(verbose):
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                typeql_read_query = "match $b isa book, has ISBN $x; get $x; limit 800;"  # get 800 books
                if verbose: print("Executing TypeQL read Query: " + typeql_read_query)
                iterator = transaction.query().match(typeql_read_query)  # Execute read query
                answers = [ans.get("x") for ans in iterator]
                books = [answer.get_value() for answer in answers]  # This contains the result (800 ISBN records)
                # for order_id in range(1,6):  # Go through all 5 orders
                ordered_books = []  # Resetting variable to store ordered items for an order
                for item_n in range(1, random.randint(2, 10)):  # Iterate through random (2-9) number of books
                    ordered_books.append(books[random.randint(0, 799)])  # Select random book from 800
    return ordered_books


# This is a list of classes to import (load) data. The order of values is important for loading data order.
# Classes have filenames and corresponding methods to load the parsed data into the TypeDB.
loaders_list = [GenreLoader, GenreHierarchyLoader, BookLoader, UserLoader, RatingLoader, OrderLoader, BookGenreLoader]
