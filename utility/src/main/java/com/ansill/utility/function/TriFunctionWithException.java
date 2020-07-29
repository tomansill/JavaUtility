package com.ansill.utility.function;

@FunctionalInterface
public interface TriFunctionWithException<A, B, C, R>{
  R apply(A a, B b, C c) throws Exception;
}
