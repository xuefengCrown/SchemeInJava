
## DONE
### 基本过程

## Special Form
1. define

2. if, cond

3. and, or

4. lambda

5. let

6. begin

7. quote

## TODO
### dynamically scope procedure

### macro

### tail call elimination(消除 tail recursion)
  I do take care to make the interpreter properly tail recursive: calls in the tail position do not grow the stack.

scm> (define (xeven? x)
	(if (= x 0) 
		#t 
		(xodd? (- x 1))))
xeven?
scm> (define (xodd? x)
	(if (= x 0)
		#f
		(xeven? (- x 1))))
xodd?
scm> (xodd? 40000)

Exception in thread "main" java.lang.StackOverflowError
	at com.xuef.scm.Frame.lookup(Frame.java:67)
  
  
