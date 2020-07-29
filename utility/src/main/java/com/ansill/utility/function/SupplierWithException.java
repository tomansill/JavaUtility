package com.ansill.utility.function;

@FunctionalInterface
public interface SupplierWithException<T>{

  T get() throws Exception;

}
