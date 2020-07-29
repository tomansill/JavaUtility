package com.ansill.utility.function;

@FunctionalInterface
public interface FunctionWithException<A, R>{
  R apply(A a) throws Exception;
}
