from typedb.client import TypeDB, SessionType, TransactionType

# change later to a config update from other file
db = '6'  # DB name


def selection():

    print("Please choose one of the following functions: ")
    print("1. Search for a Book")
    print("2. Search for a User")
    selection = input("Your request: ")
    if selection == '':
        print('Empty selection recognized. Please try again.')
        return '3'
    elif selection == '1':  # Book
        search_book(input('Searching for a Book. Please type in an ISBN or press enter for a full listing: '))
        return '1'
    elif selection == '2':  # User
        search_user(input('Searching for a User. Please type in a foreign ID or press enter for a full listing: '))
        return '2'
    elif x == '0' or 'exit' or 'exit()' or 'close' or 'close()' or 'help':  # Exit
        return '0'
    else:
        print('Invalid selection recognized. Please try again.')
        return '3'


def search_book(ISBN):

    if ISBN == '':
        print('Empty input. Listing all books')
        show_all_books()
        return
    else:
        with TypeDB.core_client("localhost:1729") as client:
            with client.session(db, SessionType.DATA) as session:
                with session.transaction(TransactionType.READ) as transaction:
                    TypeQL_read_query = 'match $b isa Book, has ISBN "' + ISBN + '", has name $n, has Book_Author $ba; ' \
                                        'get $n, $ba;'
                    # What id we don't have a Book_Author set? We will not have this instance?
                    # Shall we search ISBNs first and then try to obtain all parameters?
                    print("Executing TypeQL read Query: " + TypeQL_read_query)
                    iterator = transaction.query().match(TypeQL_read_query)
                    k = 0
                    for item in iterator:
                        print(ISBN, item.get('n').get_value(), item.get('ba').get_value(), sep=' — ')
                        k += 1

        # Rating computation
        with TypeDB.core_client("localhost:1729") as client:  # 1
            with client.session(db, SessionType.DATA) as session:  # 2
                with session.transaction(TransactionType.READ) as transaction:  # a
                    TypeQL_read_query = 'match $b isa Book, has ISBN "' + ISBN + '";' \
                                        '$r (product: $b, author:$a) isa reviewing; $r has rating $rating;' \
                                        'get $rating;'
                    print("Executing TypeQL read Query: " + TypeQL_read_query)
                    iterator = transaction.query().match(TypeQL_read_query)
                    g = 0
                    for item in iterator:
                        print(ISBN, item.get('rating').get_value())
                        g += 1
                        # insert Rating computation here
                    print('Total rating records:', g)
    print('Results found:', k)
    return


def search_user(user):

    if user == '':
        print('Empty input. Listing all users')
        show_all_users()
        return
    else:
        with TypeDB.core_client("localhost:1729") as client:  # 1
            with client.session(db, SessionType.DATA) as session:  # 2
                with session.transaction(TransactionType.READ) as transaction:  # a
                    TypeQL_read_query = 'match $u isa User, has id $i, has name $n, has foreign-id "' + user + '"; ' \
                                        'get $i, $n;'  # We can limit the number of results by adding ' limit 100;'
                    print("Executing TypeQL read Query: " + TypeQL_read_query)
                    iterator = transaction.query().match(TypeQL_read_query)
                    k = 0
                    for item in iterator:
                        print(user, item.get('n').get_value(), item.get('i').get_value(), sep=' — ')
                        k += 1
                        # insert Rating computation here
                    print('Results found:', k)
                    return


def show_all_books():
    print('Showing all books')
    with TypeDB.core_client("localhost:1729") as client:  # 1
        with client.session(db, SessionType.DATA) as session:  # 2
            with session.transaction(TransactionType.READ) as transaction:  # a
                TypeQL_read_query = 'match $b isa Book, has ISBN $i, has name $n, has Book_Author $ba; ' \
                                    'get $i, $n, $ba;'  # We can limit the number of results by adding ' limit 100;'
                print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)
                k = 0
                for item in iterator:
                    k += 1
                    print(k, item.get("i").get_value(), item.get('n').get_value(), item.get('ba').get_value(), sep=' — ')
                print('Total count:', k)


def show_all_users():
    print('Showing all users')
    with TypeDB.core_client("localhost:1729") as client:  # 1
        with client.session(db, SessionType.DATA) as session:  # 2
            with session.transaction(TransactionType.READ) as transaction:  # a
                TypeQL_read_query = 'match $u isa User, has id $i, has name $n, has foreign-id $fi; ' \
                                    'get $i, $n, $fi;'  # We can limit the number of results by adding ' limit 100;'
                print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)
                k = 0
                for item in iterator:
                    k += 1
                    print(k, '- Foreign ID: ' + item.get("fi").get_value(), 'Name: ' + item.get('n').get_value(), 'ID: '
                          + item.get('i').get_value())
                print('Total count:', k)


x = '0'
print("Bookstore CRM v.0.0.0.0.1a")
while True:
    x = selection()
    if x == '1':
        break
    elif x == '2':
        break
    elif x == '0':
        print('Terminating program.')
        break
