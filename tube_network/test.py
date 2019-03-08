import grakn
import unittest

from tube_network.src import migration, statistics, journey_planner


class Test(unittest.TestCase):

    def setUp(self):
        client = grakn.GraknClient(uri="localhost:48555")
        with client.session(keyspace="tube_network") as session:
            with open('schemas/tube-network-schema.gql', 'r') as schema:
                define_query = schema.read()
                with session.transaction().write() as transaction:
                    transaction.query(define_query)
                    transaction.commit()
                    print("Loaded the tube_network schema")
        client.close()

    def test_migration(self):
        migration.init()
        self.assert_migration_results()

    def test_statistics(self):
        migration.init()

        query_examples = statistics.query_examples

        client = grakn.GraknClient(uri="localhost:48555")

        with client.session(keyspace="tube_network") as session:
            self.assertEqual(
                query_examples[0].get("query_function")("", session.transaction().read()),
                271
            )

            self.assertEqual(
                query_examples[1].get("query_function")("", session.transaction().read()),
                1.0
            )

            self.assertItemsEqual(
                query_examples[2].get("query_function")("", session.transaction().read()),
                [51.402142, [u'Morden Underground Station']]
            )

            self.assertItemsEqual(
                query_examples[3].get("query_function")("", session.transaction().read()),
                [9.0, [[u'Chesham Underground Station', u'Chalfont & Latimer Underground Station', u'Metropolitan', u'Chesham Underground Station', u'Aldgate Underground Station']]]
            )

            self.assertEqual(
                query_examples[4].get("query_function")("", session.transaction().read()),
                2.31864406779661
            )

            self.assertEqual(
                query_examples[5].get("query_function")("", session.transaction().read()),
                2.0
            )

            self.assertEqual(
                query_examples[6].get("query_function")("", session.transaction().read()),
                1.0437559149702726
            )

    def test_journey_planner(self):
        migration.init()

        client = grakn.GraknClient(uri="localhost:48555")

        with client.session(keyspace="tube_network") as session:
            # not containing 'Underground Station'
            # not Title Case
            # fewest stops
            self.assertItemsEqual(
                journey_planner.find_path(
                    session,
                    journey_planner.get_station_by_name(session, "Green Park"),  # not containing 'Underground Station'
                    journey_planner.get_station_by_name(session, "holloway road"),  # not Title Case
                    "stops"
                ),
                [u'Green Park Underground Station', u'Oxford Circus Underground Station', u'Warren Street Underground Station',
                 u'Euston Underground Station', u"King's Cross St. Pancras Underground Station",
                 u'Caledonian Road Underground Station', u'Holloway Road Underground Station']
            )

            # fewest route changes
            self.assertItemsEqual(
                journey_planner.find_path(
                    session,
                    journey_planner.get_station_by_name(session, "Green Park"),
                    journey_planner.get_station_by_name(session, "holloway road"),
                    "routes"
                ),
                # TODO: make sure this is actually the right answer
                [u'Green Park Underground Station', u'Holloway Road Underground Station']
            )

    def assert_migration_results(self):
        client = grakn.GraknClient(uri="localhost:48555")

        with client.session(keyspace="tube_network") as session:
            with session.transaction().read() as transaction:
                number_of_stations = transaction.query("match $x isa station; get $x; count;").next().number()
                self.assertEqual(number_of_stations, 271)

                number_of_tube_lines = transaction.query("match $x isa tube-line; get $x; count;").next().number()
                self.assertEqual(number_of_tube_lines, 11)

                number_of_route_sections = transaction.query("match $x isa route-section; get $x; count;").next().number()
                self.assertEqual(number_of_route_sections, 885)

                number_of_routes = transaction.query("match $x isa route; get $x; count;").next().number()
                self.assertEqual(number_of_routes, 39)

                number_of_tunnels = transaction.query("match $x isa tunnel; get $x; count;").next().number()
                self.assertEqual(number_of_tunnels, 907)

                number_of_zones = transaction.query("match $x isa zone; get $x; count;").next().number()
                self.assertEqual(number_of_zones, 9)
        client.close()

    def tearDown(self):
        client = grakn.GraknClient(uri="localhost:48555")
        client.keyspaces().delete("tube_network")
        client.close()
        print("Deleted the tube_network keyspace")


if __name__ == '__main__':
    unittest.main()
