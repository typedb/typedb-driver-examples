# This file to be deleted as redundant
import random
from typedb.client import TypeDB, SessionType, TransactionType

db = '4'
data_path = 'data/'
n = 1
result = []

# generate 5 random sets of 1-9 books
with TypeDB.core_client("localhost:1729") as client:
    with client.session(db, SessionType.DATA) as session:
        with session.transaction(TransactionType.READ) as transaction:
            TypeQL_read_query = 'match $b isa Book, has ISBN $x; get $x; limit 800;'
            print("Executing TypeQL read Query: " + TypeQL_read_query)
            iterator = transaction.query().match(TypeQL_read_query)
            answers = [ans.get("x") for ans in iterator]
            books = [answer.get_value() for answer in answers]
            for order_id in range(1,6):
                ordered_books = []
                for item_n in range(1, random.randint(2, 10)):
                    ordered_books.append(books[random.randint(1, 800)])
                result.append(ordered_books)

n = 1
k = 1
# Add ordering relations (assign items from the sets above)
with TypeDB.core_client("localhost:1729") as client:
    with client.session(db, SessionType.DATA) as session:
        for order in result:
            print('Order #', n, 'contains:')
            for book in order:
                print('\nISBN', book)
                with session.transaction(TransactionType.WRITE) as transaction:
                    TypeQL_insert_query = 'match $b isa Book, has ISBN "' + book + '";' \
                                          '$o isa Order, has id "' + str(n) + '", has foreign-user-id $fui;' \
                                          '$u isa User, has foreign-id $fi;' \
                                          '$fui = $fi;' \
                                          'insert (order: $o, item: $b, author: $u ) isa ordering;'
                                          # the $fui and $fi variables are compared by value only
                    print("Executing TypeQL Query: " + TypeQL_insert_query)
                    print(transaction.query().insert(TypeQL_insert_query))
                    transaction.commit()
            n += 1
