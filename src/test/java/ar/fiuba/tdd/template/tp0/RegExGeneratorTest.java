package ar.fiuba.tdd.template.tp0;

import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class RegExGeneratorTest {

    private boolean validate(String regEx, int numberOfResults) {
        int maxLenght = 5;
        RegExGenerator generator = new RegExGenerator(maxLenght);
        List<String> results = generator.generate(regEx, numberOfResults);

        // force matching the beginning and the end of the strings
        Pattern pattern = Pattern.compile("^" + regEx + "$");
        return results
                .stream()
                .reduce(true,
                        (acc, item) -> {
                            Matcher matcher = pattern.matcher(item);
                            return acc && matcher.find();
                        },
                        (item1, item2) -> item1 && item2);
    }

    @Test
    public void testAnyCharacter() {
        assertTrue(validate(".", 5));
    }

    @Test
    public void testMultipleCharacters() {
        assertTrue(validate("...", 5));
    }

    @Test
    public void testLiteral() {
        assertTrue(validate("\\@", 1));
    }

    @Test
    public void testLiteralDotCharacter() {
        assertTrue(validate("\\@..", 5));
    }

    @Test
    public void testZeroOrOneCharacter() {
        assertTrue(validate("\\@.h?", 5));
    }

    @Test
    public void testCharacterSet() {
        assertTrue(validate("[abc]", 1));
    }

    @Test
    public void testCharacterSetWithQuantifiers() {
        assertTrue(validate("[abc]+", 1));
    }

    @Test
    public void testPoints() {
        assertTrue(validate(".....", 20));
    }

    @Test
    public void testLetras() {
        assertTrue(validate("abcdefghijk", 1));
    }

    @Test
    public void testTwoGroups() {
        assertTrue(validate("[asdfg]AAA[qwerty]", 3));
    }

    @Test
    public void testAllQuantifiers() {
        assertTrue(validate("a+b*c?", 5));
    }

    @Test
    public void testVarios1() {
        assertTrue(validate(".+c+a+i+[qwerty]*v+b+[zxc]*,", 3));
    }

    @Test
    public void testVarios2() {
        assertTrue(validate("a*b*c*d+e+f+g?h?i?[jkl]*", 3));
    }

}
