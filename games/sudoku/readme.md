# Sudoku
In this example, we consider a smaller 6x6 version of the sudoku.
The aim is to fill in the grid with numbers from 1 to 6 such that no row, column or box contains the same number twice.
Boxes are 2 rows tall x 3 columns wide.
Sudoku is a popular puzzle involving a 9x9 grid.
Some of the numbers will be filled in as part of the puzzle.


## Usage
The driver program requires an instance of typedb server to be running. 
If you are not running TypeDB locally on the default port, specify the address as the second commandline parameter.

### Setup
1. Install the dependencies:
    * `python -m pip requirements.txt`
2. Create the database with required schema and data:
    * `python driver.py setup [typedb-address]`
  
### Running the solver
`python driver.py <sudoku-path> [typedb-address]`

### Running tests
`bazel test //games/sudoku:test`  or `python test.py`
    
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
The work is done in the `solution-rule`.

Each row, column and box is constrained to be a permutation of the numbers 1 to 6.
We have one variable corresponding to each position in the sudoku. 

| - | a | b | c | d | e | f |
|---|---|---|---|---|---|---|
| 1 |$a1|$b1|$c1|$d1|$e1|$f1|
| 2 |$a2|$b2|$c2|$d2|$e2|$f2|
| 3 |$a3|$b3|$c3|$d3|$e3|$f3|
| 4 |$a4|$b4|$c4|$d4|$e4|$f4|
| 5 |$a5|$b5|$c5|$d5|$e5|$f5|
| 6 |$a6|$b6|$c6|$d6|$e6|$f6|

 
```typeql
rule solution-rule:
when {
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
} then {
  (
    pos11: $a1, pos12: $b1, pos13: $c1, pos14: $d1, pos15: $e1, pos16: $f1,
    pos21: $a2, pos22: $b2, pos23: $c2, pos24: $d2, pos25: $e2, pos26: $f2,
    pos31: $a3, pos32: $b3, pos33: $c3, pos34: $d3, pos35: $e3, pos36: $f3,
    pos41: $a4, pos42: $b4, pos43: $c4, pos44: $d4, pos45: $e4, pos46: $f4,
    pos51: $a5, pos52: $b5, pos53: $c5, pos54: $d5, pos55: $e5, pos56: $f5,
    pos61: $a6, pos62: $b6, pos63: $c6, pos64: $d6, pos65: $e6, pos66: $f6
  ) isa solution;
};
```

In our database, we have a single permutation instance inserted as follows:
```typeql
insert 
$v1 = 1 isa number;
$v2 = 2 isa number;
$v3 = 3 isa number;
$v4 = 4 isa number;
$v5 = 5 isa number;
$v6 = 6 isa number;
(mem: $v1, mem: $v2, mem: $v3, mem: $v4, mem: $v5, mem: $v6) isa permutation;
```


To query it, We add a statement for the variables which we know the value of (e.g. `$v13 = 3 isa number;`) and query the rule:
```typeql
match
    $v13 = 3 isa number;
    $v15 = 1 isa number;
    # ...
    $v62 = 4 isa number;
    $v64 = 1 isa number;
    (
        pos11: $v11, pos12: $v12, pos13: $v13, pos14: $v14, pos15: $v15, pos16: $v16,
        pos21: $v21, pos22: $v22, pos23: $v23, pos24: $v24, pos25: $v25, pos26: $v26,
        pos31: $v31, pos32: $v32, pos33: $v33, pos34: $v34, pos35: $v35, pos36: $v36,
        pos41: $v41, pos42: $v42, pos43: $v43, pos44: $v44, pos45: $v45, pos46: $v46,
        pos51: $v51, pos52: $v52, pos53: $v53, pos54: $v54, pos55: $v55, pos56: $v56,
        pos61: $v61, pos62: $v62, pos63: $v63, pos64: $v64, pos65: $v65, pos66: $v66
    ) isa solution;
    limit 1;
```