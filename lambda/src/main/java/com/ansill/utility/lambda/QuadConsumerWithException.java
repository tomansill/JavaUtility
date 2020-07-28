package com.ansill.utility.lambda;

@FunctionalInterface
public interface QuadConsumerWithException<A, B, C, D>{
  void accept(A a, B b, C c, D d) throws Exception;
}
