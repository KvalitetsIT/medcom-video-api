package dk.medcom.video.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class RetryOnException {
    private static final Logger logger = LoggerFactory.getLogger(RetryOnException.class);

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
                logger.debug("Calling method.");
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
                        logger.info("Retried call {} times without success. Throwing exception.", retryCount);
                        throw e;
                    }

                    logger.info("Retrying call. Retry {} out of {}.", i+1, retryCount);
                    Thread.sleep(100);
                }
                else {
                    logger.debug("Exception not retryable. Throwing exception.");
                    throw e;
                }
            }
        }

        // Can this happen? Not really.
        return null;
    }
}
