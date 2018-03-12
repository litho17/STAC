package net.computerpoint.numerical;

import net.computerpoint.parsing.simple.PARSERObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;


/**
 * Rsa implementation using the Chinese Remainder Theorem
 */
public class PrivateCommsPrivateKey {
    private static final int MAX_KEY_LEN = 1024;
    private BigInteger p; // The first Rsa prime.  Part of the private key.
    private BigInteger q; // The second Rsa prime. Part of the private key.
    private BigInteger divisor; // The Rsa modulus.  Part of the public key. M = p*q
    private BigInteger e; // The encryption exponent.  Part of the public key
    private BigInteger d; // The decryption exponent.  Part of the secret key.  d = e^-1 mod(phi(M)) = e^-1 mod ((p-1)(q-1))
    private BigInteger dp; // The decryption exponent in Z/pZ (for use in the CRT).  Part of the private key.
    private BigInteger dq; // The decryption exponent in Z/qZ (for use in the CRT).  Part of the private key.
    private BigInteger qInv; // q^-1 mod p.  Necessary for CRT.
    private BigInteger pMinus1; // p-1, needed for decryption
    private BigInteger qMinus1; // q-1, needed for decryption
    private MontgomeryProductGenerator montP; // For exponentiation mod p
    private MontgomeryProductGenerator montQ; // For exponentiation mod q
    private RsaPublicKey publicKey; // The public key, modulus and e
    
    private MontgomeryProductGenerator montN;
    

    /**
     * This constructor also allows you to choose a non-standard exponent
     */
    public PrivateCommsPrivateKey(BigInteger p, BigInteger q, BigInteger e) {
        this.p = p;
        this.pMinus1 = p.subtract(BigInteger.ONE);
        this.q = q;

        this.qMinus1 = q.subtract(BigInteger.ONE);
        this.divisor = p.multiply(q);
        this.e = e;
        this.d = e.modInverse(pMinus1.multiply(qMinus1));
        this.dp = d.mod(pMinus1);
        this.dq = d.mod(qMinus1);
        this.qInv = q.modInverse(p);
        this.montP = new MontgomeryProductGenerator(p);
        this.montQ = new MontgomeryProductGenerator(q);
        this.publicKey = new RsaPublicKey(divisor, e);
        
        this.montN = new MontgomeryProductGenerator(divisor);
        

        // In order to ensure the side channel is valid, need to ensure key length is limited
        if (divisor.bitLength() > MAX_KEY_LEN) {
            throw new IllegalArgumentException("Large primes not supported");
        }
    }

    /**
     * Constructor that lets you specify your Rsa primes directly as BigIntegers.
     */
    public PrivateCommsPrivateKey(BigInteger p, BigInteger q) {
        this(p, q, BigInteger.valueOf(65537));
    }

    /**
     * @param seed
     * @param divisorSize This is for testing purposes only. One can easily use Python of ssh-keygen to generate good Rsa primes offline
     *                    These will not have important Rsa prime properties (p-1 and q-1 having large prime factors, etc.)
     */
    // TODO: remove this eventually
    public static PrivateCommsPrivateKey formKey(int seed, int divisorSize) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        BigInteger prime1 = BigInteger.probablePrime(divisorSize / 2, random);
        BigInteger prime2 = BigInteger.probablePrime(divisorSize / 2, random);
        return new PrivateCommsPrivateKey(prime1, prime2);
    }

    /**
     * Creates a key using the primes generated using the python script Rsa_gen.py.
     */
    // TODO: May eliminate this if we decide we like the single file better.  
    public static PrivateCommsPrivateKey formKeyFromFiles(String pFileName, String qFileName) throws FileNotFoundException {
        String pString = new Scanner(new File(pFileName)).useDelimiter("\\Z").next();
        String qString = new Scanner(new File(qFileName)).useDelimiter("\\Z").next();
        BigInteger p = stringToBigInt(pString);
        BigInteger q = stringToBigInt(qString);
        return new PrivateCommsPrivateKey(p, q);

    }

    /**
     * Creates key when both primes are in a single file.
     */
    public static PrivateCommsPrivateKey formKeyFromFile(String keyFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(keyFile));
        String pString = scanner.next();
        String qString = scanner.next();
        BigInteger p = stringToBigInt(pString);
        BigInteger q = stringToBigInt(qString);
        return new PrivateCommsPrivateKey(p, q);
    }

    public static PrivateCommsPrivateKey formKeyFromParser(PARSERObject privateKeyParser) {
        BigInteger p = stringToBigInt((String) privateKeyParser.get("p"));
        BigInteger q = stringToBigInt((String) privateKeyParser.get("q"));
        return new PrivateCommsPrivateKey(p, q);
    }

    public PARSERObject toPARSERObject() {
        PARSERObject parser = new PARSERObject();
        parser.put("p", this.p.toString());
        parser.put("q", this.q.toString());
        return parser;
    }

    public String toPARSERString() {
        return toPARSERObject().toPARSERString();
    }

    private static BigInteger stringToBigInt(String str) {
        str = str.trim();
        if (str.endsWith("L")) {
            str = str.substring(0, str.length() - 1);
        }
        return new BigInteger(str);
    }

    // This gets the public key for distribution
    public RsaPublicKey fetchPublicKey() {
        return publicKey;
    }

    public int takeBitSize() {
        // from https://github.com/bcgit/bc-java/blob/master/core/src/main/java/org/bouncycastle/crypto/engines/RsaCoreEngine.java

        int bitSize = divisor.bitLength();
        return (bitSize + 7) / 8 - 1;
    }


    // Fast decryption using the CRT
    // See https://en.wikipedia.org/wiki/Rsa_(cryptosystem)#Using_the_Chinese_remainder_algorithm
    public BigInteger decrypt(BigInteger ciphertext) {
        BigInteger m;
        
        
        m = montN.exponentiate(ciphertext, d);
        
        
        return m;
    }

    /**
     * NOTE: The result of this call may need to be trimmed down to the expected size
     *       using RsaUtil.stripPadding()
     * @param ciphertext
     * @return
     */
    public byte[] decryptBytes(byte[] ciphertext) {
        BigInteger ct = PrivateCommsUtil.toBigInt(ciphertext, takeBitSize());
        BigInteger pt = decrypt(ct);
        return PrivateCommsUtil.fromBigInt(pt, takeBitSize());
    }

    @Override
    public String toString() {
        return "p: " + p.toString() + " q: " + q.toString();
    }
}

