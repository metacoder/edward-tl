package de.metacoder.edwardthreadlocal.util;

import de.metacoder.edwardthreadlocal.bytecodemanipulation.EdwardTLClassModifier;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.ClassReader;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.ClassVisitor;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.function.Function;

import static de.metacoder.edwardthreadlocal.util.logging.Log.*;

public final class InstrumentationUtils {
  private InstrumentationUtils() {
    throw new UnsupportedOperationException();
  }

  public static Class<?> findLoadedClass(String fullyQualifiedClassName, Instrumentation inst) {
    return infoAround("findLoadedClass(\"" + fullyQualifiedClassName + "\")", () -> {
      for(Class<?> cls : inst.getAllLoadedClasses())
        if(fullyQualifiedClassName.equals(cls.getCanonicalName())) {
          info("Found class");
          return cls;
        }
      warn("Class not found, returning null");
      return null;
    });
  }

  public static byte[] modifyBytecode(byte[] originalBytecode, Function<ClassVisitor, ClassVisitor> modifier) {
    ClassReader reader = new ClassReader(originalBytecode);
    ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
    reader.accept(modifier.apply(writer), ClassReader.SKIP_FRAMES);
    return writer.toByteArray();
  }

  public static void patchAlreadyLoadedClass(String fullyQualifiedClassName, byte[] patchedBytecode, Instrumentation inst) {
    infoBlock("patchAlreadyLoadedClass(\"" + fullyQualifiedClassName + "\", [" + patchedBytecode.length + " bytes])", () -> {
      Class<?> loadedClass = findLoadedClass(fullyQualifiedClassName, inst);
      try {
        inst.redefineClasses(new ClassDefinition(loadedClass, patchedBytecode));
      } catch(Exception cause) {
        throw new RuntimeException(cause);
      }
    });
  }
}
