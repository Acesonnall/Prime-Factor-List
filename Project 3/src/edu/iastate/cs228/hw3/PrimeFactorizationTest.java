package edu.iastate.cs228.hw3;

public class PrimeFactorizationTest {

	public static void main(String[] args) {
		PrimeFactor[] pfs = new PrimeFactor[] { new PrimeFactor(2, 1), new PrimeFactor(3, 3), new PrimeFactor(5, 5) };

		PrimeFactorization pf = new PrimeFactorization();
		System.out.println("Value of prime factorization: " + pf.value());
		System.out.println("List size: " + pf.size());
		System.out.println("The prime factorization is: " + pf.toString());

		PrimeFactorization pf2 = new PrimeFactorization(25480);
		System.out.println(pf2.containsPrimeFactor(5));
		System.out.println("Value of prime factorization: " + pf2.value());
		System.out.println("List size: " + pf2.size());
		System.out.println("The prime factorization is: " + pf2.toString());

		PrimeFactorization pf3 = new PrimeFactorization(pf2);
		System.out.println("Value of prime factorization: " + pf3.value());
		System.out.println("List size: " + pf3.size());
		System.out.println("The prime factorization is: " + pf3.toString());

		PrimeFactorization pf4 = new PrimeFactorization(pfs);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.add(2, 4);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.add(7, 6);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.add(11, 10);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.remove(11, 5);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.remove(11, 5);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.remove(2, 5);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.clearList();
		pf4.add(3, 3);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf4.remove(3, 3);
		System.out.println("Value of prime factorization: " + pf4.value());
		System.out.println("List size: " + pf4.size());
		System.out.println("The prime factorization is: " + pf4.toString());

		pf3.multiply(1810);
		System.out.println("Value of prime factorization: " + pf3.value());
		System.out.println("List size: " + pf3.size());
		System.out.println("The prime factorization is: " + pf3.toString());

		PrimeFactorization pf5 = new PrimeFactorization(pfs);
		pf3.multiply(pf5);
		System.out.println("Value of prime factorization: " + pf3.value());
		System.out.println("List size: " + pf3.size());
		System.out.println("The prime factorization is: " + pf3.toString());

		PrimeFactorization pf6 = new PrimeFactorization(25480);
		pf6.dividedBy(98);
		System.out.println("Value of prime factorization: " + pf6.value());
		System.out.println("List size: " + pf6.size());
		System.out.println("The prime factorization is: " + pf6.toString());

		System.out.println(Long.MAX_VALUE);
	}

}
