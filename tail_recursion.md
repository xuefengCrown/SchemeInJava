普通递归：

    def recsum(x):
      if x == 1:
        return x
      else:
        return x + recsum(x - 1)

调用recsum(5)为例，SICP中描述了相应的栈空间变化[7]：

    recsum(5)
    5 + recsum(4)
    5 + (4 + recsum(3))
    5 + (4 + (3 + recsum(2)))
    5 + (4 + (3 + (2 + recsum(1))))
    5 + (4 + (3 + (2 + 1)))
    5 + (4 + (3 + 3))
    5 + (4 + 6)
    5 + 10
    15

可观察，堆栈从左到右，增加到一个峰值后再计算从右到左缩小，这往往是我们不希望的，所以在C语言等语言中设计for, while, goto等特殊结构语句，使用迭代、尾递归，对普通递归进行优化，减少可能对内存的极端消耗。修改以上代码，可以成为尾递归：

    def tailrecsum(x, running_total=0):
      if x == 0:
        return running_total
      else:
        return tailrecsum(x - 1, running_total + x)

或者使用迭代：

    for i in range(6):
      sum += i

对比后者尾递归对内存的消耗：

    tailrecsum(5, 0) 
    tailrecsum(4, 5) 
    tailrecsum(3, 9)
    tailrecsum(2, 12) 
    tailrecsum(1, 14) 
    tailrecsum(0, 15) 
    15

则是线性的。
