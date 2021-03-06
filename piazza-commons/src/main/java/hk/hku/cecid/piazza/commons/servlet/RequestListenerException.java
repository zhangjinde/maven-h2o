/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.servlet;

/**
 * RequestListenerException represents all kinds of exception related to
 * RequestListener.
 * 
 * @author Hugo Y. K. Lam
 */
public class RequestListenerException extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of RequestListenerException.
     */
    public RequestListenerException() {
        super();
    }

    /**
     * Creates a new instance of RequestListenerException.
     * 
     * @param message the error message.
     */
    public RequestListenerException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of RequestListenerException.
     * 
     * @param cause the cause of this exception.
     */
    public RequestListenerException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of RequestListenerException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public RequestListenerException(String message, Throwable cause) {
        super(message, cause);
    }
}