import grakn
import unittest

from tube_network.src import migration, statistics, journey_planner, app


class Test(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        client = grakn.GraknClient(uri="localhost:48555")
        with client.session(keyspace="tube_network") as session:
            with open('schemas/tube-network-schema.gql', 'r') as schema:
                define_query = schema.read()
                with session.transaction().write() as transaction:
                    transaction.query(define_query)
                    transaction.commit()
                    print("Loaded the tube_network schema")
        client.close()
        migration.init()
        print("Loaded the tube_network data")

    def test_statistics(self):
        query_examples = statistics.query_examples

        client = grakn.GraknClient(uri="localhost:48555")

        with client.session(keyspace="tube_network") as session:
            with session.transaction().read() as transaction:
                self.assertEqual(
                    query_examples[0].get("query_function")("", transaction),
                    271
                )

                self.assertEqual(
                    query_examples[1].get("query_function")("", transaction),
                    1.0
                )

                self.assertItemsEqual(
                    query_examples[2].get("query_function")("", transaction),
                    [51.402142, [u'Morden Underground Station']]
                )

                self.assertItemsEqual(
                    query_examples[3].get("query_function")("", transaction),
                    [9.0, [[u'Chesham Underground Station', u'Chalfont & Latimer Underground Station', u'Metropolitan',
                            u'Chesham Underground Station', u'Aldgate Underground Station']]]
                )

                self.assertEqual(
                    query_examples[4].get("query_function")("", transaction),
                    2.314317673378076
                )

                self.assertEqual(
                    query_examples[5].get("query_function")("", transaction),
                    2.0
                )

                self.assertEqual(
                    query_examples[6].get("query_function")("", transaction),
                    1.0437559149702726
                )

        client.close()

    def test_journey_planner(self):
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

        client.close()

    def test_visualisation_queries(self):
        client = grakn.GraknClient(uri="localhost:48555")

        with client.session(keyspace="tube_network") as session:
            app.TubeGui(session)

        client.close()

    @classmethod
    def tearDownClass(cls):
        client = grakn.GraknClient(uri="localhost:48555")
        client.keyspaces().delete("tube_network")
        client.close()
        print("Deleted the tube_network keyspace")

if __name__ == '__main__':
    unittest.main()
