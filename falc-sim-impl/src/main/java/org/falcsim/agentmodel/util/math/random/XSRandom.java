package org.falcsim.agentmodel.util.math.random;

import java.util.Random;

/**
 * A subclass of java.util.random that implements the Xorshift random number
 * generator
 * 
 * - it is 30% faster than the generator from Java's library - it produces
 * random sequences of higher quality than java.util.Random - this class also
 * provides a clone() function
 * 
 * Usage: XSRandom rand = new XSRandom(); //Instantiation x = rand.nextInt();
 * //pull a random number
 * 
 * To use the class in legacy code, you may also instantiate an XSRandom object
 * and assign it to a java.util.Random object: java.util.Random rand = new
 * XSRandom();
 * 
 * for an explanation of the algorithm, see
 * http://demesos.blogspot.com/2011/09/pseudo-random-number-generators.html
 * 
 * @author Wilfried Elmenreich University of Klagenfurt/Lakeside Labs
 *         http://www.elmenreich.tk
 * 
 * This code is released under the GNU Lesser General Public License Version 3
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 */

public class XSRandom extends Random {
	private static final long serialVersionUID = 6208727693524452904L;
	private long seed;

	/**
	 * Creates a new pseudo random number generator. The seed is initialized to
	 * the current time, as if by
	 * <code>setSeed(System.currentTimeMillis());</code>.
	 */
	public XSRandom() {
		this(System.nanoTime());
	}

	/**
	 * Creates a new pseudo random number generator, starting with the specified
	 * seed, using <code>setSeed(seed);</code>.
	 * 
	 * @param seed
	 *            the initial seed
	 */
	public XSRandom(long seed) {
		this.seed = seed;
	}

	/**
	 * Returns the current state of the seed, can be used to clone the object
	 * 
	 * @return the current seed
	 */
	public synchronized long getSeed() {
		return seed;
	}

	/**
	 * Sets the seed for this pseudo random number generator. As described
	 * above, two instances of the same random class, starting with the same
	 * seed, produce the same results, if the same methods are called.
	 * 
	 * @param seed
	 *            the new seed
	 */
	public synchronized void setSeed(long seed) {
		this.seed = seed;
		super.setSeed(seed);
	}

	/**
	 * Returns an XSRandom object with the same state as the original
	 */
	public XSRandom clone() {
		return new XSRandom(getSeed());
	}

	/**
	 * Implementation of George Marsaglia's elegant Xorshift random generator
	 * 30% faster and better quality than the built-in java.util.random see also
	 * see http://www.javamex.com/tutorials/random_numbers/xorshift.shtml

	 */
	protected int next(int nbits) {
		long x = seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		seed = x;
		x &= ((1L << nbits) - 1);
		return (int) x; 
	}

}
