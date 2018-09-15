
Tail call elimination is an optimization that saves stack space. It replaces a function call with a goto.
Tail recursion elimination is the same thing, but with the added constraint that the function is calling itself.

Basically, if the very last thing a function A does is return A(params...) 
then you can eliminate the allocation of a stack frame and instead set the appropriate registers and jump directly 
into the body of the function.

In Scheme, the language detects when you have something like the helper function in the example above where your 
return statement consists of only the recursive call. In the first example, we have the n * and the recursive 
call which means it cannot be tail optimized because it needs to keep track of all of the frames that it creates. 
In a tail optimized call, Scheme will get rid of the frames that are no longer necessary.

### tail call elimination
When a function performs a tail call, its own activation frame is dead, as by definition nothing follows the tail call.
Therefore, it is possible to first free the activation frame of a function about to perform such a call, then load the
parameters for the call, and finally jump to the function’s code.
This technique is called tail call elimination (or optimisation), abbreviated TCE.

    def tailrecsum(x, total=0):
        if x == 0:
          return total
        else:
          return tailrecsum(x - 1, total + x) ;; 不必新开栈帧，直接在当前栈帧执行!


## Implementing TCE
Tail call elimination is implemented by:
1. identifying tail calls in the program,
2. compiling those tail calls specially, by deallocating(释放，解除) the activation frame of the caller 
before jumping to the called function.

We already know how to compile tail calls, but we did not explain yet how to identify them.

### Identifying tail calls
??????

### TCE in various environments
When generating assembly language, it is easy to perform TCE, as the target language is sufficiently low-level to
express the deallocation of the activation frame and the following jump.

When targeting higher-level languages, like C or the JVM, this becomes difficult – although recent VMs like .NET’s
support tail calls. We explore several techniques that have been developed to perform TCE in such contexts.

