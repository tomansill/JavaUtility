package com.ansill.utility.function;

@FunctionalInterface
public interface BiFunctionWithException<A, B, R>{
  R apply(A a, B b) throws Exception;
}
