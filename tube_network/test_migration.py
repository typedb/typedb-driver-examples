import unittest
from typedb.client import TypeDB, SessionType, TransactionType

from tube_network.src import migration


class Test(unittest.TestCase):

    def setUp(self):
        with TypeDB.core_client("localhost:1729") as client:
            client.databases().create("tube_network")
            with client.session("tube_network", SessionType.SCHEMA) as session:
                with open('schemas/tube-network-schema.gql', 'r') as schema:
                    define_query = schema.read()
                    with session.transaction(TransactionType.WRITE) as transaction:
                        transaction.query().define(define_query)
                        transaction.commit()
                        print("Loaded the tube_network schema")

    def test_migration(self):
        migration.init()
        self.assert_migration_results()

    def assert_migration_results(self):
        with TypeDB.core_client("localhost:1729") as client:
            with client.session("tube_network", SessionType.DATA) as session:
                with session.transaction(TransactionType.READ) as tx:
                    number_of_stations = tx.query().match_aggregate(
                        "match $x isa station; get $x; count;").get().as_int()
                    self.assertEqual(number_of_stations, 271)

                    number_of_tube_lines = tx.query().match_aggregate(
                        "match $x isa tube-line; get $x; count;").get().as_int()
                    self.assertEqual(number_of_tube_lines, 11)

                    number_of_route_sections = tx.query().match_aggregate(
                        "match $x isa route-section; get $x; count;").get().as_int()
                    self.assertEqual(number_of_route_sections, 885)

                    number_of_routes = tx.query().match_aggregate(
                        "match $x isa route; get $x; count;").get().as_int()
                    self.assertEqual(number_of_routes, 39)

                    number_of_tunnels = tx.query().match_aggregate(
                        "match $x isa tunnel; get $x; count;").get().as_int()
                    self.assertEqual(number_of_tunnels, 907)

                    number_of_zones = tx.query().match_aggregate(
                        "match $x isa zone; get $x; count;").get().as_int()
                    self.assertEqual(number_of_zones, 9)

    def tearDown(self):
        with TypeDB.core_client("localhost:1729") as client:
            client.databases().get("tube_network").delete()
        print("Deleted the tube_network keyspace")


if __name__ == '__main__':
    unittest.main()
