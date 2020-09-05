package top.gardel.translate.tokens;

import java.util.ArrayList;

public class GoogleTokenGenerator implements TokenProvider {

    private final long[] TKK = {406398, 561666268L + 1526272306L};

    @Override
    public String generateToken(String source, String target, String text) {
        return this.TL(text);
    }

    /**
     * Generate a valid Google Translate request token.
     *
     * @param a text to translate
     * @return string
     */
    private String TL(String a) {
        long[] tkk = this.TKK;
        long b = tkk[0];

        ArrayList<Integer> d = new ArrayList<>();
        for (int f = 0; f < a.length(); f++) {
            int g = a.codePointAt(f);
            if (128 > g) {
                d.add(g);
            } else {
                if (2048 > g) {
                    d.add(g >> 6 | 192);
                } else {
                    if (55296 == (g & 64512) && f + 1 < a.length() && 56320 == (a.codePointAt(f + 1) & 64512)) {
                        g = 65536 + ((g & 1023) << 10) + (a.codePointAt(++f) & 1023);
                        d.add(g >> 18 | 240);
                        d.add(g >> 12 & 63 | 128);
                    } else {
                        d.add(g >> 12 | 224);
                    }
                    d.add(g >> 6 & 63 | 128);
                }
                d.add(g & 63 | 128);
            }
        }
        long aa = b;
        for (Integer integer : d) {
            aa += integer;
            aa = this.RL(aa, "+-a^+6");
        }
        aa = this.RL(aa, "+-3^+b+-f");
        aa ^= tkk[1];
        if (0 > aa) {
            aa = (aa & 2147483647) + 2147483648L;
        }
        aa = (long) (aa % Math.pow(10, 6));

        return aa + "." + (aa ^ b);
    }

    /**
     * Process token data by applying multiple operations.
     * (Params are safe, no need for multibyte functions)
     */
    private long RL(long a, String b)
    {
        for (int c = 0; c < b.length() - 2; c += 3) {
            long d = b.codePointAt(c + 2);
            d = 'a' <= d ? d - 87 : Character.getNumericValue(Math.toIntExact(d));
            d = '+' == b.charAt(c + 1) ? a >>> d : a << d;
            a = '+' == b.charAt(c) ? (a + d & 4294967295L) : a ^ d;
        }

        return a;
    }

}
