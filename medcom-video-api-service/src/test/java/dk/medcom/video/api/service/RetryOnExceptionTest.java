package dk.medcom.video.api.service;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class RetryOnExceptionTest {
    @Test
    public void testSuccessFirstTry() throws Exception {
        var mock = Mockito.mock(TestInterface.class);
        Mockito.when(mock.method()).thenReturn("some value");

        var result = RetryOnException.retry(3, ArrayIndexOutOfBoundsException.class, mock::method);
        assertNotNull(result);
        assertEquals("some value", result);

        Mockito.verify(mock, times(1)).method();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testSuccessRetry() throws Exception {
        var mock = Mockito.mock(TestInterface.class);
        Mockito.when(mock.method()).thenThrow(ArrayIndexOutOfBoundsException.class)
                                   .thenThrow(ArrayIndexOutOfBoundsException.class)
                                   .thenReturn("some value");

        var result = RetryOnException.retry(3, ArrayIndexOutOfBoundsException.class, mock::method);
        assertNotNull(result);
        assertEquals("some value", result);

        Mockito.verify(mock, times(3)).method();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testSuccessRetryNested() throws Exception {
        var mock = Mockito.mock(TestInterface.class);

        var someOtherCustomException = new SomeOtherCustomException();
        var otherCustomException = new OtherCustomException(someOtherCustomException);
        var exceptionThrown = new CustomException(otherCustomException);

        Mockito.when(mock.method()).thenThrow(exceptionThrown)
                .thenThrow(exceptionThrown)
                .thenReturn("some value");

        var result = RetryOnException.retry(3, OtherCustomException.class, mock::method);
        assertNotNull(result);
        assertEquals("some value", result);

        Mockito.verify(mock, times(3)).method();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testFailRetry() {
        var mock = Mockito.mock(TestInterface.class);

        var someOtherCustomException = new SomeOtherCustomException();
        var otherCustomException = new OtherCustomException(someOtherCustomException);
        var exceptionThrown = new CustomException(otherCustomException);

        Mockito.when(mock.method()).thenThrow(exceptionThrown)
                .thenThrow(exceptionThrown)
                .thenThrow(exceptionThrown);

        assertThrows(CustomException.class, () -> RetryOnException.retry(3, CustomException.class, mock::method));

        Mockito.verify(mock, times(3)).method();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testThrowsException() throws Exception {
        var mock = Mockito.mock(TestInterface.class);

        var someOtherCustomException = new SomeOtherCustomException();
        var otherCustomException = new OtherCustomException(someOtherCustomException);
        var exceptionThrown = new CustomException(otherCustomException);

        Mockito.when(mock.method()).thenThrow(exceptionThrown);

        assertThrows(CustomException.class, () -> RetryOnException.retry(3, ArrayIndexOutOfBoundsException.class, mock::method));

        Mockito.verify(mock, times(1)).method();
        Mockito.verifyNoMoreInteractions(mock);
    }

    public interface TestInterface {
        String method();
    }

    public static class CustomException extends RuntimeException {
        public CustomException(Throwable t) {
            super(t);
        }
    }

    public static class OtherCustomException extends RuntimeException {
        public OtherCustomException(Throwable t) {
            super(t);
        }
    }

    public static class SomeOtherCustomException extends RuntimeException {
        public SomeOtherCustomException(Throwable t) {
            super(t);
        }

        public SomeOtherCustomException() {
            // Empty
        }
    }
}
