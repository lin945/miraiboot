package top.gardel.translate.tokens;

public interface TokenProvider {
    /**
     * Generate and return a token.
     *
     * @param source Source language
     * @param target Target language
     * @param text Text to translate
     * @return the token
     */
    String generateToken(String source, String target, String text);
}
