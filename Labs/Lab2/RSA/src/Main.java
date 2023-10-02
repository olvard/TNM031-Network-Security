import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.math.BigInteger;

import static java.math.BigInteger.ONE;

public class Main {



    public static void main(String[] args) throws IOException {

        BigInteger p,n,e,d,q;

        //Read input
        System.out.println("Enter your message: " + "\n");
        String input = (new BufferedReader(new InputStreamReader(System.in))).readLine();

        //Bob chooses secret primes p and q and computes n = pq
        p = primeGenerator(1024);
        q = primeGenerator(1024);
        n = p.multiply(q);

        //Bob chooses e with gcd(e,(p-1)(q-1))=1
        BigInteger lambda = (p.subtract(ONE).multiply(q.subtract(ONE))); //(p-1)(q-1)
        e = find_e(lambda);

        //Bob computes d with de = 1(mod(p-1)(q-1))
        d = e.modInverse(lambda);

        //Convert message to bytes
        BigInteger m = new BigInteger(input.getBytes());

        //Encrypt m to c
        BigInteger c = m.modPow(e,n);

        //Print variables
        System.out.println("p: " + p + "\n" + "q: " + q + "\n" + "n: " + n + "\n" +
                           "e: " + e + "\n" + "d: " + d + "\n" + "message in bytes: " + m + "\n");

        //Print encrypted message
        System.out.println("Encrypted message: " + c);

        //Decrypt m
        m = c.modPow(d,n);

        //Convert back to string
        String decrypted_m = new String(m.toByteArray());

        //Print decrypted message
        System.out.println("Decrypted message: " + decrypted_m);

    }

    public static BigInteger primeGenerator(int bitLength){
        Random randomInt = new Random();
        return BigInteger.probablePrime(bitLength,randomInt);
    }

    public static BigInteger find_e(BigInteger lambda){
        BigInteger e;
        Random randomInt = new Random();

        //Find a new e as long as the criterion for e are not fulfilled
        do {
            e = BigInteger.probablePrime(512, randomInt);
        }while(e.compareTo(ONE) <= 0 || e.compareTo(lambda) >= 0 || !e.gcd(lambda).equals(BigInteger.ONE)); //e > (p-1)(q-1), e < 1, common divisor is not 1

        return e;
    }


}
