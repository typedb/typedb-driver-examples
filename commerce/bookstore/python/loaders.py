import random, uuid
from typedb.client import TypeDB, SessionType, TransactionType
import config

debug = False


class Loader:  # Superclass for all loaders
    def __init__(self, item, filename):
        self.item = item  # Object (line) from csv file
        self.file = filename  # Filename of the csv file to import from


class BookInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "books.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a book
        return "insert $b isa book, has id '" + str(uuid.uuid4()) + "', has ISBN '" + self.item["ISBN"] + \
               "', has name '" + self.item["Book-Title"] + "', has book-author '" + self.item["Book-Author"] + \
               "', has publisher '" + self.item["Publisher"] + "', has price " + str(random.randint(3, 100)) + \
               ", has stock " + str(random.randint(0, 25)) + ";"


class UserInput(Loader):
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


class RatingInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "ratings.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a review (review relation)
        typeql_insert_query = "match $u isa user, has foreign-id '" + self.item["User-ID"] + "'; " \
                              "$b isa book, has ISBN '" + self.item["ISBN"] + "'; " \
                              "insert $r (author: $u, product: $b) isa review;" \
                              "$r has rating " + self.item["Book-Rating"] + ";"
        return typeql_insert_query


class OrderInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "orders.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert an order
        i = 0
        typeql_insert_query = "match $u isa user, has foreign-id '" + self.item["User-ID"] + "';"
        for book in random_books():
            i += 1  # counter for the number of books
            if debug: print("Book #" + str(i) + " ISBN: " + book)
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


class GenreInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + "genres.csv")  # Set exact filename to parse with this class

    def load(self):  # building a TypeQL request to insert a genre/book association
        typeql_insert_query = "match $b isa book, has ISBN '" + self.item["ISBN"] + "'; " \
                              "$g isa genre-tag; $g '" + self.item["Genre"] + "'; " \
                              "insert $b has $g;"
        return typeql_insert_query


def create_genre_tags():  # Creating genre tags and tag hierarchy
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            with session.transaction(TransactionType.WRITE) as transaction:
                transaction.query().insert("insert $g 'Fiction' isa genre-tag;")
                transaction.query().insert("insert $g 'Non fiction' isa genre-tag;")
                transaction.query().insert("insert $g 'Other' isa genre-tag;")
                transaction.query().insert("insert $g 'Adults only' isa genre-tag;")
                transaction.query().insert("insert $g 'Kids friendly' isa genre-tag;")
                transaction.query().insert("insert $g 'Sci-Fi' isa genre-tag;")
                transaction.query().insert("match $b = 'Sci-Fi'; $b isa genre-tag;"
                                           "$p = 'Fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Fantasy' isa genre-tag;")
                transaction.query().insert("match $b = 'Fantasy'; $b isa genre-tag;"
                                           "$p = 'Fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Biography' isa genre-tag;")
                transaction.query().insert("match $b = 'Biography'; $b isa genre-tag;"
                                           "$p = 'Non fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Adventure' isa genre-tag;")
                transaction.query().insert("match $b = 'Adventure'; $b isa genre-tag;"
                                           "$p = 'Fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Detective_story' isa genre-tag;")
                transaction.query().insert("match $b = 'Detective_story'; $b isa genre-tag;"
                                           "$p = 'Fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'History' isa genre-tag;")
                transaction.query().insert("match $b = 'History'; $b isa genre-tag;"
                                           "$p = 'Non fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Politics' isa genre-tag;")
                transaction.query().insert("match $b = 'Politics'; $b isa genre-tag;"
                                           "$p = 'Non fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Up to 5 years' isa genre-tag;")
                transaction.query().insert("match $b = 'Up to 5 years'; $b isa genre-tag;"
                                           "$p = 'Kids friendly'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Technical Documentation' isa genre-tag;")
                transaction.query().insert("match $b = 'Technical Documentation'; $b isa genre-tag;"
                                           "$p = 'Non fiction'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("match $b = 'Technical Documentation'; $b isa genre-tag;"
                                           "$p = 'Adults only'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Map' isa genre-tag;")
                transaction.query().insert("match $b = 'Map'; $b isa genre-tag;"
                                           "$p = 'Technical Documentation'; $p isa genre-tag;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.commit()
    print("Created genre tags.")
    return


def random_books():
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                typeql_read_query = "match $b isa book, has ISBN $x; get $x; limit 800;"  # get 800 books
                if debug: print("Executing TypeQL read Query: " + typeql_read_query)
                iterator = transaction.query().match(typeql_read_query)  # Execute read query
                answers = [ans.get("x") for ans in iterator]
                books = [answer.get_value() for answer in answers]  # This contains the result (800 ISBN records)
                # for order_id in range(1,6):  # Go through all 5 orders
                ordered_books = []  # Resetting variable to store ordered items for an order
                for item_n in range(1, random.randint(2, 10)):  # Iterate through random (2-9) number of books
                    ordered_books.append(books[random.randint(0, 799)])  # Select random book from 800
    return ordered_books


# This is a list of classes to import data
# Classes have filenames and corresponding methods to load the parsed data into the DB
Input_types_list = [BookInput, UserInput, RatingInput, OrderInput, GenreInput]
