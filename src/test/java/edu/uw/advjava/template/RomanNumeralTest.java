package edu.uw.advjava.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for the RomanNumeral class.  Illustrates the use of JUnit 5.x.
 */
public class RomanNumeralTest {

    /** Roman numeral representing 1975, initialized by a string value. */
    private RomanNumeral romanStr1975;
    /** Roman numeral representing 1975, initialized by an int value. */
    private RomanNumeral romanInt1975;
    /** Roman numeral representing 1989, initialized by a string value. */
    private RomanNumeral romanStr1989;
    /** Roman numeral representing 1989, initialized by an int value. */
    private RomanNumeral romanInt1989;
    /** Roman numeral representing 1998, initialized by a string value. */
    private RomanNumeral romanStr1998;
    /** Roman numeral representing 1998, initialized by an int value. */
    private RomanNumeral romanInt1998;

    /**
     * Initialize the test fixture.  Initializes variables used by the various
     * tests.
     */
    @BeforeEach
    public void init() {
        romanStr1975 = new RomanNumeral("MCMLXXV");
        romanInt1975 = new RomanNumeral(1975);
        romanStr1989 = new RomanNumeral("MCMLXXXIX");
        romanInt1989 = new RomanNumeral(1989);
        romanStr1998 = new RomanNumeral("MCMXCVIII");
        romanInt1998 = new RomanNumeral(1998);
    }

    /**
     * Test the constructor's handling of a null argument.
     */
    @Test
    public void verifyConstructor() {
    	Assertions.assertThrows(NullPointerException.class, () -> {
            new RomanNumeral(null);
    	  });
    }

    /**
     * Teardown the test fixture.  Sets variables used by the various
     * tests to null.
     */
    @AfterEach
    public void clean() {
        romanStr1975 = null;
        romanInt1975 = null;
        romanStr1989 = null;
        romanInt1989 = null;
        romanStr1998 = null;
        romanInt1998 = null;
    }

    /**
     * Tests the <code>equals</code> method.
     */
    @Test
    public void equals() {
        assertFalse(romanInt1975.equals(null));
        assertFalse(romanInt1975.equals("Foo"));
        assertFalse(romanInt1975.equals(romanInt1989));
        assertEquals(romanInt1975, romanInt1975);
        assertEquals(romanStr1975, romanInt1975);
        assertEquals(romanStr1989, romanInt1989);
        assertEquals(romanStr1998, romanInt1998);
    }

    /**
     * Tests the <code>hashCode</code> method.
     */
    @Test
    public void verifyHashCode() {
        assertEquals(romanStr1975.hashCode(),romanInt1975.hashCode());
        assertNotEquals(romanInt1989.hashCode(), romanInt1975.hashCode());
    }

    /**
     * Tests the <code>toString</code> method.
     */
    @Test
    public void verifyToString() {
        assertEquals("MCMLXXV", romanInt1975.toString());
        assertEquals("MCMLXXXIX", romanInt1989.toString());
        assertEquals("MCMXCVIII", romanInt1998.toString());
    }

    /**
     * Tests the <code>getValue</code> method.
     */
    @Test
    public void getValue() {
        assertEquals(1975, romanStr1975.intValue());
        assertEquals(1989, romanStr1989.intValue());
        assertEquals(1998, romanStr1998.intValue());
    }

    /**
     * Tests the <code>parseInt</code> method.
     */
    @Test
    public void parseInt() {
        assertEquals(0, RomanNumeral.parseInt(""));
        assertEquals(1, RomanNumeral.parseInt("I"));
        assertEquals(2, RomanNumeral.parseInt("II"));
        assertEquals(3, RomanNumeral.parseInt("III"));
        assertEquals(4, RomanNumeral.parseInt("IV"));
        assertEquals(5, RomanNumeral.parseInt("V"));
        assertEquals(6, RomanNumeral.parseInt("VI"));
        assertEquals(7, RomanNumeral.parseInt("VII"));
        assertEquals(8, RomanNumeral.parseInt("VIII"));
        assertEquals(9, RomanNumeral.parseInt("IX"));
        assertEquals(10, RomanNumeral.parseInt("X"));
        assertEquals(11, RomanNumeral.parseInt("XI"));
        assertEquals(12, RomanNumeral.parseInt("XII"));
        assertEquals(13, RomanNumeral.parseInt("XIII"));
        assertEquals(14, RomanNumeral.parseInt("XIV"));
        assertEquals(15, RomanNumeral.parseInt("XV"));
        assertEquals(16, RomanNumeral.parseInt("XVI"));
        assertEquals(17, RomanNumeral.parseInt("XVII"));
        assertEquals(18, RomanNumeral.parseInt("XVIII"));
        assertEquals(19, RomanNumeral.parseInt("XIX"));
        assertEquals(20, RomanNumeral.parseInt("XX"));
        assertEquals(21, RomanNumeral.parseInt("XXI"));
        assertEquals(29, RomanNumeral.parseInt("XXIX"));
        assertEquals(30, RomanNumeral.parseInt("XXX"));
        assertEquals(34, RomanNumeral.parseInt("XXXIV"));
        assertEquals(40, RomanNumeral.parseInt("XL"));
        assertEquals(50, RomanNumeral.parseInt("L"));
        assertEquals(60, RomanNumeral.parseInt("LX"));
        assertEquals(70, RomanNumeral.parseInt("LXX"));
        assertEquals(80, RomanNumeral.parseInt("LXXX"));
        assertEquals(90, RomanNumeral.parseInt("XC"));
        assertEquals(100, RomanNumeral.parseInt("C"));
        assertEquals(200, RomanNumeral.parseInt("CC"));
        assertEquals(300, RomanNumeral.parseInt("CCC"));
        assertEquals(400, RomanNumeral.parseInt("CD"));
        assertEquals(500, RomanNumeral.parseInt("D"));
        assertEquals(600, RomanNumeral.parseInt("DC"));
        assertEquals(700, RomanNumeral.parseInt("DCC"));
        assertEquals(800, RomanNumeral.parseInt("DCCC"));
        assertEquals(900, RomanNumeral.parseInt("CM"));
        assertEquals(1000, RomanNumeral.parseInt("M"));
        assertEquals(1900, RomanNumeral.parseInt("MCM"));
        assertEquals(1975, RomanNumeral.parseInt("MCMLXXV"));
        assertEquals(1989, RomanNumeral.parseInt("MCMLXXXIX"));
        assertEquals(1999, RomanNumeral.parseInt("MCMXCIX"));
        assertEquals(2000, RomanNumeral.parseInt("MM"));
        assertEquals(2001, RomanNumeral.parseInt("MMI"));
    }

    /**
     * Tests the <code>toRoman</code> method.
     */
    @Test
    public void toRoman() {
        assertEquals("", RomanNumeral.toRoman(0));
        assertEquals("I", RomanNumeral.toRoman(1));
        assertEquals("II", RomanNumeral.toRoman(2));
        assertEquals("III", RomanNumeral.toRoman(3));
        assertEquals("IV", RomanNumeral.toRoman(4));
        assertEquals("V", RomanNumeral.toRoman(5));
        assertEquals("VI", RomanNumeral.toRoman(6));
        assertEquals("VII", RomanNumeral.toRoman(7));
        assertEquals("VIII", RomanNumeral.toRoman(8));
        assertEquals("IX", RomanNumeral.toRoman(9));
        assertEquals("X", RomanNumeral.toRoman(10));
        assertEquals("XI", RomanNumeral.toRoman(11));
        assertEquals("XII", RomanNumeral.toRoman(12));
        assertEquals("XIII", RomanNumeral.toRoman(13));
        assertEquals("XIV", RomanNumeral.toRoman(14));
        assertEquals("XV", RomanNumeral.toRoman(15));
        assertEquals("XVI", RomanNumeral.toRoman(16));
        assertEquals("XVII", RomanNumeral.toRoman(17));
        assertEquals("XVIII", RomanNumeral.toRoman(18));
        assertEquals("XIX", RomanNumeral.toRoman(19));
        assertEquals("XX", RomanNumeral.toRoman(20));
        assertEquals("XXI", RomanNumeral.toRoman(21));
        assertEquals("XXIX", RomanNumeral.toRoman(29));
        assertEquals("XXX", RomanNumeral.toRoman(30));
        assertEquals("XXXIV", RomanNumeral.toRoman(34));
        assertEquals("XXX", RomanNumeral.toRoman(30));
        assertEquals("XL", RomanNumeral.toRoman(40));
        assertEquals("L", RomanNumeral.toRoman(50));
        assertEquals("LX", RomanNumeral.toRoman(60));
        assertEquals("LXX", RomanNumeral.toRoman(70));
        assertEquals("LXXX", RomanNumeral.toRoman(80));
        assertEquals("XC", RomanNumeral.toRoman(90));
        assertEquals("C", RomanNumeral.toRoman(100));
        assertEquals("CC", RomanNumeral.toRoman(200));
        assertEquals("CCC", RomanNumeral.toRoman(300));
        assertEquals("CD", RomanNumeral.toRoman(400));
        assertEquals("D", RomanNumeral.toRoman(500));
        assertEquals("DC", RomanNumeral.toRoman(600));
        assertEquals("DCC", RomanNumeral.toRoman(700));
        assertEquals("DCCC", RomanNumeral.toRoman(800));
        assertEquals("CM", RomanNumeral.toRoman(900));
        assertEquals("M", RomanNumeral.toRoman(1000));
        assertEquals("MCM", RomanNumeral.toRoman(1900));
        assertEquals("MCMLXXV", RomanNumeral.toRoman(1975));
        assertEquals("MCMLXXXIX", RomanNumeral.toRoman(1989));
        assertEquals("MCMXCIX", RomanNumeral.toRoman(1999));
        assertEquals("MM", RomanNumeral.toRoman(2000));
        assertEquals("MMI", RomanNumeral.toRoman(2001));
    }

}
