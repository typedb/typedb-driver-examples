#!/usr/bin/env python3
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

from unittest import mock, TestCase
import load_data
import requests
import config
from typedb.client import TypeDB, SessionType, TransactionType


class LoadDataTests(TestCase):
    @mock.patch('load_data.input', create=True)
    def test_load_data(self, mocked_input):
        mocked_input.side_effect = ['delete']
        result = load_data.main()
        self.assertEqual(result, [16, 16, 809, 43, 110, 5, 8])  # Expected results for:
        # GenreLoader = 16
        # GenreHierarchyLoader = 16
        # BookLoader = 809
        # UserLoader = 43
        # RatingLoader = 110
        # OrderLoader = 5
        # BookGenreLoader = 8


class RequestTests(TestCase):
    @mock.patch('requests.input', create=True)
    def test_request_all_books(self, mocked_input):
        mocked_input.side_effect = ["1", ""]  # 1. Search for a book -> All
        result = requests.main()
        self.assertEqual(result, 809)  # Expected result is 809 (number of books)

    @mock.patch('requests.input', create=True)
    def test_request_one_books(self, mocked_input):
        mocked_input.side_effect = ["1", "0375410538"]  # Search for a book -> ISBN = 0375410538
        result = requests.main()
        self.assertEqual(result, [1, 3.5])  # Expected result is 1 book and 3.5 rating

    @mock.patch('requests.input', create=True)
    def test_request_all_users(self, mocked_input):
        mocked_input.side_effect = ["2", ""]  # 2. Search for a user -> All
        result = requests.main()
        self.assertEqual(result, 43)  # Expected result is 43 (number of users)

    @mock.patch('requests.input', create=True)
    def test_request_one_user(self, mocked_input):
        user_foreign_id = "88"
        mocked_input.side_effect = ["2", user_foreign_id]  # 2. Search for a user -> Foreign ID: 88
        result = requests.main()  # we get the result from requests.py
        expected_result = []  # we get the result of our own request here
        with TypeDB.core_client(config.typedb_server_addr) as client:  # Establishing connection
            with client.session(config.db, SessionType.DATA) as session:  # Access data in the database
                with session.transaction(TransactionType.READ) as transaction:  # Open transaction to read
                    typeql_read_query = "match $u isa user, has id $i, has name $n, " \
                                        "has foreign-id '" + user_foreign_id + "'; get $i, $n;"
                    iterator = transaction.query().match(typeql_read_query)  # Executing query
                    for item in iterator:  # Iterating through results
                        expected_result.append(item.get("i").get_value())  # We get every user with the foreign_id

        self.assertEqual(result, expected_result)  # we compare requests.py result with our own request result

    @mock.patch('requests.input', create=True)
    def test_request_all_orders(self, mocked_input):
        mocked_input.side_effect = ["3", ""]  # 3. Search for an order -> All
        result = requests.main()
        self.assertEqual(result, 5)  # Expected result is 5 (number of orders)

    @mock.patch('requests.input', create=True)
    def test_request_one_order(self, mocked_input):
        mocked_input.side_effect = ["3", "1"]  # 3. Search for an order -> Order ID: 1
        result = requests.main()
        self.assertEqual(result, 1)  # Expected result is 1 (number of orders)

    @mock.patch('requests.input', create=True)
    def test_request_tag(self, mocked_input):
        mocked_input.side_effect = ["4", "Non fiction"]  # 4. Search for books by genre -> Non fiction
        result = requests.main()
        self.assertEqual(result, 3)  # Expected result is 3 (number of books found with the tag and all inferenced tags)


if __name__ == "__main__":
    print("Try using the following command: python3 -m unittest")
