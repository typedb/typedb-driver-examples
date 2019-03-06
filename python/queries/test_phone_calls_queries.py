import grakn
import unittest

from python.migration.csv import migrate
import queries


class TestPhoneCallsXMLMigration(unittest.TestCase):

    @classmethod
    def setUpClass(self):
        self._client = grakn.Grakn(uri="localhost:48555")
        self._session = self._client.session(keyspace="phone_calls")
        with open('python/schema.gql', 'r') as schema:
            define_query = schema.read()
            with self._session.transaction(grakn.TxType.WRITE) as transaction:
                transaction.query(define_query)
                transaction.commit()
                print("Loaded the phone_calls schema")
                migrate.build_phone_call_graph(migrate.Inputs)
                print("Loaded the phone_calls data")


    def test_phone_calls_xml_migration(self):
        query_examples = queries.query_examples

        first_actual_answer = query_examples[0].get("query_function")("a", self._session.transaction(grakn.TxType.READ))
        first_expected_answer = [u"+370 351 224 5176", u"+54 398 559 0423", u"+62 107 530 7500", u"+63 815 962 6097",
                                 u"+7 690 597 4443", u"+263 498 495 0617", u"+81 308 988 7153", u"+81 746 154 2598"]
        self.assertItemsEqual(first_actual_answer, first_expected_answer)

        second_actual_answer = query_examples[1].get("query_function")("b", self._session.transaction(grakn.TxType.READ))
        second_expected_answer = [u"+351 272 414 6570", u"+30 419 575 7546", u"+1 254 875 4647", u"+86 892 682 0628",
                                  u"+33 614 339 0298", u"+351 515 605 7915", u"+86 922 760 0418", u"+63 808 497 1769",
                                  u"+86 825 153 5518", u"+48 894 777 5173", u"+27 117 258 4149", u"+86 202 257 8619"]
        self.assertItemsEqual(second_actual_answer, second_expected_answer)

        third_actual_answer = query_examples[2].get("query_function")("c", self._session.transaction(grakn.TxType.READ))
        third_expected_answer = [u"+86 892 682 0628", u"+54 398 559 0423"]
        self.assertItemsEqual(third_actual_answer, third_expected_answer)

        forth_actual_answer = query_examples[3].get("query_function")("d", self._session.transaction(grakn.TxType.READ))
        forth_expected_answer = [u"+261 860 539 4754", u"+81 308 988 7153", u"+62 107 530 7500", u"+261 860 539 4754",
                                 u"+81 308 988 7153", u"+62 107 530 7500"]
        self.assertItemsEqual(forth_actual_answer, forth_expected_answer)

        fifth_actual_answer = query_examples[4].get("query_function")("e", self._session.transaction(grakn.TxType.READ))
        fifth_expected_answer = [1242.7714285714285, 1699.4308943089432]
        self.assertItemsEqual(fifth_actual_answer, fifth_expected_answer)

    @classmethod
    def tearDownClass(self):
        self._client.keyspaces().delete("phone_calls")
        self._session.close()
        print("Deleted the phone_calls keyspace")


if __name__ == '__main__':
    unittest.main()
