
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
### 1. 从任何输入流读取字符的能力
标准I/O流的支持：在不做任何设定的前提下，分析器从标准输入流读取字符。但是，作为解析器，
应该具备从任何输入流读取字符的能力，需要增加一个函数接口来实现从某个输入流读取字符进行语法分析。

### 2. dynamically scope procedure

### 3. macro

### 4. tail call elimination(消除 tail recursion)
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

  
