package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import de.metacoder.edwardthreadlocal.org.objectweb.asm.*;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.commons.AdviceAdapter;

import static sun.misc.Version.println;

public class EdwardTLClassModifier  {
  public static byte[] patchByteCode(byte[] classByteCode) {
    try {
      final ClassReader reader = new ClassReader(classByteCode);
      final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

      reader.accept(new InstrumentingClassVisitor(writer, reader.getClassName()), ClassReader.SKIP_FRAMES);
      return writer.toByteArray();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }


  private static class InstrumentingClassVisitor extends ClassVisitor {

    private final String className;

    public InstrumentingClassVisitor(ClassVisitor cv, String className) {
      super(Opcodes.ASM5, cv);
      this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

      System.out.println("[MethodVisitor] Visiting method " + name + " in class " + className);

      if("de/metacoder/edwardthreadlocal/test/Main".equals(className)) {
        System.out.println("Returning MainClassTrackingMethodAdapter");
        return new MainClassTrackingMethodAdapter(mv, access, name, desc);
      }

      throw new IllegalStateException("THIS SHOULD NEVER HAPPEN :S"); // make the compiler happy
    }

    class MainClassTrackingMethodAdapter extends AdviceAdapter {
      private final String methodName;

      MainClassTrackingMethodAdapter(MethodVisitor delegate, int access, String name, String desc) {
        super(Opcodes.ASM5, delegate, access, name, desc);
        this.methodName = name;
      }

      @Override
      protected void onMethodEnter() {

        System.out.println("[MainClassTrackingMethodAdapter]: Entering method " + methodName);

        switch(methodName) {

          case "beforeBL":
            System.out.println("Visiting beforeBL (" + className + ") method now!");
            visitMethodInsn(Opcodes.INVOKESTATIC, "de/metacoder/edwardthreadlocal/TraceReceiver", "activateTracingForThread", "()V", false);
            break;

          case "afterBL":
            System.out.println("Visiting afterBL (" + className + ") method now!");
            visitMethodInsn(Opcodes.INVOKESTATIC, "de/metacoder/edwardthreadlocal/TraceReceiver", "deactivateTracingForThread", "()V", false);
            break;

          default:
            System.out.println("Ignoring Main method " + methodName);
        }
      }
    }


  }

}
