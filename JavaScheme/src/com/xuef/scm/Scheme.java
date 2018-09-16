package com.xuef.scm;

import java.io.PrintWriter;

public class Scheme extends SchemeUtils {
	/**
	 * ��ֵ, ��Ҫ:
	 * 1. Ŀ����ʽ(�������� InputPort ����������ַ������õ�)
	 * 2. ���������Ļ���(Environment)
	 */
	InputPort input = new InputPort(System.in);
	PrintWriter output = new PrintWriter(System.out, true);
	Frame globalEnv = new Frame(null);
	
	public Scheme(){
		// ��ʼ��ȫ�ֻ���--ע���������
		Primitive.installPrimitives(globalEnv);
	}
	public static void main(String[] args) {
		// ���� repl(����������)
		new Scheme().readEvalWriteLoop();
	}

	/**
	 * Prompt, read, eval, and write the result. 
	 * Also sets up a catch for any RuntimeExceptions encountered.
	 */
	@SuppressWarnings("static-access")
	private void readEvalWriteLoop() {
		Object x;
		while(true) {
			try {
				output.print("scm> ");
				output.flush();
				if (input.isEOF(x = input.read()))
					return;
				
				write(eval(x, globalEnv), output, true);
				
				//String repr4pair = ((Pair)x).repr4pair();
				//write(repr4pair, output, false);
				output.println();
				output.flush();
			} catch (RuntimeException e) {
				;
			}
		}
	}
	
	////////////////Evaluation
	/**
	 * ����ͨ�����ṹһ�����﷨����Ľڵ㣬Ҫô��һ��"Ҷ�ڵ�"��Ҫô��һ��"����"��
	 * Ҷ�ڵ��ǲ�����ϸ�ֵ�"ԭ��"���������֣��ַ���������������������
	 * �������ǿ�����ϸ�ֵ�"�ṹ"�������������ʽ���������壬�������ã��ȵȡ�
	 *
	 * evaluates a Scheme expression(x) in a given environment(env). 
	 * 	self-evaluating expressions: numbers, booleans, and nil
	 */
	public Object eval(Object x, Frame env) {
		// The purpose of the while loop is to allow tail recursion.
		// The idea is that in a tail recursive position, we do "x = ..."
		// and loop, rather than doing "return eval(...)".
		while (true) { // ����ʹ��ѭ����Ϊ�˴��� β�ݹ�
			/* Evaluate atoms */
			if (x instanceof String) { 				// VARIABLE
				return env.lookup((String) x);
			} else if (!(x instanceof Pair)) { 		// CONSTANT
				return x;
			}
			// TODO ���õĴ�����ʾ
			
			/* form: 
			 * 1. special form; 
			 * 	'and': do_and_form, 'or': do_or_form,
			 *  'begin': do_begin_form,
			 *  'if': do_if_form, 'cond': do_cond_form,
			 *  'define': do_define_form,
			 *  'lambda': do_lambda_form,
			 *  'let': do_let_form,
			 *  'quote': do_quote_form,
			 *  'define-macro': do_define_macro,
			 *  'set!': do_set_form,
			 *  'quasiquote': do_quasiquote_form,
			 *  'unquote': do_unquote
			 *  
			 *  Logical special forms include if, and, or, and cond.
			 *  These expressions are special because not all of their sub-expressions may be evaluated.
			 * 2. procedure call 
			 * 	 1) primitive proc call
			 *   2) user-defined proc call (Closure Call)
			 *   3) MACRO CALL
			 */
			Object fn = first(x);
			Object args = rest(x);
			if (fn == "quote") { // QUOTE
				return doQuoteForm(args, env);
			} else if (fn == "begin") { // BEGIN
				// A begin expression is evaluated by evaluating all sub-expressions in order.
				// ���ǣ�����Ϊ���Ż�β�ݹ���������һ����䲻ִ�С�ע����������ν�β�ݹ����ת��Ϊһϵ��ѭ����
				x = doBegin_TCE(args, env); // ??????
			} else if (fn == "define") { // DEFINE
				// ������� �������
				return doDefineForm(args, env);
			} else if (fn == "set!") { // SET!
				// TODO
			} else if (fn == "if") { // IF
				x = doIfForm(args, env);
			} else if (fn == "and"){
				return doAndForm(args, env);
			} else if (fn == "or"){
				return doOrForm(args, env);
			} else if (fn == "cond") { // COND
				x  = reduceCondForm(args, env); // ��Ѱ�ҵ�һ��true clause, then eval it.
			} else if (fn == "lambda") { // LAMBDA
				return doLambdaForm(args, env);
			} else if (fn == "let") { // LET
				Pair pair = list(null);
				x = doLetForm(args, env, pair);
				return ((LambdaProcedure)x).apply(this, pair.first);
			} else if (fn == "macro") { // MACRO
				// TODO �궨��
			} else { // PROCEDURE CALL:
				/**
				 * To evaluate a call expression, we do the following:
				 * 	1. Evaluate the operator (which should evaluate to a Scheme procedure)
				 *  2. Evaluate the operands
				 *  3. Apply the procedure on the evaluated operands.
				 */
				fn = eval(fn, env);
				// TODO (MACRO CALL)

				// (LAMBDA PROCEDURE CALL)
				if (fn instanceof LambdaProcedure) { // (CLOSURE CALL)
					LambdaProcedure f = (LambdaProcedure) fn;
					x = f.body;
					env = new Frame(f.formals, evalList(args, env), f.env);
				}
				// (OTHER PROCEDURE CALL) such as primitive procedure
				else {
					return Procedure.proc(fn).apply(this, evalList(args, env));
				}
			}
			
		}
	}
	/**
	 * let���ʽҲ��һ���������ʽ������ת��Ϊ����ϱ��ʽ��ֵ(һ���������̵ĵ���)
	 * ((lambda (var1 var2 ...) <body>) <exp1> <exp2> <..>)
	 * @param args
	 * @param env
	 * @return
	 */
	Object doLetForm(Object args, Frame env, Pair p) {
		// ��ȡ�����б� ��ȡֵ�б�
		// TODO �󶨲����ĸ�ʽУ��
		/*
		 * check_form: this function can be used to check the structure of each binding.
		 * check_formals: this function checks that formal parameters are a Scheme list 
		 * 		of symbols for which each symbol is distinct.
		 */
		// (let ((x 5) (y 6)) (+ x y))
		//Procedure car = (Procedure) eval("car"); // car ��һ�� primitive-procedure
		Object formalsPairs = first(args);
		Object body = rest(args);
		
		Object pair = first(formalsPairs);
		Pair vars = list(first(pair));
		Pair vals = list(eval(second(pair), env));
		
		Pair vars_tmp = vars, vals_tmp = vals;
		Object rest = rest(formalsPairs);
		while(rest != null){
			pair = first(rest);
			vars_tmp.rest = list(first(pair));
			vars_tmp = (Pair) vars_tmp.rest;
			
			vals_tmp.rest = list(eval(second(pair), env));
			vals_tmp = (Pair) vals_tmp.rest;
			rest = rest(rest);
		}
		p.first = vals;
		return new LambdaProcedure(vars, body, env);
	}
	/**
	 * cond ��if���﷨��; �����Ƚ�cond exprת��Ϊ��Ӧ�� if expr,Ȼ����ֵ;
	 * ����ֱ����ֵ(Ѱ�� true clause)
	 * 
	 * it returns the value of the first result sub-expression corresponding to a true predicate, 
	 * or the result sub-expression corresponding to else. Some special cases:
	 * 		1.When the true predicate does not have a corresponding result sub-expression, 
	 * 		  return the predicate value.
	 *		2.When a result sub-expression of a cond case has multiple expressions, evaluate them all and return the 
	 *		  value of the last expression. (Hint: Use eval_all.)
	 * @param clauses
	 * @param env
	 * @return
	 */
	Object reduceCondForm(Object clauses, Frame env) {
		/*
		 * (cond ((> x 0) 1)
		  	     ((< x 0) -1)
		         (else 0))
		 */
		@SuppressWarnings("unused")
		Object result = null;
		while(true){ // ����û�г��ڵ� case
			if(clauses == null){
				return FALSE;
			}
			Object clause = first(clauses);
			clauses = rest(clauses);
			Object predicate = first(clause);
			if (predicate == "else" || truth(result = eval(predicate, env))){
				if (rest(clause) == null) // handle (cond ((= 4 4)) (else 'hm))
					return list("quote", result);
				return cons("begin", rest(clause)); // ע������append�� begin, ����Ҫ
			}
		}
		// unreachable section
	}
	/**
	 * For or, evaluate each sub-expression from left to right. 
	 * If any sub-expression evaluates to a true value, return that value. Otherwise, return False. 
	 * If there are no sub-expressions in an or expression, it evaluates to False.
	 * @param args
	 * @param env
	 * @return
	 */
	Object doOrForm(Object args, Frame env) {
		Object rest = args;
		while(rest != null){
			Object firstVal = eval(first(rest), env);
			if(truth(firstVal)){
				return firstVal;
			}else{
				rest = rest(rest);
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * The logical forms and and or are short-circuiting.
	 * For and, your interpreter should evaluate each sub-expression from left to right,
	 * and if any of these evaluates to a false value, then #f is returned.
	 * Otherwise, it should return the value of the last sub-expression.
	 * If there are no sub-expressions in an and expression, it evaluates to #t.
	 * 
	 * > (and #t #f 42 (/ 1 0))
	 * > #f
	 * @param args
	 * @param env
	 * @return
	 */
	Object doAndForm(Object args, Frame env) {
		Object rs = Boolean.TRUE;
		Object rest = args;
		while(rest != null){
			if(!truth(eval(first(rest), env))){
				return Boolean.FALSE;
			}else{
				rs = eval(first(rest), env);
				rest = rest(rest);
			}
		}
		return rs;
	}
	/**
	 * �����Ȳ���ֵ����ֻ���� true clause�� Ϊ�˺����β�ݹ��Ż�!!!
	 * (if (> x 0)
	 *     1
	 *     -1)
	 * @param args (a Pair)
	 * @param env
	 * @return
	 */
	Object doIfForm(Object args, Frame env) {
		// �ֽ�� ν���ж���true��֧��false��֧ ����
		Object predicate = first(args);
		Object trueClause = first(rest(args));
		Object falseClause = first(rest(rest(args)));
		// ���� scheme���߼�����
		if(truth(eval(predicate, env))){
			return trueClause;
		}else{
			return falseClause;
		}
	}
	Object doLambdaForm(Object args, Frame env) {
		Object formals = first(args);
		Object body = rest(args);
		LambdaProcedure proc = new LambdaProcedure(formals, body, env);
		return proc;
	}
	/*
	 * A begin expression is evaluated by evaluating all sub-expressions in order. 
	 * The value of the begin expression is the value of the final sub-expression.
	 */
	@Deprecated
	Object doBeginForm(Object expressions, Frame env) {
		// û�ж�β�ݹ�����Ż�
		return evalAll(expressions, env);
	}
	
	// Tail Recursion Elimination
	Object doBegin_TCE(Object args, Frame env){
		for (; rest(args) != null; args = rest(args)) {
			eval(first(args), env);
		}
		return first(args);
	}
	/**
	 * Evaluate each expression in the Scheme list EXPRESSIONS in
	 * environment env and return the value of the last.
	 * @param expressions
	 * @param env
	 * @return
	 */
	Object evalAll(Object expressions, Frame env){
		if(expressions == null){
			return null;
		}
		Object result = eval(first(expressions), env);
		Object rest = rest(expressions);
		while(rest != null){
			result = eval(first(rest), env);
			rest = rest(rest);
		}
		return result;
	}
	/*
	 * define special form in Scheme can be used to either
		assign a name to the value of a given expression
		or to create a procedure and bind it to a name.
		    scm> (define a (+ 2 3))  ; Binds the name a to the value of (+ 2 3)
		    a
		    scm> (define (foo x) x)  ; Creates a procedure and binds it to the name foo
		The type of the first operand tells us what is being defined:
		1. If it is a symbol, e.g. a, then the expression is defining a name
		2. If it is a list, e.g. (foo x), then the expression is defining a procedure.
	 */
	String doDefineForm(Object args, Frame env) {
		// TODO ���ڱ��ʽ���﷨���õģ� ���define���ʽ�������﷨��Ӧ����δ���?
		Object car = first(args);
		if(!(car instanceof Pair)){ // define ��׼��ʽ
			String symbol = (String)car;
			Object val = eval(second(args), env);
			return env.define(symbol, val);
		}else{
			/*  
			 * handles the shorthand procedure definition form above. 
			 * (define (square x) (* x x))
			 * Make sure that it can handle multi-expression bodies.
			 * 
			 * (define (xodd? x)
			 * 	 (if (= x 0)
			 *   #f
			 *   (xeven? (- x 1))))
			 */
			String funcName = (String)first(car);
			// rewrite programs, �������ֶ�ִ�зǱ�׼��ʽ!!!
			return env.define(
					funcName,
					eval(cons("lambda", 
							   cons(rest(first(args)), rest(args))),
						 env));
		}
	}

	/*
	 *  evaluates the quote special form. 
	 *  it simply returns the unevaluated operand to the special form.
	 *  The quote special form returns its operand expression without evaluating it.
	 */
	Object doQuoteForm(Object args, Frame env){
		// check_form()
		return first(args);
	}
	/** 
	 * Evaluate each of a list of expressions. 
	 */
	Pair evalList(Object list, Frame env) {
		if (list == null)
			return null;
		else if (!(list instanceof Pair)) {
			error("Illegal arg list: " + list);
			return null;
		} else
			return cons(eval(first(list), env), evalList(rest(list), env));
	}
	/** 
	 * Eval in the global environment. 
	 */
	public Object eval(Object x) {
		return eval(x, this.globalEnv);
	}
}
