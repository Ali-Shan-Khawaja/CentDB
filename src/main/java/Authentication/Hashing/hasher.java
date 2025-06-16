package Authentication.Hashing;

public interface hasher {
    String generateHash();

    String decrypt();
}
