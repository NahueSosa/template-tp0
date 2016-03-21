package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RegExGenerator {

    public static final char QUANT_ASTERISK = '*';
    public static final char QUANT_PLUS = '+';
    public static final char QUANT_QUEST = '?';
    public static final char QUANT_ONE = '1';

    public static final char ANY_CHAR = '.';

    public static final char BACKSLASH = '\\';

    public static final char OPEN_BRACKET = '[';
    public static final char CLOSE_BRACKET = ']';

    private int maxLength;

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
    }

    public List<String> generate(String regEx, int numberOfResults) {
        List<Field> fields = parseRegEx(regEx);

        List<String> results = new ArrayList<>();

        while (results.size() < numberOfResults) {
            String result = generateResult(fields);
            if (!results.contains(result) && !containsInvalidChars(result)) {
                results.add(result);
            }
        }
        return results;
    }

    private boolean containsInvalidChars(String result) {
        return (result.contains("\u0085") || result.contains("\n"));
    }

    private String generateResult(List<Field> fields) {
        StringBuilder partialResult = new StringBuilder();

        for (Field field : fields) {
            int times;
            times = getTimesFromQuantifier(field.quant);
            int count = 0;
            while (count < times) {
                switch (field.chars.charAt(0)) {
                    case BACKSLASH:
                        partialResult.append(field.chars.substring(1, 2));
                        break;
                    case OPEN_BRACKET:
                        partialResult.append(getRandomCharFromSet(field.chars.substring(1, field.chars.length() - 1)));
                        break;
                    case ANY_CHAR:
                        partialResult.append(getRandomCharacter());
                        break;
                    default:
                        partialResult.append(field.chars.substring(0, 1));
                        break;
                }
                count++;
            }
        }
        return partialResult.toString();
    }

    private int getTimesFromQuantifier(int quant) {
        int times;
        switch (quant) {
            case QUANT_ASTERISK:
                times = getRandomIntBetween(0, maxLength);
                break;
            case QUANT_PLUS:
                times = getRandomIntBetween(1, maxLength);
                break;
            case QUANT_QUEST:
                times = getRandomIntBetween(0, 1);
                break;
            case QUANT_ONE:
            default:
                times = 1;
                break;
        }
        return times;
    }

    private List<Field> parseRegEx(String regEx) {
        ArrayList<Field> fields = new ArrayList<>();
        int readChar = 0;
        while (readChar < regEx.length()) {
            char currChar = regEx.charAt(readChar);
            switch (currChar) {
                case BACKSLASH:
                    readChar = getReadCharBackSlash(regEx, fields, readChar);
                    break;
                case OPEN_BRACKET:
                    readChar = getReadCharOpenBracket(regEx, fields, readChar);
                    break;
                default:
                    readChar = getReadCharDefault(regEx, fields, readChar);
                    break;
            }
        }
        return fields;
    }

    private int getReadCharBackSlash(String regEx, ArrayList<Field> fields, int readChar) {
        Field fieldToAddBS = getFieldFromBackSlash(regEx.substring(readChar));
        readChar += getAddFromBackSlash(fieldToAddBS.quant);
        fields.add(fieldToAddBS);
        return readChar;
    }

    private int getReadCharOpenBracket(String regEx, ArrayList<Field> fields, int readChar) {
        Field fieldToAddOB = getFieldFromBrackets(regEx.substring(readChar));
        readChar += getAddFromBrackets(fieldToAddOB);
        fields.add(fieldToAddOB);
        return readChar;
    }

    private int getReadCharDefault(String regEx, ArrayList<Field> fields, int readChar) {
        Field fieldToAddDef = getFieldFromChar(regEx.substring(readChar));
        readChar += getAddFromDefault(fieldToAddDef.quant);
        fields.add(fieldToAddDef);
        return readChar;
    }

    private int getAddFromDefault(int quant) {
        if (QUANT_ONE == quant) {
            return 1;
        } else {
            return 2;
        }
    }

    private int getAddFromBrackets(Field fieldToAdd) {
        if (QUANT_ONE == fieldToAdd.quant) {
            return fieldToAdd.chars.length();
        } else {
            return fieldToAdd.chars.length() + 1;
        }
    }

    private int getAddFromBackSlash(int quant) {
        if (QUANT_ONE == quant) {
            return 2;
        } else {
            return 3;
        }
    }

    private Field getFieldFromBrackets(String subRegEx) {
        int index = subRegEx.indexOf(CLOSE_BRACKET);

        Field field = getFieldFromChar(subRegEx.substring(index));
        field.chars = subRegEx.substring(0, index + 1);

        return field;
    }

    private Field getFieldFromBackSlash(String subRegEx) {
        Field field = getFieldFromChar(subRegEx.substring(1));

        field.chars = BACKSLASH + field.chars;

        return field;
    }

    private Field getFieldFromChar(String subRegEx) {
        if (subRegEx.length() == 1) {
            return new Field(subRegEx, QUANT_ONE);
        }

        switch (subRegEx.charAt(1)) {
            case QUANT_ASTERISK:
                return new Field(subRegEx.substring(0, 1), QUANT_ASTERISK);
            case QUANT_PLUS:
                return new Field(subRegEx.substring(0, 1), QUANT_PLUS);
            case QUANT_QUEST:
                return new Field(subRegEx.substring(0, 1), QUANT_QUEST);
            default:
                return new Field(subRegEx, QUANT_ONE);
        }
    }

    private String getRandomCharFromSet(String charset) {
        char[] chars = charset.toCharArray();
        int length = chars.length;
        int randomInt = getRandomIntBetween(0, length - 1);

        return String.valueOf(chars[randomInt]);
    }

    private String getRandomCharacter() {
        char character = (char) getRandomIntBetween(0, 255);
        return String.valueOf(character);
    }

    private int getRandomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private class Field {
        public String chars;
        public char quant;

        public Field(String chars, char quant) {
            this.chars = chars;
            this.quant = quant;
        }
    }

}