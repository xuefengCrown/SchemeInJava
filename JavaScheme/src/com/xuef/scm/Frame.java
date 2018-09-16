package com.xuef.scm;

import java.util.HashMap;
import java.util.Map;

/**
 * Environments allow you to look up the value of a variable, given its name.
 * Keep a list of variables and values, and a pointer to the parent environment.
 * If a variable list ends in a symbol rather than null, it means that symbol is
 * bound to the remainder of the values list.
 * 
 * @author Peter Norvig, peter@norvig.com http://www.norvig.com Copyright 1998
 *         Peter Norvig, see http://www.norvig.com/license.html
 */

public class Frame extends SchemeUtils {
	/**
	 * bindings is a dictionary that maps Scheme symbol keys (represented as Java Strings) to Scheme values.
	 * parent is the parent Frame instance. The parent of the Global Frame is None.
	 */
	public Map<String, Object> bindings;
	public Frame parent;

	public Frame(Frame parent) {
		this.bindings = new HashMap<>();
		this.parent = parent;
	}
	
	public Frame(Map<String, Object> bindings, Frame parent){
		this.bindings = bindings;
		this.parent = parent;
	}
	/** 
	 * Construct an empty environment: no bindings. 
	 */
	public Frame() {}
	/**
	 * 
	 * @param vars (a Pair)
	 * @param vals (a Pair)
	 * @param parent
	 */
	public Frame(Object vars, Object vals, Frame parent){
		/**
		 * ����� formals ��һ�� Pair, ��Frame��bindings ȴ��һ�� Map, ���ǲ��ò�����ת��, ���鷳!
		 */
		// binding formal parameters to argument values
		Map<String, Object> formal_valList = new HashMap<String, Object>();
		Object restFormals = vars;
		Object restArgs = vals;
		while(restFormals != null){
			formal_valList.put((String)first(restFormals), first(restArgs));
			restFormals = rest(restFormals);
			restArgs = rest(restArgs);
		}
		this.bindings = formal_valList;
		this.parent = parent;
	}

	/** 
	 * Return the value bound to SYMBOL. Errors if SYMBOL is not found.
	 * Hint: 
	 * 	Recall that an environment is defined as a frame, its parent frame,
     * 	and all its ancestor frames, including the Global Frame. Therefore
	 */
	public Object lookup(String symbol) {
		if(this.bindings.containsKey(symbol)){
			return this.bindings.get(symbol);
		}
		// If not, try to look for the parent
		if (parent != null)
			return parent.lookup(symbol);
		else
			return error("Unbound variable: " + symbol);
	}
	/**
	 * define takes a symbol (represented by a Java string) && value 
	 * and binds the value to that symbol in the frame.
	 */
	public String define(String symbol, Object val){
		this.bindings.put(symbol, val);
		return symbol;
	}
	
	public Frame defPrim(String name, int id, int minArgs) {
		define(name, new Primitive(id, minArgs, minArgs));
		return this;
	}

	/**
	 * ����������ע�����ǰ����
	 * 
	 * ���� ���԰Ѻ�����������(������Ϊ��һ��Ԫ��)��������˵(�� Scheme, Python),
     * �� ���� + ע������� globalEnv �У� ֻ��Ҫ���һ�� '+'-->#[+] �İ󶨼���;
     * Java�������?
	 * @param name: primitive-procedure name
	 * @param id: ���� id
	 * @param minArgs: ����������Сֵ 
	 * @param maxArgs:
	 * @return
	 */
	public Frame defPrim(String name, int id, int minArgs, int maxArgs) {
		define(name, new Primitive(id, minArgs, maxArgs));
		return this;
	}
}
