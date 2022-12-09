import random, uuid
from typedb.client import TypeDB, SessionType, TransactionType
import config

debug = False


class Loader:
    def __init__(self, item, filename):
        self.item = item
        self.file = filename


class BookInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + 'books.csv')

    def load(self):  # building a TypeQL request to insert a book
        return "insert $b isa book, has id '" + str(uuid.uuid4()) + "', has ISBN '" + self.item["ISBN"] + \
               "', has name '" + self.item["Book-Title"] + "', has book-author '" + self.item["Book-Author"] + \
               "', has publisher '" + self.item["Publisher"] + "', has price " + str(random.randint(3, 100)) + \
               ", has stock " + str(random.randint(0, 25)) + ";"


class UserInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + 'users.csv')

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
        super().__init__(item, config.data_path + 'ratings.csv')

    def load(self):  # building a TypeQL request to insert a review (reviewing relation)
        typeql_insert_query = "match $u isa user, has foreign-id '" + self.item["User-ID"] + "'; " \
                              "$b isa book, has ISBN '" + self.item["ISBN"] + "'; " \
                              "insert $r (author: $u, product: $b) isa reviewing;" \
                              "$r has rating " + self.item["Book-Rating"] + ";"
        return typeql_insert_query


class OrderInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + 'orders.csv')

    def load(self):  # building a TypeQL request to insert an order
        typeql_insert_query = "insert $o isa order, has id '" + self.item["id"] + "'," \
                              "has foreign-user-id '" + self.item["User-ID"] + "', " \
                              "has date " + self.item["date"] + ", " \
                              "has status '" + self.item["status"] + "'," \
                              "has delivery-address '" + self.item["delivery_address"] + "', " \
                              "has payment-details '" + self.item["payment_details"] + "';"
        return typeql_insert_query


class GenreInput(Loader):
    def __init__(self, item):
        super().__init__(item, config.data_path + 'genres.csv')

    def load(self):  # building a TypeQL request to insert a genre/book association
        typeql_insert_query = "match $b isa book, has ISBN '" + self.item["ISBN"] + "'; " \
                              "$g isa genre; $g '" + self.item["Genre"] + "'; " \
                              "insert $tag (tag: $g, book: $b) isa taging;"
        return typeql_insert_query


def create_genre_tags():  # Creating genre tags and tag hierarchy

    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            with session.transaction(TransactionType.WRITE) as transaction:
                transaction.query().insert("insert $g 'Fiction' isa genre;")
                transaction.query().insert("insert $g 'Non fiction' isa genre;")
                transaction.query().insert("insert $g 'Other' isa genre;")
                transaction.query().insert("insert $g 'Adults only' isa genre;")
                transaction.query().insert("insert $g 'Kids friendly' isa genre;")
                transaction.query().insert("insert $g 'Sci-Fi' isa genre;")
                transaction.query().insert("match $b = 'Sci-Fi'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Fantasy' isa genre;")
                transaction.query().insert("match $b = 'Fantasy'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Biography' isa genre;")
                transaction.query().insert("match $b = 'Biography'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Adventure' isa genre;")
                transaction.query().insert("match $b = 'Adventure'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Detective_story' isa genre;")
                transaction.query().insert("match $b = 'Detective_story'; $b isa genre;"
                                           "$p = 'Fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'History' isa genre;")
                transaction.query().insert("match $b = 'History'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Politics' isa genre;")
                transaction.query().insert("match $b = 'Politics'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Up to 5 years' isa genre;")
                transaction.query().insert("match $b = 'Up to 5 years'; $b isa genre;"
                                           "$p = 'Kids friendly'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Technical Documentation' isa genre;")
                transaction.query().insert("match $b = 'Technical Documentation'; $b isa genre;"
                                           "$p = 'Non fiction'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("match $b = 'Technical Documentation'; $b isa genre;"
                                           "$p = 'Adults only'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.query().insert("insert $g 'Map' isa genre;")
                transaction.query().insert("match $b = 'Map'; $b isa genre;"
                                           "$p = 'Technical Documentation'; $p isa genre;"
                                           "insert $th (sub-tag: $b, sup-tag: $p) isa tag-hierarchy;")
                transaction.commit()
    print("Created genre tags.")
    return


def generate_ordered_items():  # Generating random item-lists for orders from books
    result = []
    # generate 5 random sets of 2-9 books
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                typeql_read_query = "match $b isa book, has ISBN $x; get $x; limit 800;"  # get 800 books
                if debug: print("Executing TypeQL read Query: " + typeql_read_query)
                iterator = transaction.query().match(typeql_read_query)  # Execute read query
                answers = [ans.get("x") for ans in iterator]
                books = [answer.get_value() for answer in answers]  # This contains the result (800 ISBN records)
                for order_id in range(1, 6):  # Go through all 5 orders
                    ordered_books = []
                    for item_n in range(1, random.randint(2, 10)):  #
                        ordered_books.append(books[random.randint(0, 799)])  # Exactly 800 books to select from
                    result.append(ordered_books)

    n = 1
    # Add ordering relations (assign items from the sets above)
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(config.db, SessionType.DATA) as session:
            for order in result:
                if debug: print("\nOrder #", n, "contains:")
                for book in order:
                    if debug: print("\nISBN", book)
                    with session.transaction(TransactionType.WRITE) as transaction:
                        typeql_insert_query = "match $b isa book, has ISBN '" + book + "';" \
                                              "$o isa order, has id '" + str(n) + "', has foreign-user-id $fui;" \
                                              "$u isa user, has foreign-id $fi;" \
                                              "$fui = $fi;" \
                                              "insert (order: $o, item: $b, author: $u ) isa ordering;"
                        # the $fui and $fi variables are compared by value only
                        if debug: print("Executing TypeQL Query: " + typeql_insert_query)
                        transaction.query().insert(typeql_insert_query)
                        transaction.commit()
                n += 1
    return  # END of generate_ordered_items()


# This is a list of classes to import data with filenames and corresponding methods to load the parsed data into the DB
Input_types_list = [BookInput, UserInput, RatingInput, OrderInput, GenreInput]
