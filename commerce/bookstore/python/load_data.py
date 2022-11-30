import csv, uuid, random
from typedb.client import TypeDB, SessionType, TransactionType

data_path = "data/"  # path to csv files to import/load data
db = '6'  # Name of the DB to connect on the TypeDB

def parse_data_to_dictionaries(input):
    """
      1. reads the file through a stream,
      2. adds the dictionary to the list of items
      :param input.file as string: the path to the data file, minus the format
      :returns items as list of dictionaries: each item representing a data item from the file at input.file
    """
    print('Parsing started')
    items = []

    with open(input["file"] + ".csv", encoding='UTF-8') as data:  # 1
        for row in csv.DictReader(data, delimiter=";", skipinitialspace=True):
            # row = [d.replace('"', '').replace("\'", '') for d in row]  # In case the data needs filtering
            item = {key: value for key, value in row.items()}  # fieldnames (keys) are taken from the first row
            items.append(item)  # 2
    print('parsing ended')
    return items


def load_data_into_typedb(input, session):
    with session.transaction(TransactionType.READ) as transaction:  # a
        TypeQL_read_query = ''
        print("Executing TypeQL read Query: " + TypeQL_read_query)
        x = concept_map.concepts(transaction.query().match(TypeQL_read_query))

        transaction.commit()
    return

def load_data_into_typedb(input, session):
    """
      loads the csv data into our TypeDB phone_calls database:
      1. gets the data items as a list of dictionaries
      2. for each item dictionary
        a. creates a TypeDB transaction
        b. constructs the corresponding TypeQL insert query
        c. runs the query
        d. commits the transaction
      :param input as dictionary: contains details required to parse the data
      :param session: off of which a transaction will be created
    """
    items = parse_data_to_dictionaries(input)  # 1
    x = 1
    for item in items:  # 2
        print(x, item)
        x += 1
        with session.transaction(TransactionType.WRITE) as transaction:  # a
            TypeQL_insert_query = input["template"](item)  # b
            print("Executing TypeQL Query: " + TypeQL_insert_query)
            transaction.query().insert(TypeQL_insert_query)  # c returns a list of answers
            transaction.commit()  # d

    print("\nInserted " + str(len(items)) +
          " items from [ " + input["file"] + ".csv] into TypeDB.\n")


def books_template(book):
    return 'insert $b isa Book, has id "' + str(uuid.uuid4()) + '", has ISBN "' + book["ISBN"] + '", has name "' + book["Book-Title"] + '", has Book_Author "' \
           + book["Book-Author"] + '", has Publisher "' + book["Publisher"] + '", has price ' + str(random.randint(3, 100)) \
           + ', has stock ' + str(random.randint(0, 25)) + ';'


def users_template(user):
    first_names = ('John', 'Andy', 'Joe', 'Bob', 'Alex', 'Mary', 'Alexa', 'Monika', 'Vladimir', 'Tom', 'Jerry')
    random.choice(first_names)
    TypeQL_insert_query = 'insert $u isa User, has id "' + str(uuid.uuid4()) + '", has foreign-id "' + user["User-ID"] + '"'
    if user["Age"] != "NULL":
        TypeQL_insert_query += ',  has age ' + user["Age"]
#    else:  # In case we want to generate random data
#        TypeQL_insert_query += ',  has age ' + str(random.randint(18, 105))
    TypeQL_insert_query += ', has name "' + random.choice(first_names) + '";'

    return TypeQL_insert_query


def ratings_template(review):
    TypeQL_insert_query = 'match $u isa User, has foreign-id "' + review["User-ID"] + '"; ' \
                          '$b isa Book, has ISBN "' + review["ISBN"] + '"; ' \
                          'insert $r (author: $u, product: $b) isa reviewing;' \
                          '$r has rating ' + review["Book-Rating"] + ';'

    return TypeQL_insert_query


def genre_template(genre):
    TypeQL_insert_query = 'match $b isa Book, has ISBN "' + genre["ISBN"] + '"; ' \
                          '$g isa Genre, has ISBN "' + genre["ISBN"] + '"; ' \
                          'insert $review (author: $user, product: $book) isa reviewing;' \
                          '$review has rating ' + genre["Book-Rating"] + ';'

    return TypeQL_insert_query


Inputs = [
    {
        "file": "books",
        "template": books_template
    },
    {
        "file": "users",
        "template": users_template
    },
    {
        "file": "ratings",
        "template": ratings_template
    }
    #,
    #{
    #    "file": "genre",
    #    "template": genre_template
    #}
]


with TypeDB.core_client("localhost:1729") as client:  # 1
    with client.session(db, SessionType.DATA) as session:  # 2
        for input in Inputs:
            input["file"] = data_path + input["file"]  # 3a
            print("Loading from [" + input["file"] + ".csv] into TypeDB ...")
            load_data_into_typedb(input, session)  # 3b

