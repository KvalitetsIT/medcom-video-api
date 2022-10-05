package dk.medcom.video.api.service.impl;

import java.util.concurrent.Callable;

public class RetryOnException {
    /**
     * Retry the function when retryException happens. Try at maximum for retryCount.
     * @param retryCount Number of times to retry at maximum.
     * @param retryException Exception, or subclass of, to retry on.
     * @param callable Function to call.
     * @return Return value of function.
     * @param <R> Return type.
     * @throws Exception Any exception thrown by function.
     */
    public static <R> R retry(int retryCount, Class<? extends Exception> retryException, Callable<R> callable) throws Exception {
        for(int i = 0; i < retryCount; i++) {
            try {
                return callable.call();
            }
            catch(Exception e) {
                Throwable rootCause = e;
                boolean retryExceptionIsCause = retryException.isInstance(e);

                while (rootCause.getCause() != null && rootCause.getCause() != rootCause && !retryExceptionIsCause) {
                    rootCause = rootCause.getCause();
                    if(retryException.isInstance(rootCause)) {
                        retryExceptionIsCause = true;
                    }
                }

                if(retryExceptionIsCause) {
                    if(i >= retryCount-1) {
                        throw e;
                    }

                    Thread.sleep(100);
                }
                else {
                    throw e;
                }
            }
        }

        // Can this happen? Not really.
        return null;
    }
}
