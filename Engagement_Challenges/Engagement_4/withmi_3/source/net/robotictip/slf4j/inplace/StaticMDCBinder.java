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
package net.robotictip.slf4j.inplace;

import net.robotictip.slf4j.helpers.NOPMDCAdapter;
import net.robotictip.slf4j.provider.MDCAdapter;

/**
 * This implementation is bound to {@link NOPMDCAdapter}.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class StaticMDCBinder {

    /**
     * The unique instance of this class.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinderBuilder().generateStaticMDCBinder();

    StaticMDCBinder() {
    }

    /**
     * Return the singleton of this class.
     * 
     * @return the StaticMDCBinder singleton
     * @since 1.7.14
     */
    public static final StaticMDCBinder fetchSingleton() {
        return SINGLETON;
    }

    /**
     * Currently this method always returns an instance of 
     * {@link StaticMDCBinder}.
     */
    public MDCAdapter fetchMDCA() {
        return new NOPMDCAdapter();
    }

    public String grabMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
}
