#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


from typing import List

from typedb.client import TypeDB, TypeDBOptions, SessionType, TransactionType

class Solver:
    SCHEMA_FILE = "sudoku/sudoku6x6_schema.tql"
    DATA_FILE = "sudoku/sudoku6x6_data.tql"
    QUERY_TEMPLATE = """
    match
        $connector-hack = -1 isa connector-hack;
        {0}
        (
            {1}
        ) isa solution;
    limit 1;
        """

    def __init__(self, host: str, db_name: str):
        self.client = TypeDB.core_client(host)
        self.db_name = db_name

    def read_sudoku(self, filename: str):
        with open(filename) as sudoku_file:
            sudoku = [list(map(int, row.split())) for row in sudoku_file if row.strip()]
        assert len(sudoku) == 6 and all(len(row)==6 for row in sudoku)
        return sudoku

    def format_sudoku(self, sudoku: List[List[int]]):
        return "\n".join(" ".join(map(str, row)) for row in sudoku)

    def database_exists(self):
        return self.client.databases().contains(self.db_name)

    def setup(self, force=False):
        if self.client.databases().contains(self.db_name):
            if force:
                self.client.databases().get(self.db_name).delete()
            else:
                return

        print("Setting up in database: '%s'..." % self.db_name)
        self.client.databases().create(self.db_name)

        with open(Solver.SCHEMA_FILE) as f:
            schema = f.read()

        with self.client.session(self.db_name, SessionType.SCHEMA) as session:
            with session.transaction(TransactionType.WRITE) as tx:
                tx.query().define(schema)
                tx.commit()

        with open(Solver.DATA_FILE) as f:
            data = f.read()

        with self.client.session(self.db_name, SessionType.DATA) as session:
            with session.transaction(TransactionType.WRITE) as tx:
                tx.query().insert(data)
                tx.commit()

    def cleanup(self, delete_database=False):
        if delete_database and self.client.databases().contains(self.db_name):
            self.client.databases().get(self.db_name).delete()
        self.client.close()

    def solve(self, sudoku: List[List[int]]):
        # create_query
        non_zero = [(i,j,v) for i,row in enumerate(sudoku, 1) for j,v in enumerate(row, 1) if v != 0]
        value_assignments = ["$v%d%d = %d isa number; $v%d%d != $connector-hack;"%(i,j,v,i,j) for (i,j,v) in non_zero]
        role_players = [ ["pos%d%d: $v%d%d"%(i,j,i,j) for j in range(1,7)] for i in range(1,7) ]

        query = Solver.QUERY_TEMPLATE.format(
            "\n    ".join(value_assignments),
            ",\n        ".join(", ".join(rp) for rp in role_players)
        )

        with self.client.session(self.db_name, SessionType.DATA) as session:
            with session.transaction(TransactionType.READ, TypeDBOptions().set_infer(True)) as tx:
                result = list(tx.query().match(query))

        if result:
            return [ [result[0].get("v%d%d"%(i,j)).get_value() for j in range(1,7)] for i in range(1,7) ]
        else:
            return None
