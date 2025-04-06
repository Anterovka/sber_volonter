package sbp.com.sbt.dataspace.feather.stringexpressions;

import java.util.Arrays;
import java.util.List;

/**
 * Token Parser
 */
class TokenParser {

    static final List<Character> OFFSET_PREFIX_CHARACTERS = Arrays.asList('-', '+');

    String string;
    int position;
    int lastTokenPosition;
    int datePosition = -1;
    int dateTimePosition = -1;

    /**
     * @param string String
     */
    TokenParser(String string) {
        this.string = string;
    }

    /**
     * Is it a digit?
     *
     * @param character Символ
     */
    boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

    /**
     * Is it an identifier symbol?
     *
     * @param character Символ
     */
    boolean isIdentifierCharacter(char character) {
        return isDigit(character)
            || (character >= 'a' && character <= 'z')
            || (character >= 'A' && character <= 'Z')
            || character == '_';
    }

    /**
     * Parse digits
     *
     * @param amount Количество
     */
    void parseDigits(int count) {
        for (int i = 0; i < count; ++i) {
            checkPosition();
            if (!isDigit(string.charAt(position))) {
                throw new DigitExpectedException(position, string.charAt(position));
            }
            ++position;
        }
    }

    /**
     * Parse the symbol
     *
     * @param character Символ
     */
    void parseCharacter(char character) {
        checkPosition();
        if (string.charAt(position) != character) {
            throw new UnexpectedCharacterException(position, character, string.charAt(position));
        }
        ++position;
    }

    /**
     * Skip spaces
     */
    void skipSpaces() {
        while (position < string.length()) {
            char character = string.charAt(position);
            if (character == ' ' || character == '\t' || character == '\n' || character == '\r' || character == '\f') {
                ++position;
            } else {
                return;
            }
        }
    }

    /**
     * Check position
     */
    void checkPosition() {
        if (position == string.length()) {
            throw new UnexpectedEndOfStringException();
        }
    }

    /**
     * Parse a simple token
     *
     * @param value The value
     * @return Was the token parsed
     */
    boolean parseSimpleToken(String value) {
        if (position + value.length() > string.length()) {
            return false;
        }
        int result = position;
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) != string.charAt(result)) {
                return false;
            }
            ++result;
        }
        position = result;
        return true;
    }

    /**
     * Parse a string
     *
     * @return Was the string parsed
     */
    boolean parseString() {
        if (string.charAt(position) != '\'') {
            return false;
        }
        do {
            do {
                ++position;
                checkPosition();
            } while (string.charAt(position) != '\'');
            ++position;
        } while (position < string.length() && string.charAt(position) == '\'');
        return true;
    }

    /**
     * Parse a number
     *
     * @return Was the number parsed
     */
    boolean parseNumber() {
        int result = position;
        if (string.charAt(result) == '-') {
            ++result;
        }
        if (isDigit(string.charAt(result))) {
            do {
                ++result;
            } while (result < string.length()
                    && isDigit(string.charAt(result)));
            if (result + 1 < string.length()
                    && string.charAt(result) == '.'
                    && isDigit(string.charAt(result + 1))) {
                ++result;
                do {
                    ++result;
                } while (result < string.length()
                        && isDigit(string.charAt(result)));
            }
            if (result + 1 < string.length() && string.charAt(result) == 'E') {
                if ((string.charAt(result + 1) == '-' || string.charAt(result + 1) == '+')
                        && result + 2 < string.length()
                        && isDigit(string.charAt(result + 2))) {
                    result += 3;
                } else if (isDigit(string.charAt(result + 1))) {
                    result += 2;
                } else {
                    return false;
                }
                while (result < string.length() && isDigit(string.charAt(result))) {
                    ++result;
                }
            }
            position = result;
            return true;
        }
        return false;
    }

    /**
     * Parse date
     *
     * @return Was the date parsed?
     */
    boolean parseDate() {
        if (datePosition == position) {
            return true;
        }
        if (string.charAt(position) == 'D') {
            ++position;
            parseDigits(4);
            parseCharacter('-');
            parseDigits(2);
            parseCharacter('-');
            parseDigits(2);
            datePosition = position;
            return true;
        }
        return false;
    }

    /**
     * Parse date and time
     *
     * @return Was the date and time parsed?
     */
    boolean parseDateTime() {
        if (dateTimePosition == position) {
            return true;
        }
        if (parseDate()
            && position < string.length()
            && parseTime()) {
            dateTimePosition = position;
            return true;
        }
        return false;
    }

    /**
     * Parse date and time with offset
     *
     * @return Was the date and time with offset parsed?
     */
    boolean parseOffsetDateTime() {
        if (parseDateTime()) {
            if (position < string.length()
                && string.charAt(position) == 'Z') {
                ++position;
                return true;
            }
            if (position + 3 < string.length()
                    && string.charAt(position + 3) == ':') {
                if (string.charAt(position) != '-' && string.charAt(position) != '+') {
                    throw new UnexpectedCharacterException(position, OFFSET_PREFIX_CHARACTERS, string.charAt(position));
                }
                ++position;
                parseDigits(2);
                parseCharacter(':');
                parseDigits(2);
                return true;
            }
        }
        return false;
    }

    boolean parseTime() {
        if (string.charAt(position) == 'T') {
            ++position;
            parseDigits(2);
            parseCharacter(':');
            parseDigits(2);
            if (position < string.length() && string.charAt(position) == ':') {
                ++position;
                parseDigits(2);
                if (position + 1 < string.length()
                        && string.charAt(position) == '.'
                        && isDigit(string.charAt(position + 1))) {
                    position += 2;
                    for (int i = 1; i < 9; ++i) {
                        if (position >= string.length() || !isDigit(string.charAt(position))) {
                            break;
                        }
                        ++position;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Parse the identifier
     *
     * @return Was the identifier parsed?
     */
    boolean parseIdentifier() {
        if (isIdentifierCharacter(string.charAt(position))) {
            do {
                ++position;
            } while (position < string.length() && isIdentifierCharacter(string.charAt(position)));
            return true;
        }
        return false;
    }

    /**
     * Parse token
     *
     * @param expectedKinds Expected kinds
     * @return Вид токена
     */
    TokenKind parseToken(TokenKind... expectedKinds) {
        skipSpaces();
        if (position != string.length()) {
            lastTokenPosition = position;
            for (TokenKind tokenKind : expectedKinds) {
                if (tokenKind.parseMethod.test(this)) {
                    return tokenKind;
                }
            }
        }
        return null;
    }

    /**
     * Tokenize the required token
     *
     * @param tokenKinds Token kinds
     */
    TokenKind parseRequiredToken(TokenKind... tokenKinds) {
        TokenKind result = parseToken(tokenKinds);
        if (result == null) {
            throw new TokenNotFoundException(position, tokenKinds);
        }
        return result;
    }

    /**
     * Get token image
     */
    String getTokenImage() {
        return string.substring(lastTokenPosition, position);
    }

    /**
     * Get context
     *
     * @param position Позиция
     */
    String getContext(int position) {
        return position + ": " + string.substring(position, this.position);
    }

    /**
     * Check that the end of the line is reached
     */
    void checkEnd() {
        if (position != string.length()) {
            throw new EndOfStringExpectedException(position, string.substring(position));
        }
    }
}
