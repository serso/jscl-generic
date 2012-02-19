package jscl.math.generic;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/18/12
 * Time: 11:02 PM
 */
public class GenericInteger extends Generic{

	public boolean isZero() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public GenericInteger add(@NotNull GenericInteger that) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public GenericInteger divide(@NotNull GenericInteger that) {
		throw new UnsupportedOperationException();
	}


	@NotNull
	public GenericInteger multiply(@NotNull GenericInteger that) {
		throw new UnsupportedOperationException();
	}


	@NotNull
	public static GenericInteger newInstance(long value) {
		throw new UnsupportedOperationException();
	}
}
