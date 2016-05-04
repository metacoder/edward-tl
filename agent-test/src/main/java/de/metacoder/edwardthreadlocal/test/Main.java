package de.metacoder.edwardthreadlocal.test;

public class Main {

  private static final ThreadLocal<String> firstThreadLocal = new ThreadLocal<String>();
  private static final ThreadLocal<String> secondThreadLocal = new ThreadLocal<String>();

  public static void beforeBL() {
    System.out.println("beforebl");
  }

  public static void main(String[] args) {

    try {
      beforeBL();
            /*for(ThreadLocal<String> tl : Arrays.asList(firstThreadLocal, secondThreadLocal)){
                System.out.println("Hello World, writing to greeting to the first thread local");
                tl.set("Hello Felix");
                System.out.println("removing the thread local value");
                tl.remove();
                System.out.println("Removed =)");
            }
            */
    } finally {
      afterBL();
    }

  }


  public static void afterBL() {
    System.out.println("afterbl");
  }

}
