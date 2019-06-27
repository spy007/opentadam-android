/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opentadam.ui.registration;

import java.text.ParseException;
import java.util.ArrayList;


public class MaskedFormatter {

    // Potential values in mask.
    private static final char DIGIT_KEY = '#';
    private static final char LITERAL_KEY = '\'';
    private static final char UPPERCASE_KEY = 'U';
    private static final char LOWERCASE_KEY = 'L';
    private static final char ALPHA_NUMERIC_KEY = 'A';
    private static final char CHARACTER_KEY = '?';
    private static final char ANYTHING_KEY = '*';
    private static final char HEX_KEY = 'H';
    private static final MaskCharacter[] EmptyMaskChars =
            new MaskCharacter[0];
    /**
     * The user specified mask.
     */
    private String mask;
    /**
     * Indicates if the value contains the literal characters.
     */
    private boolean containsLiteralChars;
    /**
     * List of valid characters.
     */
    private String validCharacters;

    /**
     * List of invalid characters.
     */
    private String invalidCharacters;

    /**
     * String used to represent characters not present.
     */
    private char placeholder;

    /**
     * String used for the passed in value if it does not completely
     * fill the mask.
     */
    private String placeholderString;

    private transient MaskCharacter[] maskChars;


    /**
     * Creates a MaskFormatter with no mask.
     */
    private MaskedFormatter() {

        containsLiteralChars = true;
        maskChars = EmptyMaskChars;
        placeholder = ' ';
    }

    /**
     * Creates a MaskFormatter with the specified mask.
     * A ParseException
     * will be thrown if mask is an invalid mask.
     *
     * @throws ParseException if mask does not contain valid mask characters
     */
    public MaskedFormatter(String mask) throws ParseException {
        this();
        setMask(mask);
    }

    /**
     * Returns the formatting mask.
     *
     * @return Mask dictating legal character values.
     */
    private String getMask() {
        return mask;
    }

    /**
     * Sets the mask dictating the legal characters.
     * This will throw a ParseException if mask is
     * not valid.
     *
     * @throws ParseException if mask does not contain valid mask characters
     */
    private void setMask(String mask) throws ParseException {
        this.mask = mask;
        updateInternalMask();
    }

    /**
     * Updates the internal representation of the mask.
     */
    private void updateInternalMask() throws ParseException {
        String mask = getMask();
        ArrayList<MaskCharacter> fixed = new ArrayList<>();

        if (mask != null) {
            for (int counter = 0, maxCounter = mask.length();
                 counter < maxCounter; counter++) {
                char maskChar = mask.charAt(counter);

                switch (maskChar) {
                    case DIGIT_KEY:
                        fixed.add(new DigitMaskCharacter());
                        break;
                    case LITERAL_KEY:
                        if (++counter < maxCounter) {
                            maskChar = mask.charAt(counter);
                            fixed.add(new LiteralCharacter(maskChar));
                        }
                        // else: Could actually throw if else
                        break;
                    case UPPERCASE_KEY:
                        fixed.add(new UpperCaseCharacter());
                        break;
                    case LOWERCASE_KEY:
                        fixed.add(new LowerCaseCharacter());
                        break;
                    case ALPHA_NUMERIC_KEY:
                        fixed.add(new AlphaNumericCharacter());
                        break;
                    case CHARACTER_KEY:
                        fixed.add(new CharCharacter());
                        break;
                    case ANYTHING_KEY:
                        fixed.add(new MaskCharacter());
                        break;
                    case HEX_KEY:
                        fixed.add(new HexCharacter());
                        break;
                    default:
                        fixed.add(new LiteralCharacter(maskChar));
                        break;
                }
            }
        }
        if (fixed.size() == 0) {
            maskChars = EmptyMaskChars;
        } else {
            maskChars = new MaskCharacter[fixed.size()];
            fixed.toArray(maskChars);
        }
    }

    private String getValidCharacters() {
        return validCharacters;
    }


    private String getInvalidCharacters() {
        return invalidCharacters;
    }

    public void setValueContainsLiteralCharacters(
            boolean containsLiteralChars) {
        this.containsLiteralChars = containsLiteralChars;
    }

    /**
     * Returns the character to use in place of characters that are not present
     * in the value, ie the user must fill them in.
     *
     * @return Character used when formatting if the value does not
     * completely fill the mask
     */
    private char getPlaceholderCharacter() {
        return placeholder;
    }

    /**
     * Sets the character to use in place of characters that are not present
     * in the value, ie the user must fill them in. The default value is
     * a space.
     * <p/>
     * This is only applicable if the placeholder string has not been
     * specified, or does not completely fill in the mask.
     *
     * @param placeholder Character used when formatting if the value does not
     *                    completely fill the mask
     */
    public void setPlaceholderCharacter(char placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Returns the String to use if the value does not completely fill
     * in the mask.
     *
     * @return String used when formatting if the value does not
     * completely fill the mask
     */
    private String getPlaceholder() {
        return placeholderString;
    }

    public String valueToString(Object value) throws ParseException {
        String sValue = (value == null) ? "" : value.toString();
        StringBuffer result = new StringBuffer();
        String placeholder = getPlaceholder();
        int[] valueCounter = {0};

        append(result, sValue, valueCounter, placeholder, maskChars);
        return result.toString();
    }

    /**
     * Invokes append on the mask characters in
     * mask.
     */
    private void append(StringBuffer result, String value, int[] index,
                        String placeholder, MaskCharacter[] mask)
            throws ParseException {
        for (MaskCharacter aMask : mask) {
            aMask.append(result, value, index, placeholder);
        }
    }

    private class MaskCharacter {
        public boolean isLiteral() {
            return false;
        }

        public boolean isValidCharacter(char character) {
            if (isLiteral()) {
                return getChar(character) == character;
            }

            character = getChar(character);

            String filter = getValidCharacters();
            if (filter != null && filter.indexOf(character) == -1) {
                return false;
            }

            filter = getInvalidCharacters();
            return !(filter != null && filter.indexOf(character) != -1);

        }

        public char getChar(char character) {
            return character;
        }

        public void append(StringBuffer buffer, String formatting, int[] index, String placeholder) {
            boolean inString = index[0] < formatting.length();
            char character = inString ? formatting.charAt(index[0]) : 0;

            if (!inString) {
                return;
            }

            if (isLiteral()) {
                buffer.append(getChar(character));

                if (character == getChar(character)) {
                    index[0] = index[0] + 1;
                }
            } else if (index[0] >= formatting.length()) {
                if (placeholder != null && index[0] < placeholder.length()) {
                    buffer.append(placeholder.charAt(index[0]));
                } else {
                    buffer.append(getPlaceholderCharacter());
                }

                index[0] = index[0] + 1;
            } else if (isValidCharacter(character)) {
                buffer.append(getChar(character));
                index[0] = index[0] + 1;
            }
        }
    }

    private class LiteralCharacter extends MaskCharacter {
        private char mLiteralCharacter;

        public LiteralCharacter(char character) {
            mLiteralCharacter = character;
        }

        public boolean isLiteral() {
            return true;
        }

        public char getChar(char aChar) {
            return mLiteralCharacter;
        }
    }

    private class DigitMaskCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isDigit(character) && super.isValidCharacter(character);
        }
    }

    private class UpperCaseCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetter(character) && super.isValidCharacter(character);
        }

        public char getChar(char character) {
            return Character.toUpperCase(character);
        }
    }

    private class LowerCaseCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetter(character) && super.isValidCharacter(character);
        }

        public char getChar(char character) {
            return Character.toLowerCase(character);
        }
    }

    private class AlphaNumericCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetterOrDigit(character) && super.isValidCharacter(character);
        }
    }

    private class CharCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetter(character) && super.isValidCharacter(character);
        }
    }

    private class HexCharacter extends MaskCharacter {
        private static final String HEX_CHARS = "0123456789abcedfABCDEF";

        public boolean isValidCharacter(char character) {
            return HEX_CHARS.indexOf(character) != -1 && super.isValidCharacter(character);
        }

        public char getChar(char character) {
            if (Character.isDigit(character)) {
                return character;
            }

            return Character.toUpperCase(character);
        }
    }
}