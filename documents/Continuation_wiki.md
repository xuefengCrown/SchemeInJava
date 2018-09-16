
[Continuation, Wiki](https://en.wikipedia.org/wiki/Continuation)

在计算机科学和编程中， 延续性(continuation)指的是一种 计算机程序的控制状态的抽象表示。continuation 具体化了程序控制状态，
延续是表示过程执行中给定点的计算过程的数据结构; 创建的数据结构可以由编程语言访问，而不是隐藏在运行时环境中。
Continuations 对于实现编码语言中的其他控制机制很有用，例如异常，生成器，协同程序， 等等。

“ 当前继续 ”或“计算步骤的继续”是从运行代码的角度来看，将从程序执行中的当前点导出的延续。
术语continuation也可用于表示第一类continuation，它是一种结构，它使编程语言能够在任何点保存执行状态，并在程序的稍后时间点返回到该点，可能多次。


延续性也用于计算模型，包括指称语义，Actor模型，过程计算和lambda演算。这些模型依赖程序员或语义工程师以所谓的延续传递方式编写数学函数。
所谓 continuation-passing style. This means that each function consumes a function that represents the rest of the computation 
relative to this function call. To return a value, the function calls this "continuation function" with a return value; 
to abort the computation it returns a value.

Functional programmers who write their programs in continuation-passing style 
gain the expressive power to manipulate the flow of control in arbitrary ways. 
The cost is that they must maintain the invariants of control and continuations by hand, which is a highly complex undertaking.
以延续传递方式编写程序的函数式程序员获得了以任意方式操纵控制流的表达能力。成本是他们必须手工维持控制和延续的不变量，这是一项非常复杂的任务。






