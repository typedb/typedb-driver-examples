from typedb.client import TypeDB, SessionType, TransactionType, TypeDBOptions

# change later to a config update from other file
db = 'bookstore'  # DB name


def selection():

    print("Please choose one of the following functions: ")
    print("1. Search for a Book")
    print("2. Search for a User")
    print("3. Search for an Order")
    print("4. Search for books by genre")
    selection = input("Your request: ")
    if selection == '':
        print('Empty selection recognized. Please try again.')
        return '1'
    elif selection == '1':  # Book
        search_book(input('Searching for a Book. Please type in an ISBN or press enter for a full listing: '))
        return '0'
    elif selection == '2':  # User
        search_user(input('Searching for a User. Please type in a foreign ID or press enter for a full listing: '))
        return '0'
    elif selection == '3':  # Order
        search_order(input('Searching for an Order. Please type in an Order ID or press enter for a full listing: '))
        return '0'
    elif selection == '4':  # genre
        show_genres()
        search_genre(input('Searching for books by genre. Please type in genre name: '))
        return '0'
    elif x == '0' or 'exit' or 'exit()' or 'close' or 'close()' or 'help':  # Exit
        return '2'
    else:
        print('Invalid selection recognized. Please try again.')
        return '1'


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
        print('Books found:', k)
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
                    s = 0
                    for item in iterator:
                        g += 1
                        rating = item.get('rating').get_value()
                        # print(g, 'Review rating found:', rating)
                        s = s + rating
                    print('Total rating records:', str(g) + '. Average book rating:', round(s/g, 2))
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
                    print('Users found:', k)
                    return


def search_order(order_id):
    # Different approach - download all orders first, filter later
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                TypeQL_read_query = 'match $o isa Order, has id $i, has foreign-user-id $fui, ' \
                                    'has date $d, has status $s, has delivery_address $da;' \
                                    'get $i, $fui, $d, $s, $da;'
                print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)
                result = ''
                for answer in iterator:
                    if order_id == '' or (order_id == answer.get('i').get_value()):
                        result += '\nOrder ID:' + str(answer.get('i').get_value())
                        result += ' Foreign User-ID:' + str(answer.get('fui').get_value())
                        result += ' Date:' + str(answer.get('d').get_value())
                        result += ' Status:' + str(answer.get('s').get_value())
                        result += ' Delivery address:' + str(answer.get('da').get_value())
                        print(result)
                        result = ''
    return


def search_genre(tag_name):
    if tag_name == '':
        print('Empty input. Lets look for a Map genre, so you can find what you are looking for.')
        tag_name = 'Map'
    TB = TypeDBOptions.core()
    TB.infer = True
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ, TB) as transaction:
                TypeQL_read_query = 'match $g isa genre; $g "' + tag_name + '";' \
                                    '$b isa Book, has name $n, has ISBN $i; ' \
                                    '(tag:$g, book: $b) isa taging; ' \
                                    'get $i, $n;'
                print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)
                result = ''
                print('Looking for a', tag_name, 'genre. Here is what we have:')
                k = 1
                for answer in iterator:
                    result = '\n' + str(k)
                    result += ' ISBN:' + str(answer.get('i').get_value())
                    result += ' Book title:' + str(answer.get('n').get_value())
                    print(result)
                    k += 1
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


def show_genres():
    with TypeDB.core_client("localhost:1729") as client:  # 1
        with client.session(db, SessionType.DATA) as session:  # 2
            with session.transaction(TransactionType.READ) as transaction:  # a
                TypeQL_read_query = 'match $g isa genre; get $g;'
                print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)
                k = 0
                for item in iterator:
                    k += 1
                    print(k, item.get("g").get_value())
                print('Total count:', k)
    return

x = '1'
print("Bookstore CRM v.0.0.0.0.1a")
while True:
    x = selection()
    if x == '0':
        break
    elif x == '2':
        print('Terminating program.')
        break
