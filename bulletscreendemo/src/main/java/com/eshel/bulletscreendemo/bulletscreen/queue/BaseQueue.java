package com.eshel.bulletscreendemo.bulletscreen.queue;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 项目名称: Demo
 * 创建人: Eshel
 * 创建时间:2017/8/3 10时09分
 * 描述: TODO
 */

public class BaseQueue<T>{
	LinkedList<T> value;

	public BaseQueue() {
		this.value = new LinkedList<>();
	}

	public int size() {
		if(value != null)
			return value.size();
		return 0;
	}

	public boolean isEmpty() {
		return value == null || value.size() == 0;
	}

	public boolean inQueue(T o) {
		return value.add(o);
	}

	public T outQueue() {
		T t = isEmpty() ? null : value.pollFirst();
		return t;
	}
/*	public boolean contains(Object o) {
		return value.contains(o);
	}

	public Iterator iterator() {
		return value.iterator();
	}*/

/*	public Object[] toArray() {
		return value.toArray();
	}

	public Object[] toArray(Object[] a) {
		return value.toArray(a);
	}*/
	public boolean addAll(Collection c) {
		return value.addAll(c);
	}

	public void clear() {
		value.clear();
	}
	@Override
	public boolean equals(Object o) {
		return value.equals(o);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	public boolean retainAll(Collection c) {
		return value.retainAll(c);
	}

	public boolean removeAll(Collection c) {
		return value.removeAll(c);
	}

	public boolean containsAll(Collection c) {
		return value.containsAll(c);
	}

	public boolean offer(T o) {
		return value.offer(o);
	}

	public T remove(int index) {
		return value.remove(index);
	}

	public T poll() {
		return value.poll();
	}

	public T element() {
		return value.element();
	}

	public T peek() {
		return value.peek();
	}
}
