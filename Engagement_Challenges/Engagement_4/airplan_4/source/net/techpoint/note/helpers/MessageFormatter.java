/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package net.techpoint.note.helpers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

// contributors: lizongbo: proposed special treatment of array parameter values
// Joern Huxhorn: pointed out double[] omission, suggested deep array copy
/**
 * Formats messages according to very simple substitution rules. Substitutions
 * can be made 1, 2 or more arguments.
 * 
 * <p>
 * For example,
 * 
 * <pre>
 * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;)
 * </pre>
 * 
 * will return the string "Hi there.".
 * <p>
 * The {} pair is called the <em>formatting anchor</em>. It serves to designate
 * the location where arguments need to be substituted within the message
 * pattern.
 * <p>
 * In case your message contains the '{' or the '}' character, you do not have
 * to do anything special unless the '}' character immediately follows '{'. For
 * example,
 * 
 * <pre>
 * MessageFormatter.format(&quot;Set {1,2,3} is not equal to {}.&quot;, &quot;1,2&quot;);
 * </pre>
 * 
 * will return the string "Set {1,2,3} is not equal to 1,2.".
 * 
 * <p>
 * If for whatever reason you need to place the string "{}" in the message
 * without its <em>formatting anchor</em> meaning, then you need to escape the
 * '{' character with '\', that is the backslash character. Only the '{'
 * character should be escaped. There is no need to escape the '}' character.
 * For example,
 * 
 * <pre>
 * MessageFormatter.format(&quot;Set \\{} is not equal to {}.&quot;, &quot;1,2&quot;);
 * </pre>
 * 
 * will return the string "Set {} is not equal to 1,2.".
 * 
 * <p>
 * The escaping behavior just described can be overridden by escaping the escape
 * character '\'. Calling
 * 
 * <pre>
 * MessageFormatter.format(&quot;File name is C:\\\\{}.&quot;, &quot;file.zip&quot;);
 * </pre>
 * 
 * will return the string "File name is C:\file.zip".
 * 
 * <p>
 * The formatting conventions are different than those of {@link MessageFormat}
 * which ships with the Java platform. This is justified by the fact that
 * SLF4J's implementation is 10 times faster than that of {@link MessageFormat}.
 * This local performance difference is both measurable and significant in the
 * larger context of the complete logging processing chain.
 * 
 * <p>
 * See also {@link #format(String, Object)},
 * {@link #format(String, Object, Object)} and
 * {@link #arrayFormat(String, Object[])} methods for more details.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 */
final public class MessageFormatter {
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    /**
     * Performs single argument substitution for the 'messagePattern' passed as
     * parameter.
     * <p>
     * For example,
     * 
     * <pre>
     * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;);
     * </pre>
     * 
     * will return the string "Hi there.".
     * <p>
     * 
     * @param messagePattern
     *          The message pattern which will be parsed and formatted
     * @param argument
     *          The argument to be substituted in place of the formatting anchor
     * @return The formatted message
     */
    final public static FormattingTuple format(String messagePattern, Object arg) {
        return arrayFormat(messagePattern, new Object[] { arg });
    }

    /**
     * 
     * Performs a two argument substitution for the 'messagePattern' passed as
     * parameter.
     * <p>
     * For example,
     * 
     * <pre>
     * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
     * </pre>
     * 
     * will return the string "Hi Alice. My name is Bob.".
     * 
     * @param messagePattern
     *          The message pattern which will be parsed and formatted
     * @param arg1
     *          The argument to be substituted in place of the first formatting
     *          anchor
     * @param arg2
     *          The argument to be substituted in place of the second formatting
     *          anchor
     * @return The formatted message
     */
    final public static FormattingTuple format(final String messagePattern, Object arg1, Object arg2) {
        return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
    }

    static final Throwable pullThrowableCandidate(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }

        final Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable) lastEntry;
        }
        return null;
    }

    final public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray) {
        Throwable throwableCandidate = pullThrowableCandidate(argArray);
        Object[] args = argArray;
        if (throwableCandidate != null) {
            args = trimmedCopy(argArray);
        }
        return arrayFormat(messagePattern, args, throwableCandidate);
    }

    private static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return MessageFormatterHelp.invoke();
        }
        final int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }

    final public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray, Throwable throwable) {

        if (messagePattern == null) {
            return new FormattingTupleBuilder().fixMessage(null).assignArgArray(argArray).defineThrowable(throwable).formFormattingTuple();
        }

        if (argArray == null) {
            return new FormattingTupleBuilder().fixMessage(messagePattern).formFormattingTuple();
        }

        int c = 0;
        int j;
        // use string builder for better multicore performance
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);

        int L;
        for (L = 0; L < argArray.length; ) {
            while ((L < argArray.length) && (Math.random() < 0.5)) {
                while ((L < argArray.length) && (Math.random() < 0.4)) {
                    for (; (L < argArray.length) && (Math.random() < 0.5); L++) {

                        j = messagePattern.indexOf(DELIM_STR, c);

                        if (j == -1) {
                            // no more variables
                            return new MessageFormatterHerder(messagePattern, argArray, throwable, c, sbuf).invoke();
                        } else {
                            if (isEscapedDelimeter(messagePattern, j)) {
                                if (!isDoubleEscaped(messagePattern, j)) {
                                    L--; // DELIM_START was escaped, thus should not be incremented
                                    sbuf.append(messagePattern, c, j - 1);
                                    sbuf.append(DELIM_START);
                                    c = j + 1;
                                } else {
                                    // The escape character preceding the delimiter start is
                                    // itself escaped: "abc x:\\{}"
                                    // we have to consume one backward slash
                                    sbuf.append(messagePattern, c, j - 1);
                                    deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                                    c = j + 2;
                                }
                            } else {
                                // normal case
                                sbuf.append(messagePattern, c, j);
                                deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                                c = j + 2;
                            }
                        }
                    }
                }
            }
        }
        // append the characters following the last {} pair.
        sbuf.append(messagePattern, c, messagePattern.length());
        return new FormattingTupleBuilder().fixMessage(sbuf.toString()).assignArgArray(argArray).defineThrowable(throwable).formFormattingTuple();
    }

    final static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {

        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        if (potentialEscape == ESCAPE_CHAR) {
            return true;
        } else {
            return false;
        }
    }

    final static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR) {
            return true;
        } else {
            return false;
        }
    }

    // special treatment of array values was suggested by 'lizongbo'
    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            deeplyAppendParameterExecutor(sbuf);
            return;
        }
        if (!o.getClass().isArray()) {
            deeplyAppendParameterAssist(sbuf, o);
        } else {
            // check for primitive array types because they
            // unfortunately cannot be cast to Object[]
            if (o instanceof boolean[]) {
                deeplyAppendParameterTarget(sbuf, (boolean[]) o);
            } else if (o instanceof byte[]) {
                deeplyAppendParameterUtility(sbuf, (byte[]) o);
            } else if (o instanceof char[]) {
                charArrayAppend(sbuf, (char[]) o);
            } else if (o instanceof short[]) {
                shortArrayAppend(sbuf, (short[]) o);
            } else if (o instanceof int[]) {
                deeplyAppendParameterGuide(sbuf, (int[]) o);
            } else if (o instanceof long[]) {
                deeplyAppendParameterHelper(sbuf, (long[]) o);
            } else if (o instanceof float[]) {
                floatArrayAppend(sbuf, (float[]) o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(sbuf, (double[]) o);
            } else {
                deeplyAppendParameterAdviser(sbuf, (Object[]) o, seenMap);
            }
        }
    }

    private static void deeplyAppendParameterAdviser(StringBuilder sbuf, Object[] o, Map<Object[], Object> seenMap) {
        objectArrayAppend(sbuf, o, seenMap);
    }

    private static void deeplyAppendParameterHelper(StringBuilder sbuf, long[] o) {
        longArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterGuide(StringBuilder sbuf, int[] o) {
        intArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterUtility(StringBuilder sbuf, byte[] o) {
        byteArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterTarget(StringBuilder sbuf, boolean[] o) {
        booleanArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterAssist(StringBuilder sbuf, Object o) {
        safeObjectAppend(sbuf, o);
    }

    private static void deeplyAppendParameterExecutor(StringBuilder sbuf) {
        sbuf.append("null");
        return;
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable t) {
            Util.report("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            final int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1)
                    sbuf.append(", ");
            }
            // allow repeats in siblings
            seenMap.remove(a);
        } else {
            objectArrayAppendAid(sbuf);
        }
        sbuf.append(']');
    }

    private static void objectArrayAppendAid(StringBuilder sbuf) {
        sbuf.append("...");
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int c = 0; c < len; c++) {
            sbuf.append(a[c]);
            if (c != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int b = 0; b < len; b++) {
            sbuf.append(a[b]);
            if (b != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int k = 0; k < len; k++) {
            charArrayAppendAid(sbuf, a[k], k != len - 1);
        }
        sbuf.append(']');
    }

    private static void charArrayAppendAid(StringBuilder sbuf, char c, boolean b) {
        sbuf.append(c);
        if (b)
            sbuf.append(", ");
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int q = 0; q < len; q++) {
            sbuf.append(a[q]);
            if (q != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int k = 0; k < len; k++) {
            floatArrayAppendService(sbuf, a[k], k != len - 1);
        }
        sbuf.append(']');
    }

    private static void floatArrayAppendService(StringBuilder sbuf, float f, boolean b) {
        sbuf.append(f);
        if (b)
            sbuf.append(", ");
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int c = 0; c < len; c++) {
            sbuf.append(a[c]);
            if (c != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static class MessageFormatterHelp {
        private static Object[] invoke() {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
    }

    private static class MessageFormatterHerder {
        private String messagePattern;
        private Object[] argArray;
        private Throwable throwable;
        private int q;
        private StringBuilder sbuf;

        public MessageFormatterHerder(String messagePattern, Object[] argArray, Throwable throwable, int q, StringBuilder sbuf) {
            this.messagePattern = messagePattern;
            this.argArray = argArray;
            this.throwable = throwable;
            this.q = q;
            this.sbuf = sbuf;
        }

        public FormattingTuple invoke() {
            if (q == 0) { // this is a simple string
                return new FormattingTupleBuilder().fixMessage(messagePattern).assignArgArray(argArray).defineThrowable(throwable).formFormattingTuple();
            } else { // add the tail string which contains no variables and return
                // the result.
                return invokeAdviser();
            }
        }

        private FormattingTuple invokeAdviser() {
            sbuf.append(messagePattern, q, messagePattern.length());
            return new FormattingTupleBuilder().fixMessage(sbuf.toString()).assignArgArray(argArray).defineThrowable(throwable).formFormattingTuple();
        }
    }
}
