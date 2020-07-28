package com.ansill.utility.lambda;

@FunctionalInterface
public interface RunnableWithException{

  void run() throws Exception;

}
