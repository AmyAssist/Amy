/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.speechrecognition;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO: Description
 *
 * @author Tim Neumann, Patrick Gebhardt, Patrick Singer, Florian Bauer, Kai
 *         Menzel
 */
public class MyInputStream extends InputStream {

	private ArrayBlockingQueue<Integer> bytes;

	/**
	 * @param p_bytes
	 *            The byte queue
	 *
	 */
	public MyInputStream(ArrayBlockingQueue<Integer> p_bytes) {
		this.bytes = p_bytes;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		while (true) {
			try {
				int i = this.bytes.take().intValue();
				return i;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read but not return -1 on getting -1 from read.
	 *
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null)
			throw new NullPointerException();
		else if (off < 0 || len < 0 || len > b.length - off)
			throw new IndexOutOfBoundsException();
		else if (len == 0)
			return 0;

		int c = read();
		b[off] = (byte) c;

		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				b[off + i] = (byte) c;
			}
		} catch (IOException ee) {
		}
		return i;
	}

}
