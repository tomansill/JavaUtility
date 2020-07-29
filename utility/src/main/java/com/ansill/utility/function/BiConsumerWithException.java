package com.ansill.utility.function;

@FunctionalInterface
public interface BiConsumerWithException<A, B>{
  void accept(A a, B b) throws Exception;
}
