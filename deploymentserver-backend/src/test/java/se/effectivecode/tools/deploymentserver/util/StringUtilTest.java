package se.effectivecode.tools.deploymentserver.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {
    String EMPTY = "";
    String NULL = null;
    String NOT_NULL_NOR_EMPTY_STRING = "This string is neither null nor empty";

    @Test
    void isNotEmptyShouldReturnTrueWhenInputStringIsNotEmptyNorNull() {
        assertTrue(StringUtil.isNotEmpty(NOT_NULL_NOR_EMPTY_STRING));
    }

    @Test
    void isNotEmptyShouldReturnFalseWhenInputStringIsNull() {
        assertFalse(StringUtil.isNotEmpty(NULL));
    }

    @Test
    void isNotEmptyShouldReturnFalseWhenInputStringIsEmpty() {
        assertFalse(StringUtil.isNotEmpty(EMPTY));
    }

    @Test
    void isEmptyShouldReturnTrueWhenTheInputStringIsEmpty() {
        assertTrue(StringUtil.isEmpty(EMPTY));
    }

    @Test
    void isEmptyShouldReturnFalseWhenInputStringIsNotEmpty() {
        assertFalse(StringUtil.isEmpty(NOT_NULL_NOR_EMPTY_STRING));
    }

    @Test
    void isEmptyShouldReturnFalseWhenInputStringIsNull() {
        assertFalse(StringUtil.isEmpty(NULL));
    }

    @Test
    void isNotNullShouldReturnFalseWhenInputStringIsNull() {
        assertFalse(StringUtil.isNotNull(NULL));
    }

    @Test
    void isNotNullShouldReturnTrueForEmptyString() {
        assertTrue(StringUtil.isNotNull(EMPTY));
    }

    @Test
    void isNotNullShouldReturnTrueForNoneNullNorEmptyString() {
        assertTrue(StringUtil.isNotNull(NOT_NULL_NOR_EMPTY_STRING));
    }

    @Test
    void isNullShouldReturnTrueWhenInParamIsNull() {
        assertTrue(StringUtil.isNull(NULL));
    }

    @Test
    void isNullShouldReturnFalseWhenTheStringIsNotNull() {
        assertFalse(StringUtil.isNull(NOT_NULL_NOR_EMPTY_STRING));
    }

    @Test
    void isNullShouldReturnFalseWhenInStringIsEmpty() {
        assertFalse(StringUtil.isNull(EMPTY));
    }
}