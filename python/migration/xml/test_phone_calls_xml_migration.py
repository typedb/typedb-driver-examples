import grakn
import unittest


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

    def test_phone_calls_xml_migration(self):
        import migrate

        with self._session.transaction(grakn.TxType.READ) as transaction:
            number_of_people = transaction.query("match $x isa person; get $x; count;").next().number()
            self.assertEqual(number_of_people, 30)

            number_of_companies = transaction.query("match $x isa company; get $x; count;").next().number()
            self.assertEqual(number_of_companies, 1)

            number_of_contracts = transaction.query("match $x isa contract; get $x; count;").next().number()
            self.assertEqual(number_of_contracts, 10)

            number_of_calls = transaction.query("match $x isa call; get $x; count;").next().number()
            self.assertEqual(number_of_calls, 200)

    @classmethod
    def tearDownClass(self):
        self._client.keyspaces().delete("phone_calls")
        self._session.close()
        print("Deleted the phone_calls keyspace")


if __name__ == '__main__':
    unittest.main()
