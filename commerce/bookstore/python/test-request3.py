from typedb.client import TypeDB, SessionType, TransactionType

db = '4'
data_path = 'data/'

with TypeDB.core_client("localhost:1729") as client:  # 1
    with client.session(db, SessionType.DATA) as session:  # 2
        with session.transaction(TransactionType.READ) as transaction:  # a
            TypeQL_read_query = 'match $b isa Book, has name $n; get $n; limit 10;'
            print("Executing TypeQL read Query: " + TypeQL_read_query)
            iterator = transaction.query().match(TypeQL_read_query)
            answers = [ans.get("n") for ans in iterator]
            result = [answer.get_value() for answer in answers]
            print("Result:", result)
