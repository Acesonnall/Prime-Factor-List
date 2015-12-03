package edu.iastate.cs228.hw3;

/**
 *  
 * @author Omar Taylor
 *
 */

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class PrimeFactorization implements Iterable<PrimeFactor> {
	private static final long OVERFLOW = -1;
	private long value; // the factored integer
						// it is set to OVERFLOW when the number is greater than
						// 2^63-1,
						// the largest number representable by the type long.

	/**
	 * Reference to dummy node at the head.
	 */
	private Node head;

	/**
	 * Reference to dummy node at the tail.
	 */
	private Node tail;

	private int size; // number of distinct prime factors

	// ------------
	// Constructors
	// ------------

	/**
	 * Default constructor constructs an empty list to represent the number 1.
	 * 
	 * Combined with the add() method, it can be used to create a prime
	 * factorization.
	 */
	public PrimeFactorization() {
		this(1);
		value = 1;
	}

	/**
	 * Obtains the prime factorization of n and creates a doubly linked list to
	 * store the result. Follows the algorithm in Section 1.2 of the project
	 * description.
	 * 
	 * @param n
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public PrimeFactorization(long n) throws IllegalArgumentException {
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		size = 0;
		PrimeFactorizationIterator iter = new PrimeFactorizationIterator();
		int multiplicityCounter = 0;
		int d;

		for (d = 2; d * d <= n; d++) {
			while (n % d == 0) {
				multiplicityCounter++;
				n /= d;
			}
			if (multiplicityCounter != 0) {
				PrimeFactor pf = new PrimeFactor(d, multiplicityCounter);
				iter.add(pf);
				multiplicityCounter -= multiplicityCounter;
			}
		}

		if (n > 1) {
			PrimeFactor pf = new PrimeFactor((int) n, 1);
			iter.add(pf);
		}
	}

	/**
	 * Copy constructor. It is unnecessary to verify the primality of the
	 * numbers in the list.
	 * 
	 * @param pf
	 */
	public PrimeFactorization(PrimeFactorization pf) {
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		size = 0;
		PrimeFactorizationIterator iter = this.iterator();
		PrimeFactorizationIterator iter2 = pf.iterator();
		while (iter2.hasNext()) {
			iter.add(iter2.next());
		}
	}

	/**
	 * Constructs a factorization from an array of prime factors. Useful when
	 * the number is too large to be represented even as a long integer.
	 * 
	 * @param pflist
	 */
	public PrimeFactorization(PrimeFactor[] pfList) {
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		size = 0;
		PrimeFactorizationIterator iter = this.iterator();
		for (int i = 0; i < pfList.length; i++) {
			iter.add(pfList[i]);
		}
	}

	// --------------
	// Primality Test
	// --------------

	/**
	 * Test if a number is a prime or not. Check iteratively from 2 to the
	 * largest integer not exceeding the square root of n to see if it divides
	 * n.
	 * 
	 * @param n
	 * @return true if n is a prime false otherwise
	 */
	public static boolean isPrime(long n) {
		if (n == 2) {
			return true;
		}
		if (n % 2 == 0 || n < 2)
			return false;

		for (long i = 3; i * i <= n; i += 2) {
			if (n % i == 0)
				return false;
		}
		return true;
	}

	// ---------------------------
	// Multiplication and Division
	// ---------------------------

	/**
	 * Multiplies this.value with another number n. You can do this in one loop:
	 * Factor n and traverse the doubly linked list in the same time. For
	 * details refer to Section 3.1 in the project description. Store the prime
	 * factorization of the product. Update value and size.
	 * 
	 * @param n
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public void multiply(long n) throws IllegalArgumentException {
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		multiply(new PrimeFactorization(n));
	}

	/**
	 * Multiplies this.value with another number in the factorization form.
	 * Traverse both linked lists and store the result in this list object. See
	 * Section 3.1 in the project description for details of algorithm.
	 * 
	 * @param pf
	 */
	public void multiply(PrimeFactorization pf) {
		PrimeFactorizationIterator iter2 = pf.iterator();
		while (iter2.hasNext()) {
			add(iter2.cursor.pFactor.prime, iter2.cursor.pFactor.multiplicity);
			iter2.next();
		}
	}

	/**
	 * Divides this.value by n. Make updates to the list, value, size if
	 * divisible. No update otherwise. Refer to Section 3.2 in the project
	 * description for details.
	 * 
	 * @param n
	 * @return true if divisible false if not divisible
	 * @throws IllegalArgumentException
	 *             if n <= 0
	 */
	public boolean dividedBy(long n) throws IllegalArgumentException {
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		if (value != -1 && value < n) {
			return false;
		} else if (value == n) {
			clearList();
			return true;
		} else {
			PrimeFactorization divisor = new PrimeFactorization(n);
			return dividedBy(divisor);
		}
	}

	/**
	 * Division where the divisor is represented in the factorization form.
	 * Update the linked list of this object accordingly by removing those nodes
	 * housing prime factors that disappear after the division. No update if
	 * this number is not divisible by pf. Algorithm details are given in
	 * Section 3.2.
	 * 
	 * @param pf
	 * @return true if divisible by pf false otherwise
	 */
	public boolean dividedBy(PrimeFactorization pf) {
		if ((this.value != -1 && pf.value != -1 && this.value < pf.value) || this.value != -1 && pf.value == 1) {
			return false;
		}

		PrimeFactorization copy = new PrimeFactorization(this);
		PrimeFactorizationIterator iterCopy = copy.iterator();
		PrimeFactorizationIterator iterPf = pf.iterator();

		while (iterCopy.hasNext() && iterPf.hasNext()) {
			PrimeFactor p = iterPf.next();
			PrimeFactor c = iterCopy.next();

			if (c.prime > p.prime) {
				return false;
			}
			if (c.prime == p.prime && c.multiplicity < p.multiplicity) {
				return false;

			}
			if (c.prime == p.prime && c.multiplicity >= p.multiplicity) {
				c.multiplicity -= p.multiplicity;
				if (c.multiplicity == 0) {
					iterCopy.remove();
				}
			}
			if (p.prime > c.prime) {
				iterPf.previous();
			}
		}
		head = copy.head;
		tail = copy.tail;
		updateValue();
		return true;
	}

	// -------------------------------------------------
	// Greatest Common Divisor and Least Common Multiple
	// -------------------------------------------------

	/**
	 * Computes the greatest common divisor (gcd) of this.value and an integer
	 * n, and return the result as a PrimeFactors object. Calls the method
	 * Euclidean() if this.value != OVERFLOW.
	 * 
	 * It is more efficient to factorize the gcd than n, which is often much
	 * greater.
	 * 
	 * @param n
	 * @return prime factorization of gcd
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public PrimeFactorization gcd(long n) throws IllegalArgumentException {
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		if (value != OVERFLOW) {
			return new PrimeFactorization(Euclidean(value, n));
		}
		return new PrimeFactorization(value);
	}

	/**
	 * Implements the Euclidean algorithm to compute the gcd of two natural
	 * numbers m and n. The algorithm is described in Section 4.1 of the project
	 * description.
	 * 
	 * @param m
	 * @param n
	 * @return gcd of m and n.
	 * @throws IllegalArgumentException
	 *             if m < 1 or n < 1
	 */
	public static long Euclidean(long m, long n) throws IllegalArgumentException {
		if ((m < 1) || (n < 1)) {
			throw new IllegalArgumentException();
		}
		while (n != 0) {
			long temp = n;
			n = m % n;
			m = temp;
		}
		return m;
	}

	/**
	 * Computes the gcd of this.value and pf.value by traversing the two lists.
	 * No direct computation involving value and pf.value. Refer to Section 4.2
	 * in the project description on how to proceed.
	 * 
	 * @param pf
	 * @return prime factorization of the gcd
	 */
	public PrimeFactorization gcd(PrimeFactorization pf) {
		PrimeFactorizationIterator iter = this.iterator();
		PrimeFactorizationIterator iterPf = pf.iterator();
		PrimeFactorization gcd = new PrimeFactorization();
		PrimeFactorizationIterator gcdIter = gcd.iterator();

		while (iter.hasNext() && iterPf.hasNext()) {
			PrimeFactor a = iter.next();
			PrimeFactor b = iterPf.next();
			if (a.prime == b.prime) {
				if (a.multiplicity < b.multiplicity) {
					gcdIter.add(new PrimeFactor(a.prime, a.multiplicity));
				} else {
					gcdIter.add(new PrimeFactor(b.prime, b.multiplicity));
				}
			}
			if (a.prime < b.prime) {
				iterPf.previous();
			} else if (b.prime < a.prime) {
				iter.previous();
			}
		}
		updateValue();
		return gcd;
	}

	/**
	 * Computes the least common multiple (lcm) of this.value and the number
	 * represented by pf. The list-based algorithm is given in Section 4.3 in
	 * the project description.
	 * 
	 * @param pf
	 * @return factored least common multiple
	 */
	public PrimeFactorization lcm(PrimeFactorization pf) {
		PrimeFactorizationIterator iter = this.iterator();
		PrimeFactorizationIterator iterPf = pf.iterator();
		PrimeFactorization lcm = new PrimeFactorization();
		PrimeFactorizationIterator lcmIter = lcm.iterator();

		while (iter.hasNext() && iterPf.hasNext()) {
			PrimeFactor a = iter.next();
			PrimeFactor b = iterPf.next();

			if (a.prime != b.prime) {
				if (a.prime < b.prime) {
					lcmIter.add(new PrimeFactor(a.prime, a.multiplicity));
					iterPf.previous();
				} else if (b.prime < a.prime) {
					lcmIter.add(new PrimeFactor(b.prime, b.multiplicity));
					iter.previous();
				}
			} else if (a.prime == b.prime) {
				if (a.multiplicity > b.multiplicity) {
					lcmIter.add(new PrimeFactor(a.prime, a.multiplicity));
				} else if (b.multiplicity > a.multiplicity) {
					lcmIter.add(new PrimeFactor(b.prime, b.multiplicity));
				} else if (a.multiplicity == b.multiplicity) {
					lcmIter.add(new PrimeFactor(a.prime, a.multiplicity));
				}
			}
		}
		updateValue();
		return lcm;
	}

	/**
	 * Computes the least common multiple of this.value and an integer n.
	 * Construct a PrimeFactors object using n and then call the lcm() method
	 * above. Calls the first lcm() method.
	 * 
	 * @param n
	 * @return factored least common multiple
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public PrimeFactorization lcm(long n) throws IllegalArgumentException {
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		return lcm(new PrimeFactorization(value * (n / Euclidean(value, n))));
	}

	// ------------
	// List Methods
	// ------------

	/**
	 * Traverses the list to determine if p is a prime factor.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 * @return true if p is a prime factor of the number represented by this
	 *         linked list false otherwise
	 * @throws IllegalArgumentException
	 *             if p is not a prime
	 */
	public boolean containsPrimeFactor(int p) throws IllegalArgumentException {
		if (!isPrime(p)) {
			throw new IllegalArgumentException("Illegal: Number is not prime");
		}
		for (PrimeFactor pp : this) {
			if (p == pp.prime) {
				return true;
			}
		}
		return false;
	}

	// The next two methods ought to be private but are made public for testing
	// purpose.

	/**
	 * Adds a prime factor p of multiplicity m. Search for p in the linked list.
	 * If p is found at a node N, add m to N.multiplicity. Otherwise, create a
	 * new node to store p and m.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 *            prime
	 * @param m
	 *            multiplicity
	 * @return true if m >= 1 false if m < 1
	 */
	public boolean add(int p, int m) {
		if (m < 1) {
			return false;
		}
		PrimeFactorizationIterator iter = this.iterator();
		if (this.containsPrimeFactor(p)) {
			while (iter.hasNext()) {
				if (iter.cursor.pFactor.prime == p) {
					iter.next().multiplicity += m;
					break;
				}
				iter.next();
			}
			updateValue();
			return true;
		} else {
			while (iter.cursor != tail && iter.cursor.pFactor.prime <= p) {
				iter.next();
			}
			iter.add(new Node(p, m).pFactor);
			updateValue();
			return true;
		}
	}

	/**
	 * Removes m from the multiplicity of a prime p on the linked list. <br>
	 * It starts by searching for p. <br>
	 * Returns false if p is not found, and true if p is found. <br>
	 * In the latter case, let N be the node that stores p. <br>
	 * If N.multiplicity > m, subtracts m from N.multiplicity. <br>
	 * If N.multiplicity <= m, removes the node N.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 * @param m
	 * @return true when p is found. false when p is not found.
	 * @throws IllegalArgumentException
	 *             if m < 1
	 */
	public boolean remove(int p, int m) throws IllegalArgumentException {
		if (m < 1) {
			throw new IllegalArgumentException();
		}
		PrimeFactorizationIterator iter = this.iterator();
		if (this.containsPrimeFactor(p)) {
			while (iter.hasNext()) {
				if (iter.cursor.pFactor.prime == p && iter.cursor.pFactor.multiplicity > m) {
					iter.cursor.pFactor.multiplicity -= m;
					updateValue();
					return true;
				} else if (iter.cursor.pFactor.prime == p && iter.cursor.pFactor.multiplicity <= m) {
					iter.next();
					iter.remove();
					updateValue();
					return true;
				}
				iter.next();
			}
		}
		return false;
	}

	/**
	 * 
	 * @return size of the list
	 */
	public int size() {
		return size;
	}

	/**
	 * Writes out the list as a factorization in the form of a product.
	 * Represents exponentiation by a caret. For example, if the number is 5814,
	 * the returned string would be printed out as "2 * 3^2 * 17 * 19".
	 */
	@Override
	public String toString() {
		if (size == 0) {
			return "1";
		}
		StringBuilder sb = new StringBuilder();
		PrimeFactorizationIterator iter = this.iterator();
		while (iter.hasNext()) {
			if (iter.cursor == this.tail.previous) {
				sb.append(iter.next().toString());
			} else {
				sb.append(iter.next().toString().concat(" * "));
			}
		}
		return sb.toString();
	}

	// The next three methods are for testing, but you may use them as you like.

	/**
	 * @return true if this PrimeFactorization is representing a value that is
	 *         too large to be within long's range. e.g. 999^999. false
	 *         otherwise.
	 */
	public boolean valueOverflow() {
		return value == OVERFLOW;
	}

	/**
	 * @return value represented by this PrimeFactorization, or -1 if
	 *         valueOverflow()
	 */
	public long value() {
		return value;
	}

	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}

	@Override
	public PrimeFactorizationIterator iterator() {
		return new PrimeFactorizationIterator();
	}

	/**
	 * Doubly-linked node type for this class.
	 */
	private class Node {
		public PrimeFactor pFactor; // prime factor
		public Node next;
		public Node previous;

		/**
		 * Default constructor for creating a dummy node.
		 */
		public Node() {
			Node newNode = null;
		}

		/**
		 * Precondition: p is a prime
		 * 
		 * @param p
		 *            prime number
		 * @param m
		 *            multiplicity
		 * @throws IllegalArgumentException
		 *             if m < 1
		 */
		public Node(int p, int m) throws IllegalArgumentException {

			if (m < 1) {
				throw new IllegalArgumentException();
			}

			pFactor = new PrimeFactor(p, m);
		}

		/**
		 * Constructs a node over a provided PrimeFactor object.
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 */
		public Node(PrimeFactor pf) throws IllegalArgumentException {
			pFactor = new PrimeFactor(pf.prime, pf.multiplicity);

		}

		/**
		 * Printed out in the form: prime + "^" + multiplicity. For instance
		 * "2^3".
		 */
		@Override
		public String toString() {
			return pFactor.toString();
		}

	}

	private class PrimeFactorizationIterator implements ListIterator<PrimeFactor> {
		// Class invariants:
		// 1) logical cursor position is always between cursor.previous and
		// cursor
		// 2) after a call to next(), cursor.previous refers to the node just
		// returned
		// 3) after a call to previous() cursor refers to the node just returned
		// 4) index is always the logical index of node pointed to by cursor

		private Node cursor = head;
		private Node pending = null; // node pending for removal
		private int index = 0;

		// other instance variables ...

		/**
		 * Default constructor positions the cursor before the smallest prime
		 * factor.
		 */
		public PrimeFactorizationIterator() {
			cursor = head.next;
		}

		/**
		 * Returns true if this list iterator has more prime factors when
		 * traversing the list in the forward direction. (In other words,
		 * returns true if next() would return a prime factor rather than
		 * throwing an exception.)
		 * 
		 * @return true if the list iterator has more prime factors when
		 *         traversing the list in the forward direction
		 */
		@Override
		public boolean hasNext() {
			return cursor.next != null;
		}

		/**
		 * Returns true if this list iterator has more prime factors when
		 * traversing the list in the reverse direction. (In other words,
		 * returns true if previous() would return a prime factor rather than
		 * throwing an exception.)
		 * 
		 * @return true if the list iterator has more prime factors when
		 *         traversing the list in the reverse direction
		 */
		@Override
		public boolean hasPrevious() {
			return cursor.previous != null;
		}

		/**
		 * Returns the next prime factor in the list and advances the cursor
		 * position. This method may be called repeatedly to iterate through the
		 * list, or intermixed with calls to previous() to go back and forth.
		 * (Note that alternating calls to next and previous will return the
		 * same prime factor repeatedly.)
		 * 
		 * @return the next prime factor in the list
		 * 
		 * @throws NoSuchElementException
		 *             if the iteration has no next prime factor
		 */
		@Override
		public PrimeFactor next() throws NoSuchElementException {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			pending = cursor;
			cursor = cursor.next;
			index++;
			return pending.pFactor;
		}

		/**
		 * Returns the previous prime factor in the list and moves the cursor
		 * position backwards. This method may be called repeatedly to iterate
		 * through the list backwards, or intermixed with calls to next() to go
		 * back and forth. (Note that alternating calls to next and previous
		 * will return the same prime factor repeatedly.)
		 * 
		 * @return the previous prime factor in the list
		 * 
		 * @throws NoSuchElementException
		 *             if the iteration has no previous prime factor
		 */
		@Override
		public PrimeFactor previous() throws NoSuchElementException {
			if (!hasPrevious()) {
				throw new NoSuchElementException();
			}
			pending = cursor = cursor.previous;
			index--;
			return pending.pFactor;
		}

		/**
		 * Replaces the last prime factor returned by next() or previous() with
		 * the specified element (optional operation). This call can be made
		 * only if neither remove() nor add() have been called after the last
		 * call to next or previous.
		 * 
		 * @throws IllegalStateException
		 *             if pending == null
		 */
		@Override
		public void remove() throws IllegalStateException {
			if (pending == null) {
				throw new IllegalStateException();
			}
			Node lastCursor = pending.next;

			unlink(pending);

			if (cursor == pending) {
				cursor = lastCursor;
			} else {
				index--;
			}
			--size;

			pending = null;
			updateValue();
		}

		/**
		 * Adds a prime factor at the cursor position. The cursor is at a wrong
		 * position in either of the two situations below:
		 * 
		 * a) pf.prime < cursor.previous.pFactor.prime if cursor.previous !=
		 * null. b) pf.prime > cursor.pFactor.prime if cursor != null.
		 * 
		 * Take into account the possibility that pf.prime ==
		 * cursor.pFactor.prime.
		 * 
		 * Precondition: pf.prime is a prime.
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 *             if the cursor is at a wrong position.
		 */
		@Override
		public void add(PrimeFactor pf) throws IllegalArgumentException {
			if (cursor.previous != head && cursor.previous.pFactor.prime >= pf.prime)
				throw new IllegalArgumentException("Illegal add: previous prime >= given prime");
			if (cursor != tail && cursor.pFactor.prime <= pf.prime)
				throw new IllegalArgumentException("Illegal add: next prime <= given prime");
			pending = head;

			link(cursor.previous, new Node(pf));
			updateValue();
			size++;
			index++;
		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public int previousIndex() {
			return index - 1;
		}

		@Deprecated
		@Override
		public void set(PrimeFactor pf) {
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support set method");
		}

		// Other methods you may want to add or override that could possibly
		// facilitate
		// other operations, for instance, addition, access to the previous
		// element, etc.
		//
		// ...
		//
	}

	// --------------
	// Helper methods
	// --------------

	/**
	 * Inserts toAdd into the list after current without updating size.
	 * 
	 * Precondition: current != null, toAdd != null
	 */
	private void link(Node current, Node toAdd) {
		toAdd.previous = current;
		toAdd.next = current.next;
		current.next.previous = toAdd;
		current.next = toAdd;
	}

	/**
	 * Removes toRemove from the list without updating size.
	 */
	private void unlink(Node toRemove) {
		toRemove.previous.next = toRemove.next;
		toRemove.next.previous = toRemove.previous;
	}

	/**
	 * Remove all the nodes in the linked list except the two dummy nodes.
	 * 
	 * Made public for testing purpose. Ought to be private otherwise.
	 */
	public void clearList() {
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		size = 0;
		value = 1;
	}

	/**
	 * Multiply the prime factors (with multiplicities) out to obtain the
	 * represented integer. Use Math.multiply(). If an exception is throw,
	 * assign OVERFLOW to the instance variable value. Otherwise, assign the
	 * multiplication result to the variable.
	 * 
	 */
	private void updateValue() {
		try {
			value = 1;
			for (PrimeFactor pp : this) {
				long temp = 1;
				for (int i = 0; i < pp.multiplicity; i++) {
					temp = Math.multiplyExact(temp, pp.prime);

				}
				value = Math.multiplyExact(value, temp);
			}
		}

		catch (

		ArithmeticException e)

		{
			value = OVERFLOW;
		}

	}
}
