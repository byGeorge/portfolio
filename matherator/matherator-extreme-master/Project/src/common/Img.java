package common;

import java.io.InputStream;

public class Img {
	private static Class klas;
	static {
		Object fooObject = new Object();
		klas = fooObject.getClass();
	}
	
	public static InputStream get(String name) {
		return klas.getResourceAsStream(name);
	}

}
