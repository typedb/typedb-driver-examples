from typedb.client import TypeDB, SessionType, TransactionType, TypeDBOptions

db = 'bookstore'  # DB name
debug = False  # Set True to enable additional output for debugging


def selection():  # This is the main UI to select a function to proceed with

    print("Please choose one of the following functions: ")
    print("1. Search for a Book")
    print("2. Search for a User")
    print("3. Search for an Order")
    print("4. Search for books by genre")
    selection = input("Your request: ")  # Storing answer here
    if selection == '':
        print('Empty selection recognized. Please try again.')
        return '1'
    elif selection == '1':  # We chose variant #1 — searching for a Book
        search_book(input('Searching for a Book. Please type in an ISBN or press enter for a full listing: '))
        return '0'
    elif selection == '2':  # 2. Searching for a User
        search_user(input('Searching for a User. Please type in a foreign ID or press enter for a full listing: '))
        return '0'
    elif selection == '3':  # 3. Searching for an Order
        search_order(input('Searching for an Order. Please type in an Order ID or press enter for a full listing: '))
        return '0'
    elif selection == '4':  # 4. Searching for Books by genre
        show_all_genres()  # Display all genres as a tip
        search_genre(input('Searching for books by genre. Please type in genre name: '))
        return '0'
    elif x == '0' or 'exit' or 'exit()' or 'close' or 'close()' or 'help':  # Exit the program
        return '2'
    else:
        print('Invalid selection recognized. Please try again.')  # Something else / unrecognized - repeat
        return '1'


def search_book(ISBN):  # Search Book by ISBN (or show all Books if empty ISBN given)

    if ISBN == '':  # empty ISBN given
        print('Empty input. Listing all books')
        show_all_books()  # Display all Books
        return
    else:  # Non-empty ISBN given
        with TypeDB.core_client("localhost:1729") as client:
            with client.session(db, SessionType.DATA) as session:
                with session.transaction(TransactionType.READ) as transaction:
                    TypeQL_read_query = 'match $b isa Book, has ISBN "' + ISBN + '", has name $n, has Book_Author $ba; ' \
                                        'get $n, $ba;'
                    if debug: print("Executing TypeQL read Query: " + TypeQL_read_query)
                    iterator = transaction.query().match(TypeQL_read_query)  # Execute query
                    k = 0  # counter
                    for item in iterator:  # Iterating through results of the match query
                        print(ISBN, item.get('n').get_value(), item.get('ba').get_value(), sep=' — ')  # Print results
                        k += 1
        print('Books found:', k)  # Print counter as a number of results
        # Rating computation
        with TypeDB.core_client("localhost:1729") as client:  # 1
            with client.session(db, SessionType.DATA) as session:  # 2
                with session.transaction(TransactionType.READ) as transaction:  # a
                    TypeQL_read_query = 'match $b isa Book, has ISBN "' + ISBN + '";' \
                                        '$r (product: $b, author:$a) isa reviewing; $r has rating $rating;' \
                                        'get $rating;'
                    print("Executing TypeQL read Query: " + TypeQL_read_query)
                    iterator = transaction.query().match(TypeQL_read_query)  # Execute query
                    g = 0
                    s = 0
                    for item in iterator:  # iterating through query results
                        g += 1  # counter
                        rating = item.get('rating').get_value()
                        if debug: print(g, 'Review rating found:', rating)
                        s = s + rating  # sum
                    print('Total rating records:', str(g) + '. Average book rating:', round(s/g, 2))
                    # printed s (sum) divided by g (number of results), rounded to 2 signs after a comma
    return


def search_user(user):  # Search User by foreign-id (or show all Users if empty id given)

    if user == '':
        print('Empty input. Listing all users')
        show_all_users()  # Display all Users
        return
    else:
        with TypeDB.core_client("localhost:1729") as client:  # 1
            with client.session(db, SessionType.DATA) as session:  # 2
                with session.transaction(TransactionType.READ) as transaction:  # a
                    TypeQL_read_query = 'match $u isa User, has id $i, has name $n, has foreign-id "' + user + '"; ' \
                                        'get $i, $n;'  # We can limit the number of results by adding ' limit 100;'
                    print("Executing TypeQL read Query: " + TypeQL_read_query)
                    iterator = transaction.query().match(TypeQL_read_query)  # Executing query
                    k = 0
                    for item in iterator:  # Iterating through results
                        print(user, item.get('n').get_value(), item.get('i').get_value(), sep=' — ')  # Print results
                        k += 1  # Counter
                    print('Users found:', k)  # Print number of results
                    return


def search_order(order_id):  # Search Order by id (or show all Orders if empty id given)
    # Different approach - download all orders first, filter later
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                TypeQL_read_query = 'match $o isa Order, has id $i, has foreign-user-id $fui, ' \
                                    'has date $d, has status $s, has delivery_address $da;' \
                                    'get $i, $fui, $d, $s, $da;'
                if debug: print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)  # Execute query
                result = ''
                for answer in iterator:  # Iterate through results (orders)
                    if order_id == '' or (order_id == answer.get('i').get_value()):  # show all or one with this order_id
                        result += '\nOrder ID:' + str(answer.get('i').get_value())
                        result += ' Foreign User-ID:' + str(answer.get('fui').get_value())
                        result += ' Date:' + str(answer.get('d').get_value())
                        result += ' Status:' + str(answer.get('s').get_value())
                        result += ' Delivery address:' + str(answer.get('da').get_value())
                        if result != '':  # If this iteration has something to print
                            print(result)  # Print result
                        result = ''  # Reset the variable for next iteration
    return


def search_genre(tag_name):  # Search Books by genre tag
    if tag_name == '':  # Empty input. But we already showed all tags/genres before
        print('Empty input. Lets look for a Map genre, so you can find what you are looking for.')
        tag_name = 'Map'  # Choosing genre instead of an empty input
    TB = TypeDBOptions.core()  # Initialising a new set of options
    TB.infer = True  # Enabling inference in this new set of options
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ, TB) as transaction:
                TypeQL_read_query = 'match $g isa genre; $g "' + tag_name + '";' \
                                    '$b isa Book, has name $n, has ISBN $i; ' \
                                    '(tag:$g, book: $b) isa taging; ' \
                                    'get $i, $n;'
                if debug: print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)  # Execute query
                print('Looking for a', tag_name, 'genre. Here is what we have:')
                k = 1  # Counter
                for answer in iterator:  # Iterating through results
                    result = '\n' + str(k)  # Prepare the positional number of result
                    result += ' ISBN:' + str(answer.get('i').get_value())  # Prepare ISBN
                    result += ' Book title:' + str(answer.get('n').get_value())  # Prepare Book name
                    print(result)  # Print prepared result
                    k += 1  # Increase the counter
    return


def show_all_books():  # Just show all Books
    print('Showing all books')
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                TypeQL_read_query = 'match $b isa Book, has ISBN $i, has name $n, has Book_Author $ba; ' \
                                    'get $i, $n, $ba;'  # We can limit the number of results by adding ' limit 100;'
                if debug: print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)  # Executing match query
                k = 0  # Counter
                for item in iterator:  # Iterating through results
                    k += 1
                    print(k, item.get("i").get_value(), item.get('n').get_value(), item.get('ba').get_value(), sep=' — ')
                    #  Printed result
                print('Total count:', k)  # Printing the counter value after all iterations
    return


def show_all_users():  # Just show all Users
    print('Showing all users')
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                TypeQL_read_query = 'match $u isa User, has id $i, has name $n, has foreign-id $fi; ' \
                                    'get $i, $n, $fi;'  # We can limit the number of results by adding ' limit 100;'
                if debug: print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)  # Executing query
                k = 0  # Counter
                for item in iterator:  # Iterating through results
                    k += 1
                    print(k, '- Foreign ID: ' + item.get("fi").get_value(), 'Name: ' + item.get('n').get_value(), 'ID: '
                          + item.get('i').get_value())
                    # Printed result
                print('Total count:', k)  # Printing counter value after all iterations


def show_all_genres():  # Just display all genre tags
    with TypeDB.core_client("localhost:1729") as client:
        with client.session(db, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ) as transaction:
                TypeQL_read_query = 'match $g isa genre; get $g;'  # Prepare query
                if debug: print("Executing TypeQL read Query: " + TypeQL_read_query)
                iterator = transaction.query().match(TypeQL_read_query)  # Execute transaction
                k = 0  # Counter
                for item in iterator:  # Iterating through all results
                    k += 1
                    print(k, item.get("g").get_value())  # Printing positional number and genre
                print('Total count:', k)  # Printing counter value after all iterations
    return

# This is the main body of this script
x = '1'
print("Bookstore CRM v.0.0.0.0.1a")
while True:  # This cycle will repeat until one of the following breaks happen
    x = selection()  # call selection UI once per cycle
    if x == '0':  # If we successfully selected one of the functions — we no longer need to repeat the selection
        break  # This stops printing selection of the function UI after successful pick
    elif x == '2':  # If we chose to exit
        print('Terminating program.')
        break  # This stops printing selection of the function UI after successful pick
