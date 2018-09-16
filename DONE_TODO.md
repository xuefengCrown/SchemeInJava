
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

  
一个 Java 写的 Scheme 解释器：https://github.com/xuefengCrown/SchemeInJava

没有比写一个解释器更能加深对编程语言的理解了。
CS61A 的最后一个大作业是用 Python 写一个 Scheme 解释器。https://inst.eecs.berkeley.edu/~cs61a/sp17/proj/scheme/

一般来说，解释器的词法分析部分是很繁琐和困难的，而这个project已经写好了词法分析和基本过程注册部分，而且搭好了整个框架，我们可以集中精力去写语言的核心部分。

由于 Scheme 语法的特殊性，其代码本身天然的就是一棵抽象语法树，这使得语法分析较其他语言要容易的多。

而 Python 对于函数式编程提供了一定的支持，在某方面与 Scheme比较相似，使得某些环节比较简洁，比如基本过程注册。

但是 Java 就没有这些便利了，使得像基本过程注册特别繁琐。但是
研究如何用 Java 实现 Scheme 的一些基本过程，可以加深对 Java 和 Scheme 的理解。
