----- Thodoris Baniokos
----- 1115201800121
----- sdi1800121@di.uoa.gr

Parser and Translator to Java for a language supporting string operations

Some files and declerations are the same as the ones provided by the course's
lab staff.

Added code in scanner.flex

- Added a regex to identify identifiers
- Added all the keywords at the top of YYINITIAL since we want these to be treated
  as reserved words for the language
- Added all the seperators after the keywords and made a special seperator for function
  declerations, with this line of code  ")" + {WhiteSpace} + "{" { return symbol(sym.METHOD_DEC); }.
  The above will be returned if a right parenthesis plus one/many whitespace/ces plus a left curly bracket
  is found. This is a characteristic of the function declerations only. This choice was made to avoid many
  Shift/Reduce and Reduce/Reduce conflicts. Also this decleration is above the one for the right parenthesis
  since we want this to be recognized first, this is JFlex specification.

Added code in parser.cup

- All the grammar and decleration for terminals, non terminals, precedence is in this file

- The input program structure must be the method declerations first and after that the method calls
- It could also have no declerations and function calls just a main body
- If the input file is blank, the smallest Java program is produced
- In src/input_files are some files I used for testing

- To run the program run the commands specified below in project1/part2/src, some input files that I used are
  in project1/part2/src/input_files:

  make && make execute-stdout < desired-input-file: to print the output program in the terminal.
  make && make execute-java < desired-input-file: to create a java file in project1/part2/