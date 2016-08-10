package net.jodah.failsafe.issues;

import java.util.concurrent.Callable;

import org.testng.annotations.Test;

import net.jodah.failsafe.Asserts;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedConsumer;

@Test
public class Issue48 {
  static class MyCustomException extends RuntimeException {
    MyCustomException() {
    }

    MyCustomException(Throwable cause) {
      super(cause);
    }
  }

  public void test() {
    Asserts.assertThrows(() -> Failsafe.with(new RetryPolicy().withMaxRetries(0)).withFallback(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        throw new MyCustomException();
      }
    }).get(() -> {
      throw new IllegalStateException();
    }), FailsafeException.class, MyCustomException.class);

    Asserts.assertThrows(
        () -> Failsafe.with(new RetryPolicy().withMaxRetries(0)).withFallback(new CheckedConsumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            throw new MyCustomException(throwable);
          }
        }).get(() -> {
          throw new IllegalStateException();
        }), FailsafeException.class, MyCustomException.class, IllegalStateException.class);
  }
}
