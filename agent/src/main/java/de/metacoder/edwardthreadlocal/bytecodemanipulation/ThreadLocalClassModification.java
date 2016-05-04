package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import de.metacoder.edwardthreadlocal.org.objectweb.asm.ClassVisitor;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.Opcodes;
import de.metacoder.edwardthreadlocal.util.IOUtils;
import de.metacoder.edwardthreadlocal.util.InstrumentationUtils;

import java.lang.instrument.Instrumentation;
import java.util.function.Function;

public final class ThreadLocalClassModification {
  private ThreadLocalClassModification() {
    throw new UnsupportedOperationException();
  }

  public static void apply(Instrumentation inst) {
    InstrumentationUtils.patchAlreadyLoadedClass("java.lang.ThreadLocal", getPatchedThreadLocalBytes(), inst);
  }

  private static byte[] getPatchedThreadLocalBytes() {
    return InstrumentationUtils.modifyBytecode(loadThreadLocalClassBytecode(), MODIFY_THREAD_LOCAL_CLASS);
  }

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_SET_METHOD =
    cv -> new MethodConsumingClassVisitor("set", "change-ThreadLocal-set", mv -> {
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitVarInsn(Opcodes.ALOAD, 1);
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "de/metacoder/edwardthreadlocal/TraceReceiver", "trackSet", "(Ljava/lang/ThreadLocal;Ljava/lang/Object;)V", false);
    }, cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_REMOVE_METHOD =
    cv -> new MethodConsumingClassVisitor("remove", "change-ThreadLocal-remove", mv -> {
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "de/metacoder/edwardthreadlocal/TraceReceiver", "trackRemove", "(Ljava/lang/ThreadLocal;)V", false);
    }, cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_THREAD_LOCAL_CLASS =
    MODIFY_SET_METHOD.andThen(MODIFY_REMOVE_METHOD);

  private static byte[] loadThreadLocalClassBytecode() {
    return IOUtils.loadResourceBytes("/java/lang/ThreadLocal.class");
  }
}
