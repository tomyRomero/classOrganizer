package com.example.classorganizer;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Turns a password into a value that is safe to store, and checks a password against it.
 *
 * PBKDF2 ships with the JDK, so no extra library is needed. It is salted (a random value per
 * password, so two equal passwords never share a hash) and slow on purpose (600,000 rounds, the
 * OWASP figure for PBKDF2 with SHA-256), so a stolen table is expensive to guess through.
 * Stored as pbkdf2-sha256$rounds$salt$hash, so the rounds can rise later without breaking old rows.
 */
final class Passwords {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2-sha256";
    private static final int ROUNDS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Checked when a username is unknown, so a wrong username takes as long as a wrong password. */
    private static final String NOBODY = hash("no account has this password");

    private Passwords() {
    }

    static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        Base64.Encoder base64 = Base64.getEncoder();
        return PREFIX + "$" + ROUNDS + "$" + base64.encodeToString(salt) + "$"
                + base64.encodeToString(pbkdf2(password, salt, ROUNDS));
    }

    static boolean matches(String password, String stored) {
        String[] parts = stored == null ? new String[0] : stored.split("\\$");
        if (parts.length != 4 || !parts[0].equals(PREFIX)) {
            return false;
        }
        try {
            int rounds = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            // isEqual takes the same time however many bytes match, so timing gives nothing away.
            return rounds > 0 && MessageDigest.isEqual(expected, pbkdf2(password, salt, rounds));
        } catch (IllegalArgumentException damaged) {
            return false; // a stored value we did not write (bad number, bad Base64, empty salt)
        }
    }

    /** Does the same work as a real check and always fails. Used when the username does not exist. */
    static boolean matchesNobody(String password) {
        matches(password, NOBODY);
        return false;
    }

    private static byte[] pbkdf2(String password, byte[] salt, int rounds) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, rounds, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(ALGORITHM + " is part of every Java 17 runtime", e);
        } finally {
            spec.clearPassword();
        }
    }
}
