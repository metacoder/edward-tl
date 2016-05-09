package de.metacoder.edwardthreadlocal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AsciiArt {


  public static void main(String[] args) {
    show();
  }

  public static void show() {
    try {

      final InputStream is = AsciiArt.class.getResourceAsStream("/asciiart.txt");
      final char[] buffer = new char[1024];
      final StringBuilder out = new StringBuilder();
      try(Reader in = new InputStreamReader(is, "UTF-8")) {

        int read = 0;

        while((read = in.read(buffer, 0, buffer.length)) != -1) {
          out.append(buffer, 0, read);
        }
      }
      System.out.println(out.toString());
    } catch(Exception e) {
      e.printStackTrace();
      // a broken ascii logo doesn't matter.
    }
  }

}
