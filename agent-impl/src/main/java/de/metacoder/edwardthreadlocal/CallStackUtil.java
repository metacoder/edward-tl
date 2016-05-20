package de.metacoder.edwardthreadlocal;

import java.util.Arrays;
import java.util.Iterator;

public class CallStackUtil {

  public static boolean threadLocalDirectlyCalledByJavaLangClass(StackTraceElement[] elements){
    return CallStackUtil.threadLocalDirectlyCalledByJavaLangClass(Arrays.asList(elements).iterator(), false);
  }

  private static boolean threadLocalDirectlyCalledByJavaLangClass(Iterator<StackTraceElement> it, boolean threadLocalStartFound){

    if(it.hasNext()){

      final StackTraceElement next = it.next();

      if(!threadLocalStartFound){

        if(next.getClassName().startsWith("java.lang.ThreadLocal")){
          return threadLocalDirectlyCalledByJavaLangClass(it, true);
        } else {
          return threadLocalDirectlyCalledByJavaLangClass(it, false);
        }

      } else {

        if(next.getClassName().startsWith("java.lang.ThreadLocal")) {
          return threadLocalDirectlyCalledByJavaLangClass(it, true);
        } else return next.getClassName().startsWith("java.lang");

      }

    }

    throw new IllegalStateException("Didn't find thread local call or caller of thread local call in stack, this mustn't happen!");
  }
}
