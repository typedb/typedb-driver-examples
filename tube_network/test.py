import unittest
from typedb.client import TypeDB, SessionType, TransactionType

from tube_network.src import migration, statistics, journey_planner, app


class Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        with TypeDB.core_client("localhost:1729") as client:
            client.databases().create("tube_network")
            with client.session("tube_network", SessionType.SCHEMA) as session:
                with open('schemas/tube-network-schema.gql', 'r') as schema:
                    define_query = schema.read()
                    with session.transaction(TransactionType.WRITE) as transaction:
                        transaction.query().define(define_query)
                        transaction.commit()
                        print("Loaded the tube_network schema")
        migration.init()
        print("Loaded the tube_network data")

    def test_statistics(self):
        statistics.init(0)

        query_examples = statistics.query_examples

        with TypeDB.core_client("localhost:1729") as client:
            with client.session("tube_network", SessionType.DATA) as session:
                with session.transaction(TransactionType.READ) as transaction:
                    self.assertEqual(
                        query_examples[0].get("query_function")("", transaction),
                        271
                    )

                    # TODO(vmax): enable when compute queries are supported again
                    """
                    self.assertEqual(
                        query_examples[1].get("query_function")("", transaction),
                        1.0
                    )

                    self.assertEqual(
                        query_examples[2].get("query_function")("", transaction),
                        [51.402142, [u'Morden Underground Station']]
                    )

                    self.assertEqual(
                        query_examples[3].get("query_function")("", transaction),
                        [9.0, [[u'Chesham Underground Station', u'Chalfont & Latimer Underground Station',
                                u'Metropolitan', u'Chesham Underground Station', u'Aldgate Underground Station']]]
                    )

                    self.assertEqual(
                        query_examples[4].get("query_function")("", transaction),
                        2.31864406779661
                    )

                    self.assertEqual(
                        query_examples[5].get("query_function")("", transaction),
                        2.0
                    )

                    self.assertEqual(
                        query_examples[6].get("query_function")("", transaction),
                        1.0437559149702726
                    )
                    """

    @unittest.skip('enable when compute queries are supported again')
    def test_journey_planner(self):
        journey_planner.init(from_station_name="Green Park", to_station_name="Holloway Road", selected_path_strategy=1)

        with TypeDB.core_client("localhost:1729") as client:
            with client.session("tube_network", SessionType.DATA) as session:
                # not containing 'Underground Station'
                # not Title Case
                # fewest stops
                self.assertEqual(
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
                self.assertEqual(
                    journey_planner.find_path(
                        session,
                        journey_planner.get_station_by_name(session, "Green Park"),
                        journey_planner.get_station_by_name(session, "holloway road"),
                        "routes"
                    ),
                    # TODO: make sure this is actually the right answer
                    [u'Green Park Underground Station', u'Holloway Road Underground Station']
                )

    @unittest.skip('enable when compute queries are supported again')
    def test_visualisation_queries(self):
        app.init(False)

        with TypeDB.core_client("localhost:1729") as client:
            with client.session("tube_network", SessionType.DATA) as session:
                for query in app.TubeGui.ANALYTICAL_QUERIES:
                    with session.transaction(TransactionType.READ) as read_transaction:
                        read_transaction.query(query)

    @classmethod
    def tearDownClass(cls):
        with TypeDB.core_client("localhost:1729") as client:
            client.databases().get("tube_network").delete()
            print("Deleted the tube_network database")


if __name__ == '__main__':
    unittest.main()
