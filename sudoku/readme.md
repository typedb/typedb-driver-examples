# Sudoku
In this example, we consider a smaller 6x6 version of the sudoku.
The aim is to fill in the grid with numbers from 1 to 6 such that no row, column or box contains the same number twice.
Boxes are 2 rows tall x 3 columns wide.
Sudoku is a popular puzzle involving a 9x9 grid.
Some of the numbers will be filled in as part of the puzzle.


## Usage
### Setup
If you're using bazel, it will take care of the dependencies for you.
If not, you'll have to install the dependencies using:

`python -m pip requirements.txt`

### Running the solver
Through bazel:

    `bazel run //sudoku:driver -- (<sudoku-path|setup>)`
Directly:

    `python driver.py (<sudoku-path>|setup)`

Running with 
* `setup` will (re)create the database and load the required schema & data.
* `<sudoku-path>` will solve the sudoku in the specified file. It will also run setup if required.
    - When running through bazel, use the path to the samples is: `sudoku/sample/sudoku*.txt`.

### Running tests
`bazel test //sudoku:test`  or `python test.py`
    
## Defining the sudoku file
The file specified through the `sudoku-path` argument must contain a space
delimited list of cells. Empty cells should be filled with `0`.

Example, for the sudoku:

|   |   | 3 | 6 |   |   |
|---|---|---|---|---|---|
|   | 2 |   |   |   | 4 |
| 5 |   |   |   | 6 |   |
|   | 3 |   |   |   | 5 |
| 3 |   |   |   | 1 |   |
|   |   | 1 | 4 |   |   |

The file would contain:
```
0 0 3 6 0 0
0 2 0 0 0 4
5 0 0 0 6 0
0 3 0 0 0 5
3 0 0 0 1 0
0 0 1 4 0 0
```

## Approach
We declare a variable corresponding to each position in the sudoku 

| - | a | b | c | d | e | f |
|---|---|---|---|---|---|---|
| 1 |$a1|$b1|$c1|$d1|$e1|$f1|
| 2 |$a2|$b2|$c2|$d2|$e2|$f2|
| 3 |$a3|$b3|$c3|$d3|$e3|$f3|
| 4 |$a4|$b4|$c4|$d4|$e4|$f4|
| 5 |$a5|$b5|$c5|$d5|$e5|$f5|
| 6 |$a6|$b6|$c6|$d6|$e6|$f6|

The work is done in the `solution-rule`.
Each row, column and box is constrained to be a permutation of the numbers 1 to 6.

```typeql
# Each row must be a valid permutation 
(mem: $a1, mem: $b1, mem: $c1, mem: $d1, mem: $e1, mem: $f1) isa permutation;
(mem: $a2, mem: $b2, mem: $c2, mem: $d2, mem: $e2, mem: $f2) isa permutation;
(mem: $a3, mem: $b3, mem: $c3, mem: $d3, mem: $e3, mem: $f3) isa permutation;
(mem: $a4, mem: $b4, mem: $c4, mem: $d4, mem: $e4, mem: $f4) isa permutation;
(mem: $a5, mem: $b5, mem: $c5, mem: $d5, mem: $e5, mem: $f5) isa permutation;
(mem: $a6, mem: $b6, mem: $c6, mem: $d6, mem: $e6, mem: $f6) isa permutation;

# Each column must be a valid permutation
(mem: $a1, mem: $a2, mem: $a3, mem: $a4, mem: $a5, mem: $a6) isa permutation;
(mem: $b1, mem: $b2, mem: $b3, mem: $b4, mem: $b5, mem: $b6) isa permutation;
(mem: $c1, mem: $c2, mem: $c3, mem: $c4, mem: $c5, mem: $c6) isa permutation;
(mem: $d1, mem: $d2, mem: $d3, mem: $d4, mem: $d5, mem: $d6) isa permutation;
(mem: $e1, mem: $e2, mem: $e3, mem: $e4, mem: $e5, mem: $e6) isa permutation;
(mem: $f1, mem: $f2, mem: $f3, mem: $f4, mem: $f5, mem: $f6) isa permutation;

# Each box must be a valid permutation
(mem: $a1, mem: $b1, mem: $c1, mem: $a2, mem: $b2, mem: $c2) isa permutation;
(mem: $a3, mem: $b3, mem: $c3, mem: $a4, mem: $b4, mem: $c4) isa permutation;
(mem: $a5, mem: $b5, mem: $c5, mem: $a6, mem: $b6, mem: $c6) isa permutation;
(mem: $d1, mem: $e1, mem: $f1, mem: $d2, mem: $e2, mem: $f2) isa permutation;
(mem: $d3, mem: $e3, mem: $f3, mem: $d4, mem: $e4, mem: $f4) isa permutation;
(mem: $d5, mem: $e5, mem: $f5, mem: $d6, mem: $e6, mem: $f6) isa permutation;
```

In our database, we have a single permutation instance inserted as follows:
```typeql
$v1 = 1 isa number;
$v2 = 2 isa number;
$v3 = 3 isa number;
$v4 = 4 isa number;
$v5 = 5 isa number;
$v6 = 6 isa number;
(mem: $v1, mem: $v2, mem: $v3, mem: $v4, mem: $v5, mem: $v6) isa permutation;
```