package com.ansill.utility.lambda;

@FunctionalInterface
public interface TriFunctionWithException<A, B, C, R>{
  R apply(A a, B b, C c) throws Exception;
}
