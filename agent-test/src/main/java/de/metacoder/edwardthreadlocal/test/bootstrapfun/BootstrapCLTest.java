package de.metacoder.edwardthreadlocal.test.bootstrapfun;

/**
 * Created by becker on 5/9/16.
 */
public class BootstrapCLTest {

  public static void main(String... args){
    System.out.println(BootstrapCLTest.class.getClassLoader().getParent().getParent());
  }

}
