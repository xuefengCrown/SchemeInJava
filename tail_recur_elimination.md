
Tail call elimination is an optimization that saves stack space. It replaces a function call with a goto.
Tail recursion elimination is the same thing, but with the added constraint that the function is calling itself.

Basically, if the very last thing a function A does is return A(params...) 
then you can eliminate the allocation of a stack frame and instead set the appropriate registers and jump directly 
into the body of the function.

In Scheme, the language detects when you have something like the helper function in the example above where your 
return statement consists of only the recursive call. In the first example, we have the n * and the recursive 
call which means it cannot be tail optimized because it needs to keep track of all of the frames that it creates. 
In a tail optimized call, Scheme will get rid of the frames that are no longer necessary.


