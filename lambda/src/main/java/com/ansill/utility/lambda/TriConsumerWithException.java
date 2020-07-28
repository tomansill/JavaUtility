package com.ansill.utility.lambda;

@FunctionalInterface
public interface TriConsumerWithException<A, B, C>{
  void accept(A a, B b, C c) throws Exception;
}
