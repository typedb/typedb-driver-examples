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
import timeit
import os.path
import unittest

from typedb.client import TypeDB

from typedb_sudoku import Solver
from utils import read_sudoku

class Test(unittest.TestCase):
    DATABASE_NAME = "test_sudoku6x6"
    SAMPLE_PATH = os.path.join(os.path.dirname(__file__), "sample")
    SAMPLES = [("sudoku1.txt", "solution1.txt"),
               ("sudoku2.txt", "solution2.txt"),
               ("sudoku3.txt", "solution3.txt"),
               ("sudoku4.txt", "solution4.txt")
               ]

    def setUp(self):
        self.solver = Solver(TypeDB.DEFAULT_ADDRESS, Test.DATABASE_NAME)
        self.solver.setup(True)
        print("Loaded the " + Test.DATABASE_NAME + " schema")

    def test_samples(self):
        for (sample_file, solution_file) in Test.SAMPLES:
            sudoku = read_sudoku(os.path.join(Test.SAMPLE_PATH, sample_file))
            time_start = timeit.default_timer()
            solver_solution = self.solver.solve(sudoku)
            print("Solved %s in %d ms"% (sample_file, int(1 + 1000 * (timeit.default_timer() - time_start))))
            expected_solution = read_sudoku(os.path.join(Test.SAMPLE_PATH, solution_file))
            self.assertEqual(expected_solution, solver_solution)

    def tearDown(self):
        self.solver.cleanup(True)


if __name__ == '__main__':
    unittest.main()
