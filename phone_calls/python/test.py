#!/usr/bin/env python3

from grakn.client import GraknClient

import unittest

import migrate_csv
import migrate_json
import migrate_xml
import queries


keyspace_name = "phone_calls_python"
data_path = "datasets/phone-calls/"

class Test(unittest.TestCase):

    def setUp(self):
        self._client = GraknClient(uri="localhost:48555")
        self._session = self._client.session(keyspace=keyspace_name)
        with open('schemas/phone-calls-schema.gql', 'r') as schema:
            define_query = schema.read()
            with self._session.transaction().write() as transaction:
                transaction.query(define_query)
                transaction.commit()
                print("Loaded the " + keyspace_name + " schema")

    def test_csv_migration(self):
        migrate_csv.build_phone_call_graph(migrate_csv.Inputs, data_path, keyspace_name)
        self.assert_migration_results()

    def test_json_migration(self):
        migrate_json.build_phone_call_graph(migrate_json.Inputs, data_path, keyspace_name)
        self.assert_migration_results()

    def test_xml_migration(self):
        migrate_xml.build_phone_call_graph(migrate_xml.Inputs, data_path, keyspace_name)
        self.assert_migration_results()

    def test_queries(self):
        queries.process_selection(0, keyspace_name)

        migrate_csv.build_phone_call_graph(migrate_csv.Inputs, data_path, keyspace_name)

        with self._session.transaction().read() as transaction:
            first_actual_answer = queries.query_examples[0].get("query_function")("", transaction)
            first_expected_answer = ["+370 351 224 5176", "+54 398 559 0423", "+62 107 530 7500", "+63 815 962 6097",
                                     "+7 690 597 4443", "+263 498 495 0617", "+81 308 988 7153", "+81 746 154 2598"]
            self.assertCountEqual(first_actual_answer, first_expected_answer)

            second_actual_answer = queries.query_examples[1].get("query_function")("", transaction)
            second_expected_answer = ["+351 272 414 6570", "+30 419 575 7546", "+1 254 875 4647", "+86 892 682 0628",
                                      "+33 614 339 0298", "+351 515 605 7915", "+86 922 760 0418", "+63 808 497 1769",
                                      "+86 825 153 5518", "+48 894 777 5173", "+27 117 258 4149", "+86 202 257 8619"]
            self.assertCountEqual(second_actual_answer, second_expected_answer)

            third_actual_answer = queries.query_examples[2].get("query_function")("", transaction)
            third_expected_answer = ["+86 892 682 0628", "+54 398 559 0423"]
            self.assertCountEqual(third_actual_answer, third_expected_answer)

            forth_actual_answer = queries.query_examples[3].get("query_function")("", transaction)
            forth_expected_answer = ["+261 860 539 4754", "+81 308 988 7153", "+62 107 530 7500", "+261 860 539 4754",
                                     "+81 308 988 7153", "+62 107 530 7500"]
            self.assertCountEqual(forth_actual_answer, forth_expected_answer)

            fifth_actual_answer = queries.query_examples[4].get("query_function")("", transaction)
            fifth_expected_answer = [1242.7714285714285, 1699.4308943089432]
            self.assertCountEqual(fifth_actual_answer, fifth_expected_answer)

    def assert_migration_results(self):
        with self._session.transaction().read() as transaction:
            number_of_people = next(transaction.query("match $x isa person; get $x; count;")).number()
            self.assertEqual(number_of_people, 30)

            number_of_companies = next(transaction.query("match $x isa company; get $x; count;")).number()
            self.assertEqual(number_of_companies, 1)

            number_of_contracts = next(transaction.query("match $x isa contract; get $x; count;")).number()
            self.assertEqual(number_of_contracts, 10)

            number_of_calls = next(transaction.query("match $x isa call; get $x; count;")).number()
            self.assertEqual(number_of_calls, 200)

    def tearDown(self):
        self._client.keyspaces().delete(keyspace_name)
        self._session.close()
        self._client.close()
        print("Deleted the " + keyspace_name + " keyspace")


if __name__ == '__main__':
    unittest.main()
