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
package org.digitaltip.record.helpers;

import org.digitaltip.record.LoggerFactory;
import org.digitaltip.record.Logger;
import org.digitaltip.record.Marker;
import org.digitaltip.record.event.EventRecodingLogger;
import org.digitaltip.record.event.LoggingEvent;
import org.digitaltip.record.event.SubstituteLoggingEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A logger implementation which logs via a delegate logger. By default, the delegate is a
 * {@link NOPLogger}. However, a different delegate can be set at any time.
 * <p/>
 * See also the <a href="http://www.slf4j.org/codes.html#substituteLogger">relevant
 * error code</a> documentation.
 *
 * @author Chetan Mehrotra
 * @author Ceki Gulcu
 */
public class SubstituteLogger implements Logger {

    private final String name;
    private final SubstituteLoggerExecutor substituteLoggerExecutor = new SubstituteLoggerExecutor(this);
    private volatile Logger _delegate;
    private Boolean delegateEventAware;
    private Method logMethodCache;
    private EventRecodingLogger eventRecodingLogger;
    private List<SubstituteLoggingEvent> eventList;

    public SubstituteLogger(String name, List<SubstituteLoggingEvent> eventList) {
        this.name = name;
        this.eventList = eventList;
    }

    public String getName() {
        return name;
    }

    public boolean isTraceEnabled() {
        return delegate().isTraceEnabled();
    }

    public void trace(String msg) {
        delegate().trace(msg);
    }

    public void trace(String format, Object arg) {
        delegate().trace(format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
        substituteLoggerExecutor.trace(format, arg1, arg2);
    }

    public void trace(String format, Object... arguments) {
        delegate().trace(format, arguments);
    }

    public void trace(String msg, Throwable t) {
        delegate().trace(msg, t);
    }

    public boolean isTraceEnabled(Marker marker) {
        return delegate().isTraceEnabled(marker);
    }

    public void trace(Marker marker, String msg) {
        delegate().trace(marker, msg);
    }

    public void trace(Marker marker, String format, Object arg) {
        substituteLoggerExecutor.trace(marker, format, arg);
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        substituteLoggerExecutor.trace(marker, format, arg1, arg2);
    }

    public void trace(Marker marker, String format, Object... arguments) {
        delegate().trace(marker, format, arguments);
    }

    public void trace(Marker marker, String msg, Throwable t) {
        delegate().trace(marker, msg, t);
    }

    public boolean isDebugEnabled() {
        return substituteLoggerExecutor.isDebugEnabled();
    }

    public void debug(String msg) {
        delegate().debug(msg);
    }

    public void debug(String format, Object arg) {
        substituteLoggerExecutor.debug(format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
        substituteLoggerExecutor.debug(format, arg1, arg2);
    }

    public void debug(String format, Object... arguments) {
        delegate().debug(format, arguments);
    }

    public void debug(String msg, Throwable t) {
        delegate().debug(msg, t);
    }

    public boolean isDebugEnabled(Marker marker) {
        return delegate().isDebugEnabled(marker);
    }

    public void debug(Marker marker, String msg) {
        delegate().debug(marker, msg);
    }

    public void debug(Marker marker, String format, Object arg) {
        delegate().debug(marker, format, arg);
    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        delegate().debug(marker, format, arg1, arg2);
    }

    public void debug(Marker marker, String format, Object... arguments) {
        delegate().debug(marker, format, arguments);
    }

    public void debug(Marker marker, String msg, Throwable t) {
        delegate().debug(marker, msg, t);
    }

    public boolean isInfoEnabled() {
        return delegate().isInfoEnabled();
    }

    public void info(String msg) {
        delegate().info(msg);
    }

    public void info(String format, Object arg) {
        delegate().info(format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        substituteLoggerExecutor.info(format, arg1, arg2);
    }

    public void info(String format, Object... arguments) {
        delegate().info(format, arguments);
    }

    public void info(String msg, Throwable t) {
        delegate().info(msg, t);
    }

    public boolean isInfoEnabled(Marker marker) {
        return delegate().isInfoEnabled(marker);
    }

    public void info(Marker marker, String msg) {
        substituteLoggerExecutor.info(marker, msg);
    }

    public void info(Marker marker, String format, Object arg) {
        delegate().info(marker, format, arg);
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        delegate().info(marker, format, arg1, arg2);
    }

    public void info(Marker marker, String format, Object... arguments) {
        delegate().info(marker, format, arguments);
    }

    public void info(Marker marker, String msg, Throwable t) {
        delegate().info(marker, msg, t);
    }

    public boolean isWarnEnabled() {
        return substituteLoggerExecutor.isWarnEnabled();
    }

    public void warn(String msg) {
        delegate().warn(msg);
    }

    public void warn(String format, Object arg) {
        delegate().warn(format, arg);
    }

    public void warn(String format, Object arg1, Object arg2) {
        delegate().warn(format, arg1, arg2);
    }

    public void warn(String format, Object... arguments) {
        delegate().warn(format, arguments);
    }

    public void warn(String msg, Throwable t) {
        delegate().warn(msg, t);
    }

    public boolean isWarnEnabled(Marker marker) {
        return delegate().isWarnEnabled(marker);
    }

    public void warn(Marker marker, String msg) {
        delegate().warn(marker, msg);
    }

    public void warn(Marker marker, String format, Object arg) {
        delegate().warn(marker, format, arg);
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        delegate().warn(marker, format, arg1, arg2);
    }

    public void warn(Marker marker, String format, Object... arguments) {
        delegate().warn(marker, format, arguments);
    }

    public void warn(Marker marker, String msg, Throwable t) {
        delegate().warn(marker, msg, t);
    }

    public boolean isErrorEnabled() {
        return delegate().isErrorEnabled();
    }

    public void error(String msg) {
        delegate().error(msg);
    }

    public void error(String format, Object arg) {
        delegate().error(format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        delegate().error(format, arg1, arg2);
    }

    public void error(String format, Object... arguments) {
        delegate().error(format, arguments);
    }

    public void error(String msg, Throwable t) {
        substituteLoggerExecutor.error(msg, t);
    }

    public boolean isErrorEnabled(Marker marker) {
        return delegate().isErrorEnabled(marker);
    }

    public void error(Marker marker, String msg) {
        delegate().error(marker, msg);
    }

    public void error(Marker marker, String format, Object arg) {
        delegate().error(marker, format, arg);
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        delegate().error(marker, format, arg1, arg2);
    }

    public void error(Marker marker, String format, Object... arguments) {
        delegate().error(marker, format, arguments);
    }

    public void error(Marker marker, String msg, Throwable t) {
        delegate().error(marker, msg, t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SubstituteLogger that = (SubstituteLogger) o;

        if (!name.equals(that.name))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Return the delegate logger instance if set. Otherwise, return a {@link NOPLogger}
     * instance.
     */
    Logger delegate() {
        return _delegate != null ? _delegate : getEventRecordingLogger();
    }

    private Logger getEventRecordingLogger() {
        if (eventRecodingLogger == null) {
            eventRecodingLogger = new EventRecodingLogger(this, eventList);
        }
        return eventRecodingLogger;
    }

    /**
     * Typically called after the {@link LoggerFactory} initialization phase is completed.
     * @param delegate
     */
    public void fixDelegate(Logger delegate) {
        this._delegate = delegate;
    }

    public boolean isDelegateEventAware() {

        return substituteLoggerExecutor.isDelegateEventAware();
    }

    public void log(LoggingEvent event) {
        if (substituteLoggerExecutor.isDelegateEventAware()) {
            try {
                logMethodCache.invoke(_delegate, event);
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            }
        }
    }

    public boolean isDelegateNOP() {
        return _delegate instanceof NOPLogger;
    }

    public Method takeLogMethodCache() {
        return logMethodCache;
    }

    public Logger get_delegate() {
        return _delegate;
    }

    public Boolean grabDelegateEventAware() {
        return delegateEventAware;
    }

    public void defineLogMethodCache(Method logMethodCache) {
        this.logMethodCache = logMethodCache;
    }

    public void assignDelegateEventAware(Boolean delegateEventAware) {
        this.delegateEventAware = delegateEventAware;
    }
}
