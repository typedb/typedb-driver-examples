from typedb.client import TypeDB, SessionType, TransactionType

db = '4'
data_path = 'data/'

with TypeDB.core_client("localhost:1729") as client:  # 1
    with client.session(db, SessionType.SCHEMA) as session:  # 2
        with session.transaction(TransactionType.READ) as transaction:  # a
            TypeQL_read_query = 'match $x sub Genre; $b isa Book; $r(tag:$x, book:$b) isa genre_taging; get $x;'
            print("Executing TypeQL read Query: " + TypeQL_read_query)
            iterator = transaction.query().match(TypeQL_read_query)
            for i in iterator:
                print('Here is what we have got: ', i.concepts())  # How to get a list of Entities names
