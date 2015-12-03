package edu.iastate.cs228.hw3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * You can use this to test some basic stuff.
 * 
 * @author Yuxiang Zhang
 *
 */
public class _JUnitTest {
	private static final int n = 1000;
	private static final LinkedHashSet<Integer> primes = new LinkedHashSet<Integer>();

	static { // generate primes up to n
		if (primes.size() == 0)
			primes.add(2);
		for (int i = 2; i <= n; i++)
			for (int p : primes)
				if (i % p == 0)
					break;
				else if (p * p > i) {
					primes.add(i);
					break;
				}
	}

	@Test
	public void test_Euclidean() {
		assertEquals(15, PrimeFactorization.Euclidean(30, 45));
		assertEquals(15, PrimeFactorization.Euclidean(45, 30));
		assertEquals(100, PrimeFactorization.Euclidean(200, 300));
		assertEquals(100, PrimeFactorization.Euclidean(300, 200));
		assertEquals(111 * 777, PrimeFactorization.Euclidean(999 * 777, 777 * 555));
		assertEquals(111 * 777, PrimeFactorization.Euclidean(555 * 777, 777 * 999));
	}

	@Test
	public void test_isPrime() {
		for (int i = 0; i < n; i++)
			assertEquals(primes.contains(i), PrimeFactorization.isPrime(i));
	}

	@Test
	public void test_constructor() {
		PrimeFactorization pf = new PrimeFactorization();
		assertEquals("1", pf.toString());

		pf = new PrimeFactorization(1234567890987654321L);
		PrimeFactorization copy = new PrimeFactorization(pf);
		copy.multiply(pf);
		assertEquals("3^4 * 7^2 * 19^2 * 928163^2 * 1111211111^2", copy.toString());

		pf = new PrimeFactorization(
				new PrimeFactor[] { new PrimeFactor(2, 1000), new PrimeFactor(3, 3000), new PrimeFactor(5, 5000) });
		assertEquals("2^1000 * 3^3000 * 5^5000", pf.toString());
	}

	@Test
	public void test_clearList() {
		PrimeFactorization pf = new PrimeFactorization(12345);
		pf.clearList();
		assertEquals(0, pf.size());
		assertEquals(1, pf.value());
	}

	@Test
	public void test_toString() {
		PrimeFactorization pf = new PrimeFactorization();
		pf.multiply(2 * 2 * 3 * 3 * 3 * 5);
		assertEquals("2^2 * 3^3 * 5", pf.toString());
	}

	@Test
	public void test_toArray() {
		PrimeFactorization pf = new PrimeFactorization(1);
		assertArrayEquals(new PrimeFactor[] {}, pf.toArray());

		pf = new PrimeFactorization(45);
		assertArrayEquals(new PrimeFactor[] { new PrimeFactor(3, 2), new PrimeFactor(5, 1) }, pf.toArray());

		pf = new PrimeFactorization(new PrimeFactor[] { new PrimeFactor(2, 1000) });
		pf.dividedBy(2);
		assertArrayEquals(new PrimeFactor[] { new PrimeFactor(2, 999) }, pf.toArray());

		pf = new PrimeFactorization(new PrimeFactor[] { new PrimeFactor(2, 1000), new PrimeFactor(5, 1000) });
		pf.dividedBy(10);
		assertArrayEquals(new PrimeFactor[] { new PrimeFactor(2, 999), new PrimeFactor(5, 999) }, pf.toArray());

		pf = new PrimeFactorization(1234567890987654321L);

		PrimeFactor[] pfs = { new PrimeFactor(3, 2), new PrimeFactor(7, 1), new PrimeFactor(19, 1),
				new PrimeFactor(928163, 1), new PrimeFactor(1111211111, 1) };
		assertArrayEquals(pfs, pf.toArray()); // can fail if PrimeFactor.equals
												// is not implemented
	}

	@Test
	public void test_size() {
		PrimeFactorization pf = new PrimeFactorization();

		pf.multiply(2 * 2 * 3 * 3 * 3 * 5);
		assertEquals(3, pf.size());

		// pf.clearList();
		// assertEquals(0, pf.size());
	}

	@Test
	public void test_multiply() {
		PrimeFactorization pf = new PrimeFactorization();
		for (int p : primes)
			pf.multiply(p);
		pf.multiply(pf);
		assertEquals(primes.size(), pf.size());

		pf.clearList();
		PrimeFactorization pflong = new PrimeFactorization(1234567890987654321L);

		pf.multiply(pflong);
		pf.multiply(pflong);
		assertEquals("3^4 * 7^2 * 19^2 * 928163^2 * 1111211111^2", pf.toString());

		pf = new PrimeFactorization();
		assertEquals(1, pf.value());

		pf = new PrimeFactorization(1234567890987654321L);
		assertEquals(1234567890987654321L, pf.value());

		pf.multiply(pf);
		assertEquals(-1, pf.value());
		assertTrue(pf.valueOverflow());

		pf.dividedBy(pf);
		assertEquals(1, pf.value());
		assertFalse(pf.valueOverflow());

		// extreme number tests
		pf.multiply(new PrimeFactorization(new PrimeFactor[] { new PrimeFactor(2, 33333) }));
		assertTrue(pf.valueOverflow());
		assertEquals("2^33333", pf.toString());

		PrimeFactorization copy = new PrimeFactorization(pf);
		pf.multiply(copy);
		assertTrue(pf.valueOverflow());
		assertEquals("2^66666", pf.toString());

		pf.multiply(copy);
		assertTrue(pf.valueOverflow());
		assertEquals("2^99999", pf.toString());

		pf.multiply(2);
		assertTrue(pf.valueOverflow());
		assertEquals("2^100000", pf.toString());

		pf.multiply(new PrimeFactorization(new PrimeFactor[] { new PrimeFactor(5, 99999) }));
		pf.multiply(5);
		assertTrue(pf.valueOverflow());
		assertEquals("2^100000 * 5^100000", pf.toString());

		pf.multiply(new PrimeFactorization(new PrimeFactor[] { new PrimeFactor(3, 99999) }));
		pf.multiply(3);
		assertTrue(pf.valueOverflow());
		assertEquals("2^100000 * 3^100000 * 5^100000", pf.toString());
	}

	@Test
	public void test_dividedBy() {
		PrimeFactorization pf = new PrimeFactorization();
		for (int p : primes)
			pf.multiply(p);
		for (int p : primes)
			pf.dividedBy(p);
		assertEquals(0, pf.size());

		PrimeFactorization pflong = new PrimeFactorization(1234567890987654321L);

		pf.multiply(pflong);
		pf.multiply(pflong);
		pf.multiply(pflong);
		pf.dividedBy(pflong);
		assertEquals("3^4 * 7^2 * 19^2 * 928163^2 * 1111211111^2", pf.toString());

		pf.dividedBy(2); // unsuccessful
		assertEquals("3^4 * 7^2 * 19^2 * 928163^2 * 1111211111^2", pf.toString());

		pf.dividedBy(18); // unsuccessful
		assertEquals("3^4 * 7^2 * 19^2 * 928163^2 * 1111211111^2", pf.toString());

		// extreme number tests
		pf.clearList();
		pf.add(2, 100000);
		pf.add(5, 100000);
		pf.add(3, 100000);
		assertTrue(pf.valueOverflow());
		assertEquals("2^100000 * 3^100000 * 5^100000", pf.toString());

		pf.dividedBy(2);
		pf.dividedBy(5);
		pf.dividedBy(3);
		assertTrue(pf.valueOverflow());
		assertEquals("2^99999 * 3^99999 * 5^99999", pf.toString());

		pf.dividedBy(pf);
		assertFalse(pf.valueOverflow());
		assertEquals(1, pf.value());
	}

	@Test
	public void test_gcd() {
		PrimeFactorization a = new PrimeFactorization(1234567890987654321L), b = new PrimeFactorization(a);
		a.multiply(b);
		assertEquals("3^2 * 7 * 19 * 928163 * 1111211111", a.gcd(b).toString());

		a.multiply(5);
		b.multiply(17);
		assertEquals("3^2 * 7 * 19 * 928163 * 1111211111", a.gcd(b).toString());

		a.multiply(32);
		b.multiply(32);
		assertEquals("2^5 * 3^2 * 7 * 19 * 928163 * 1111211111", a.gcd(b).toString());

		a.dividedBy(1111211111);
		a.dividedBy(1111211111);
		assertEquals("2^5 * 3^2 * 7 * 19 * 928163", a.gcd(b).toString());

		b.dividedBy(32);
		b.dividedBy(19);
		assertEquals("3^2 * 7 * 928163", a.gcd(b).toString());
	}

	@Test
	public void test_lcm() {
		PrimeFactorization a = new PrimeFactorization(1234567890987654321L), b = new PrimeFactorization(a);
		a.multiply(b);
		assertEquals("3^4 * 7^2 * 19^2 * 928163^2 * 1111211111^2", a.lcm(b).toString());

		a.multiply(5);
		b.multiply(17);
		assertEquals("3^4 * 5 * 7^2 * 17 * 19^2 * 928163^2 * 1111211111^2", a.lcm(b).toString());

		a.multiply(96);
		b.multiply(96);
		assertEquals("2^5 * 3^5 * 5 * 7^2 * 17 * 19^2 * 928163^2 * 1111211111^2", a.lcm(b).toString());

		a.dividedBy(32);
		b.dividedBy(27);
		assertEquals("2^5 * 3^5 * 5 * 7^2 * 17 * 19^2 * 928163^2 * 1111211111^2", a.lcm(b).toString());
	}

	@Test(expected = NoSuchElementException.class)
	public void test_next() {
		PrimeFactorization pf = new PrimeFactorization();
		for (int p : primes)
			pf.multiply(p);
		ListIterator<PrimeFactor> iter = pf.iterator();
		assertEquals(0, iter.nextIndex());

		iter.next();
		assertEquals(1, iter.nextIndex());

		iter.next();
		assertEquals(2, iter.nextIndex());

		iter.remove();
		assertEquals(1, iter.nextIndex());

		// make sure our iterator has the same behavior as list iterator
		ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));

		ListIterator<Integer> listIter = list.listIterator(1);

		iter.next();
		listIter.next();
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		iter.next();
		listIter.next();
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		iter.previous();
		listIter.previous();
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		iter.previous();
		listIter.previous();
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		iter.remove();
		listIter.remove();
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		iter.add(new PrimeFactor(5, 1));
		listIter.add(2);
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		iter.previous();
		listIter.previous();
		assertEquals(listIter.nextIndex(), iter.nextIndex());

		// expected = NoSuchElementException
		while (iter.hasNext())
			iter.next();
		iter.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void test_previous() {
		PrimeFactorization pf = new PrimeFactorization();
		for (int p : primes)
			pf.multiply(p);
		ListIterator<PrimeFactor> iter = pf.iterator();
		assertEquals(-1, iter.previousIndex());

		iter.next();
		assertEquals(0, iter.previousIndex());

		iter.next();
		assertEquals(1, iter.previousIndex());

		iter.previous();
		assertEquals(0, iter.previousIndex());

		iter.remove();
		assertEquals(0, iter.previousIndex());

		// make sure our iterator has the same behavior as list iterator
		ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));

		ListIterator<Integer> listIter = list.listIterator(1);

		iter.next();
		listIter.next();
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		iter.next();
		listIter.next();
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		iter.previous();
		listIter.previous();
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		iter.previous();
		listIter.previous();
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		iter.remove();
		listIter.remove();
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		iter.add(new PrimeFactor(5, 1));
		listIter.add(2);
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		iter.previous();
		listIter.previous();
		assertEquals(listIter.previousIndex(), iter.previousIndex());

		// expected = NoSuchElementException
		while (iter.hasPrevious())
			iter.previous();
		iter.previous();
	}

	@Test
	public void test_add() {
		PrimeFactorization pf = new PrimeFactorization(2 * 2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 5);
		pf.add(2, 5);
		assertEquals("2^10 * 3^3 * 5^2", pf.toString());

		pf.dividedBy(32);
		assertEquals("2^5 * 3^3 * 5^2", pf.toString());

		pf.add(7, 2);
		assertEquals("2^5 * 3^3 * 5^2 * 7^2", pf.toString());

		pf.add(19, 5);
		assertEquals("2^5 * 3^3 * 5^2 * 7^2 * 19^5", pf.toString());

		pf.add(11, 5);
		assertEquals("2^5 * 3^3 * 5^2 * 7^2 * 11^5 * 19^5", pf.toString());

		pf.dividedBy(800);
		assertEquals("3^3 * 7^2 * 11^5 * 19^5", pf.toString());

		pf.add(2, 5);
		assertEquals("2^5 * 3^3 * 7^2 * 11^5 * 19^5", pf.toString());

		pf.clearList();
		assertEquals("1", pf.toString());

		ListIterator<PrimeFactor> iter = pf.iterator();
		iter.add(new PrimeFactor(2, 5));
		assertEquals(32, pf.value());

		iter.add(new PrimeFactor(5, 1));
		assertEquals(160, pf.value());

		pf.clearList();
		assertEquals(1, pf.value());

		pf.add(2, 1);
		assertEquals(2, pf.value());

		pf.add(3, 1);
		assertEquals(6, pf.value());

		pf.add(2, 1);
		assertEquals(12, pf.value());

		pf.add(3, 1);
		assertEquals(36, pf.value());
	}

	@Test
	public void test_remove() {
		PrimeFactorization pf = new PrimeFactorization(2 * 2 * 2 * 2 * 2 * 3 * 3 * 3 * 5 * 5);
		pf.multiply(32);
		assertEquals("2^10 * 3^3 * 5^2", pf.toString());

		pf.remove(2, 5);
		assertEquals("2^5 * 3^3 * 5^2", pf.toString());

		pf.add(7, 2);
		assertEquals("2^5 * 3^3 * 5^2 * 7^2", pf.toString());

		pf.add(19, 5);
		assertEquals("2^5 * 3^3 * 5^2 * 7^2 * 19^5", pf.toString());

		pf.add(11, 5);
		assertEquals("2^5 * 3^3 * 5^2 * 7^2 * 11^5 * 19^5", pf.toString());

		pf.remove(2, 4);
		assertEquals("2 * 3^3 * 5^2 * 7^2 * 11^5 * 19^5", pf.toString());

		pf.remove(2, 1);
		assertEquals("3^3 * 5^2 * 7^2 * 11^5 * 19^5", pf.toString());

		pf.remove(11, 5);
		assertEquals("3^3 * 5^2 * 7^2 * 19^5", pf.toString());

		pf.remove(3, 5);
		assertEquals("5^2 * 7^2 * 19^5", pf.toString());

		pf.remove(3, 2);
		assertEquals("5^2 * 7^2 * 19^5", pf.toString());

		pf.remove(19, 5);
		assertEquals("5^2 * 7^2", pf.toString());

		ListIterator<PrimeFactor> iter = pf.iterator();
		iter.next();
		iter.remove();
		assertEquals(49, pf.value());

		iter.next();
		iter.remove();
		assertEquals(1, pf.value());

		pf.add(2, 2);
		pf.add(3, 2);
		assertEquals(36, pf.value());

		pf.remove(2, 1);
		assertEquals(18, pf.value());

		pf.remove(2, 1);
		assertEquals(9, pf.value());

		pf.remove(3, 1);
		assertEquals(3, pf.value());

		pf.remove(3, 1);
		assertEquals(1, pf.value());
	}

}