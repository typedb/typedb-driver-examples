from grakn.client import GraknClient
import unittest
from tube_network.src import migration


class Test(unittest.TestCase):

    def setUp(self):
        with GraknClient(uri="localhost:48555") as client:
            with client.session(keyspace="tube_network") as session:
                with open('schemas/tube-network-schema.gql', 'r') as schema:
                    define_query = schema.read()
                    with session.transaction().write() as transaction:
                        transaction.query(define_query)
                        transaction.commit()
                        print("Loaded the tube_network schema")

    def test_migration(self):
        migration.init()
        self.assert_migration_results()

    def assert_migration_results(self):
        with GraknClient(uri="localhost:48555") as client:
            with client.session(keyspace="tube_network") as session:
                with session.transaction().read() as transaction:
                    number_of_stations = next(transaction.query("match $x isa station; get $x; count;")).number()
                    self.assertEqual(number_of_stations, 271)

                    number_of_tube_lines = next(transaction.query("match $x isa tube-line; get $x; count;")).number()
                    self.assertEqual(number_of_tube_lines, 11)

                    number_of_route_sections = next(transaction.query("match $x isa route-section; get $x; count;")).number()
                    self.assertEqual(number_of_route_sections, 885)

                    number_of_routes = next(transaction.query("match $x isa route; get $x; count;")).number()
                    self.assertEqual(number_of_routes, 39)

                    number_of_tunnels = next(transaction.query("match $x isa tunnel; get $x; count;")).number()
                    self.assertEqual(number_of_tunnels, 907)

                    number_of_zones = next(transaction.query("match $x isa zone; get $x; count;")).number()
                    self.assertEqual(number_of_zones, 9)

    def tearDown(self):
        with GraknClient(uri="localhost:48555") as client:
            client.keyspaces().delete("tube_network")
        print("Deleted the tube_network keyspace")


if __name__ == '__main__':
    unittest.main()
