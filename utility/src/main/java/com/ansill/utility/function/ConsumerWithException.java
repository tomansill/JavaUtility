package com.ansill.utility.function;

@FunctionalInterface
public interface ConsumerWithException<A>{
  void accept(A a) throws Exception;
}
