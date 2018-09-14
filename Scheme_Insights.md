
### code is data
In Scheme, source code is data. Every non-primitive expression is a list, and we can write 
procedures that manipulate other programs just as we write procedures that manipulate lists.

Rewriting programs can be useful: we can write an interpreter that only handles a small core 
of the language, and then write a procedure that converts other special forms into the core 
language before a program is passed to the interpreter.
