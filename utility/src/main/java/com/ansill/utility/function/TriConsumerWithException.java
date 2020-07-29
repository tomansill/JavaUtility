package com.ansill.utility.function;

@FunctionalInterface
public interface TriConsumerWithException<A, B, C>{
  void accept(A a, B b, C c) throws Exception;
}
