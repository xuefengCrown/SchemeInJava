
[知乎：理解CPS](https://www.zhihu.com/question/20259086)

(+ 1 (+ 2 3))

上面的代码很容易转换为CPS（虽然加法操作是 primitive 的，但我们仍然可以自己编写一个CPS版本的 k+ 来模拟，同时我们还有一个 identity continuation 作为halt）：
(define k+ (lambda (x y k) (k (+ x y)))) ;; k+ 先求x+y,然后将结果作为参数调用k

(define id (lambda (x) x))

(k+ 2 3 (lambda (five) (k+ 1 five id)))
至于 call/cc 其实并不属于 CPS 的一部分，call/cc 是语言提供给程序员用以获得当前 continuation 的机制。在语言实现层面为了支持 call/cc 操作可以首先将程序进行CPS变换；或将解释器写为 CPS 形式。上面的 (+ 1 (+ 2 3)) 例子也可以用 call/cc 来实现:(+ 1 (call/cc (lambda (k) (k (+ 2 3)))))



    (define (pyth x y)
     (sqrt (+ (* x x) (* y y))))

    (define (pyth& x y k)
     (*& x x (lambda (x2)
              (*& y y (lambda (y2)
                       (+& x2 y2 (lambda (x2py2)
                                  (sqrt& x2py2 k))))))))

    (define (*& x y k) (k (* x y)))



