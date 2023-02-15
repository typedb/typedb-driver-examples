import math
import timeit
from sys import argv
from typing import List

from typedb.client import TypeDB, TypeDBClient, TypeDBOptions, SessionType, TransactionType


HOST = TypeDB.DEFAULT_ADDRESS
DATABASE_NAME = "sudoku6x6"
QUERY_TEMPLATE = """
match
    $connector-hack = -1 isa connector-hack;
    {0}
    (
        {1}
    ) isa solution;
limit 1;
    """

def format_sudoku(sudoku: List[List[int]]):
    return "\n".join(" ".join(map(str, row)) for row in sudoku)

def database_exists(client: TypeDBClient, db_name: str):
    return client.databases().contains(db_name)

def setup(client: TypeDBClient, db_name: str):
    if database_exists(client, db_name):
        client.databases().get(db_name).delete()
    client.databases().create(db_name)

    with open("sudoku6x6_schema.tql") as f:
        schema = f.read()

    with client.session(db_name, SessionType.SCHEMA) as session:
        with session.transaction(TransactionType.WRITE) as tx:
            tx.query().define(schema)
            tx.commit()

    with open("sudoku6x6_data.tql") as f:
        data = f.read()

    with client.session(db_name, SessionType.DATA) as session:
        with session.transaction(TransactionType.WRITE) as tx:
            tx.query().insert(data)
            tx.commit()

def solve(client: TypeDBClient, db_name: str, sudoku: List[List[int]]):
    # create_query
    non_zero = [(i,j,v) for i,row in enumerate(sudoku, 1) for j,v in enumerate(row, 1) if v != 0]
    value_assignments = ["$v%d%d = %d isa number; $v%d%d != $connector-hack;"%(i,j,v,i,j) for (i,j,v) in non_zero]
    role_players = [ ["pos%d%d: $v%d%d"%(i,j,i,j) for j in range(1,7)] for i in range(1,7) ]

    query = QUERY_TEMPLATE.format(
        "\n    ".join(value_assignments),
        ",\n        ".join(", ".join(rp) for rp in role_players)
    )

    with client.session(db_name, SessionType.DATA) as session:
        with session.transaction(TransactionType.READ, TypeDBOptions().set_infer(True)) as tx:
            result = list(tx.query().match(query))

    if result:
        return [ [result[0].get("v%d%d"%(i,j)).get_value() for j in range(1,7)] for i in range(1,7) ]
    else:
        return None



def main():
    if len(argv) != 2:
        print("Usage:")
        print("python3 %s setup:            Loads required schema & data" % argv[0])
        print("python3 %s <sudoku_file>:    Reads & solves the sudoku in <sudoku_file>" % argv[0])

    client = TypeDB.core_client(HOST)
    if argv[1] == "setup":
        print("Setting up in database: '%s'..." % DATABASE_NAME)
        setup(client, DATABASE_NAME)
        return
    if not database_exists(client, DATABASE_NAME):
        print("Database '%s' does not exist. Setting up..." % DATABASE_NAME)
        setup(client, DATABASE_NAME)

    with open(argv[1]) as sudoku_file:
        sudoku = [list(map(int, row.split())) for row in sudoku_file if row.strip()]

    assert len(sudoku) == 6 and all(len(row)==6 for row in sudoku)

    print("Solving:")
    print(format_sudoku(sudoku), "\n")

    time_start = timeit.default_timer()
    solution = solve(client, DATABASE_NAME, sudoku)
    time_taken_ms = math.ceil((timeit.default_timer() - time_start) * 1000)
    if solution:
        print("Found solution in " + str(time_taken_ms) + " ms:")
        print(format_sudoku(solution))
    else:
        print("No solution (took " + str(time_taken_ms) + " ms)")


if __name__=="__main__": main()
