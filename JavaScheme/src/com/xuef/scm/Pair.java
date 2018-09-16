package com.xuef.scm;

/**
 * A Pair has two fields, first and rest (or car and cdr). The empty list is
 * represented by null. The methods that you might expect here, like first,
 * second, list, etc. are instead static methods in class SchemeUtils.
 * 
 * Pair �൱�� Scheme�е� cons, ����ʾһ����������ָ��ĺ���(car, cdr)
 * ��������������ʾ����ṹ������, �����������˾���!
 * @author Peter Norvig, peter@norvig.com http://www.norvig.com Copyright 1998
 *         Peter Norvig, see http://www.norvig.com/license.html
 */

public class Pair extends SchemeUtils {

	/** The first element of the pair. **/
	public Object first;

	/** The other element of the pair. **/
	public Object rest;

	public Pair(Object first, Object rest) {
		this.first = first;
		this.rest = rest;
	}
	public Pair(){}

	/** 
	 * Two pairs are equal if their first and rest fields are equal. 
	 * pair1.equals(pair2)
	 */
	public boolean equals(Object x) {
		// ͬһ������(ͬһ����)
		if (x == this) return true;
		else if (!(x instanceof Pair))
			return false;
		else {
			Pair that = (Pair) x;
			// �� SICP �о���д���ֵݹ麯��
			return equal(this.first, that.first) && equal(this.rest, that.rest);
		}
	}
	/**
	 * Pair �ڲ�������ַ�����ʽ
	 * @return
	 * xuef 180912
	 */
	public String repr4pair(){
		Object car = this.first;
		Object cdr = this.rest;
		
		String carStr = "nil";
		String cdrStr = "nil";
		if(car == null){
			;
		}else if(!(car instanceof Pair)){
			carStr = car.toString();
		}else{
			carStr = ((Pair)car).repr4pair();
		}
		
		if(cdr == null){
			;
		}else if(!(cdr instanceof Pair)){
			cdrStr = cdr.toString();
		}else{
			cdrStr = ((Pair)cdr).repr4pair();
		}
		return String.format("Pair(%s, %s)", carStr, cdrStr);
	}

	/** 
	 * Return a String representation of the pair. 
	 */
	public String toString() {
		return stringify(this, true);
	}

	/** 
	 * Build up a String representation of the Pair in a StringBuffer.
	 * һ�ֵ��͵ķ���ֵ�ķ�ʽ: ͨ���ı���������������ֵ
	 *  buf �൱�ڶ���ָ�룬����c�����г����ķ���ֵ�ķ�ʽ
	 */
	void stringifyPair(boolean quoted, StringBuffer buf) {
		String special = null;
		if ((rest instanceof Pair) && rest(rest) == null)
			special = (first == "quote") ? "'" : (first == "quasiquote") ? "`"
					: (first == "unquote") ? ","
							: (first == "unquote-splicing") ? ",@" : null;

		if (special != null) {
			buf.append(special);
			stringify(second(this), quoted, buf);
		} else {
			buf.append('(');
			stringify(first, quoted, buf);
			Object tail = rest;
			while (tail instanceof Pair) {
				buf.append(' ');
				stringify(((Pair) tail).first, quoted, buf);
				tail = ((Pair) tail).rest;
			}
			if (tail != null) {
				buf.append(" . ");
				stringify(tail, quoted, buf);
			}
			buf.append(')');
		}
	}

}
