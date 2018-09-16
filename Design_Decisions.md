---
## 类设计

### 1. Lisp 版本(R4RS)
关于scheme语言规范，参考[Reference](https://github.com/xuefengCrown/SchemeInJava/blob/master/Reference.md)的语言规范部分;

我们这里先只实现scheme的一个子集，关于要实现的部分，参考[CS61A -- Project 4: A Scheme Interpreter 2017](https://inst.eecs.berkeley.edu/~cs61a/sp17/proj/scheme/)

### 2. 基本数据类型和基本过程
参考[jscheme: design decisions](http://www.norvig.com/jscheme-design.html)

have a class for the primitive procedures, so I could have an R4RS, and an R5RS version some day.(get the modularity)

### 3. 类结构
SchemeUtils             ---(the utility methods)
    Scheme              ---(解释器，包含一个读取代码的输入流管道，一个全局环境，一个read-eval-print-loop循环)
    Frame               ---(环境)
    Pair                ---(用于结构化 scheme表达式)
    InputPort           ---(输入流端口，负责读取scheme代码)
    Procedure           ---(所有过程的父类)
       Primitive        ---(基本过程)
       LambdaProcedure  ---(user-defined-procedure)
          Macro         ---(宏, 尚未实现)

### 4. 解释器组成
In order to build a Scheme interpreter, you basically need six things:
   **Read and write** The reader goes in the InputPort class.
    
   **Eval and apply** The eval method goes in the Scheme class.
   
   **Memory management**  Java handles this automatically.

   **Run-time stack** I use the Java run-time stack.
   
   **Primitive functions**
   
   **Primitive data types**
---
