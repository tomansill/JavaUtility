package com.ansill.utility.function;

@FunctionalInterface
public interface QuadFunctionWithException<A, B, C, D, R>{
  R apply(A a, B b, C c, D d) throws Exception;
}
