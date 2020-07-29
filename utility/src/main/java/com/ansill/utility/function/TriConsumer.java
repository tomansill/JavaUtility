package com.ansill.utility.function;

@FunctionalInterface
public interface TriConsumer<A, B, C>{
  void accept(A a, B b, C c);
}
