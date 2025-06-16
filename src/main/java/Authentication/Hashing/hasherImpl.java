package Authentication.Hashing;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hasherImpl implements hasher {
    final MessageDigest md5;
    String value;

    public hasherImpl(String value) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.value = value;

        this.md5 = MessageDigest.getInstance("MD5");

    }

    @Override
    public String generateHash() {
        md5.reset();
        md5.update(this.value.getBytes(StandardCharsets.UTF_8));
        final byte[] resultByte = md5.digest();
        final String hashString = new String(Hex.encodeHex(resultByte));

        return hashString;
    }

    @Override
    public String decrypt() {
        return null;
    }
}
