package edu.computerapex.math;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by thennen on 2/9/16.
 */
public class EncryptionUtil {

    public static BigInteger toBigInt(byte[] data, int bitSize) {
        // from https://github.com/bcgit/bc-java/blob/master/core/src/main/java/org/bouncycastle/crypto/engines/RSACoreEngine.java
        if (data.length > bitSize + 1) {
            return toBigIntWorker(data, bitSize);
        }

        return new BigInteger(1, data);
    }

    private static BigInteger toBigIntWorker(byte[] data, int bitSize) {
        throw new IllegalArgumentException("data length " + data.length + " is too long for bitsize " + bitSize);
    }


    public static BigInteger toBigInt(byte[] data) {
        return new BigInteger(1, data);
    }

    public static byte[] fromBigInt(BigInteger dataInt, int bitSize) {
        // from https://github.com/bcgit/bc-java/blob/master/core/src/main/java/org/bouncycastle/crypto/engines/RSACoreEngine.java
        byte[] result = dataInt.toByteArray();

        if (result[0] == 0 && result.length > bitSize) {
            // got an extra byte, get rid of it
            return fromBigIntEntity(result);
        }

        if (result.length < bitSize) {
            // less data than usual, pad
            byte[] tmp = new byte[bitSize];
            System.arraycopy(result, 0, tmp, tmp.length - result.length, result.length);
            return tmp;
        }
        return result;
    }

    private static byte[] fromBigIntEntity(byte[] result) {
        byte[] tmp = new byte[result.length - 1];
        System.arraycopy(result, 1, tmp, 0, tmp.length);
        return tmp;
    }

    /**
     * encryption may pad the result to a multiple of the bitsize, this will trim it back down
     * @param ptBytes padded bytes
     * @param length expected length
     * @return stripped bytes
     */
    public static byte[] stripPadding(byte[] ptBytes, int length) {
        int diff = ptBytes.length - length;
        return Arrays.copyOfRange(ptBytes, diff, ptBytes.length);
    }


    public static byte[] decrypt(byte[] data, EncryptionPrivateKey privateKey, int trimToSize) {
        return stripPadding(privateKey.decryptBytes(data), trimToSize);
    }

    public static byte[] sign(byte[] data, EncryptionPrivateKey privateKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            byte[] sig = privateKey.decryptBytes(hash);

            return sig;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates the signature on data
     * @param data the data to validate
     * @param sig the signature on the data
     * @param publicKey the public key to use for validation
     * @return true if the signature matches the data
     */
    public static boolean verifySig(byte[] data, byte[] sig, EncryptionPublicKey publicKey) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] expectedHash = digest.digest(data);
            byte[] providedHash = EncryptionUtil.stripPadding(publicKey.encryptBytes(sig), expectedHash.length);
            return Arrays.equals(expectedHash, providedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
