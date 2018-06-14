package de.unistuttgart.iaas.amyassist.amy.core.di.util;

import java.util.function.Function;

/**
 * A simple n Tuple implementation
 * 
 * @author Leon Kiefer
 */
public class NTuple<T> {
	private T[] vector;

	public final int n;

	@SuppressWarnings("unchecked")
	public NTuple(int size) {
		this.n = size;
		this.vector = (T[]) new Object[size];
	}

	public T get(int i) {
		return this.vector[i];
	}

	public void set(int i, T value) {
		this.vector[i] = value;
	}

	public <R> NTuple<R> map(Function<T, R> function) {
		NTuple<R> nTuple = new NTuple<>(this.n);
		for (int i = 0; i < this.n; i++) {
			nTuple.set(i, function.apply(this.get(i)));
		}
		return nTuple;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NTuple)) {
			return false;
		}
		NTuple<?> comapareTuple = (NTuple<?>) obj;
		if (this.n != comapareTuple.n) {
			return false;
		}

		for (int i = 0; i < this.n; i++) {
			if (!this.get(i).equals(comapareTuple.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (T value : this.vector) {
			hashCode += value.hashCode();
		}
		return hashCode;
	}
}
