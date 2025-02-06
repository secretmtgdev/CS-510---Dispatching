package edu.uw.advjava.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple Roman numeral class constructed for the purpose of demonstrating
 * the use of JUnit tests.
 *
 * @author Russ Moul
 */
public final class RomanNumeral {
    /** This class' logger. */
    private static final Logger log = LoggerFactory.getLogger(RomanNumeral.class);

     /** Value of Roman M. */
    private static final int M_VALUE = 1000;

     /** Value of Roman CM. */
    private static final int CM_VALUE = 900;

     /** Value of Roman D. */
    private static final int D_VALUE = 500;


     /** Value of Roman CD. */
    private static final int CD_VALUE = 400;


     /** Value of Roman C. */
    private static final int C_VALUE = 100;


     /** Value of Roman XC. */
    private static final int XC_VALUE = 90;


     /** Value of Roman L. */
    private static final int L_VALUE = 50;


     /** Value of Roman XL. */
    private static final int XL_VALUE = 40;

     /** Value of Roman X. */
    private static final int X_VALUE = 10;

     /** Value of Roman IX. */
    private static final int IX_VALUE = 9;

     /** Value of Roman V. */
    private static final int V_VALUE = 5;

     /** Value of Roman IV. */
    private static final int IV_VALUE = 4;

     /** Value of Roman I. */
    private static final int I_VALUE = 1;

    /** Map of important roman numeral equivalents. */
    private static RomanEquiv[] romanMap = {
        new RomanEquiv(M_VALUE,  "M"),
        new RomanEquiv(CM_VALUE, "CM"),
        new RomanEquiv(D_VALUE,  "D"),
        new RomanEquiv(CD_VALUE, "CD"),
        new RomanEquiv(C_VALUE,  "C"),
        new RomanEquiv(XC_VALUE, "XC"),
        new RomanEquiv(L_VALUE,  "L"),
        new RomanEquiv(XL_VALUE, "XL"),
        new RomanEquiv(X_VALUE,  "X"),
        new RomanEquiv(IX_VALUE, "IX"),
        new RomanEquiv(V_VALUE,  "V"),
        new RomanEquiv(IV_VALUE, "IV"),
        new RomanEquiv(I_VALUE,  "I")
    };

    /** Value of this Roman numeral object. */
    private int value;

    /** Roman numeral representation. */
    private String stringRep;

    /**
     * Constructor.
     *
     * @param v integer value of the Roman numeral to be created
     */
    public RomanNumeral(final int v) {
        value = v;
    }

    /**
     * Constructor.
     *
     * @param s Roman numeral string representation of the Roman numeral to be
     *          created
     */
    public RomanNumeral(final String s) {
        value = parseInt(s);
        stringRep = s;
    }

    /**
     * Gets the integer value of this object.
     *
     * @return the integer value
     */
    public int intValue() {
        return value;
    }

    /**
     * Gets the Roman numeral representation of this objects value.
     *
     * @return the Roman numeral representation of this objects value
     */
    @Override
    public String toString() {
        if (stringRep == null) {
            stringRep = toRoman(value);
        }

        return stringRep;
    }

    /**
     * Compares this objects value to the value of the object provided.  If two
     * objects have the same value they are deemed equal.
     *
     * @param obj the object to be compared to this object
     *
     * @return true if they have the same value
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RomanNumeral other = (RomanNumeral) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}

    /**
     * Calculates this objects hash code.  Any two objects with the same value
     * will have the same hash code.
     *
     * @return this objects hash code
     */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

    /**
     * Composes the Roman numeral representation for the provided integer value.
     *
     * @param value the integer value to determine the Roman numeral
     *              representation of
     *
     * @return the Roman numeral string
     */
    public static String toRoman(final int value) {
        int tmp = value;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < romanMap.length; ++i) {
            final RomanEquiv re = romanMap[i];

            while (tmp >= re.equivValue) {
                tmp -= re.equivValue;
                sb.append(re.romanDigits);
            }
        }

        String s = sb.toString();
        log.debug("{} = {}", value, s);
        return s;
    }

	/**
     * Parses a Roman numeral string to derive its integer value.
     *
     * @param s the Roman numeral string
     *
     * @return the integer value of the Roman numeral string
     */
    public static int parseInt(final String s) {
        String tmp = s;
        int x = 0;

        for (int i = 0; i < romanMap.length; ++i) {
            final RomanEquiv re = romanMap[i];

            while (tmp.startsWith(re.romanDigits)) {
                x += re.equivValue;
                tmp = tmp.substring(re.romanDigits.length());
            }
        }

        log.debug("{} = {}", s, x);
        return x;
    }

    /**
     * Captures equivalent integer and Roman numeral equivalents.
     */
    private static class RomanEquiv {
        /** Integer value. */
        int equivValue;

        /** Roman numeral equivalent. */
        String romanDigits;

        /**
         * Constructor.
         *
         * @param n the integer value
         * @param s the Roman numeral representation
         */
        public RomanEquiv(final int n, final String s) {
            equivValue = n;
            romanDigits = s;
        }
    }
}
