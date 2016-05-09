package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
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
      mv.visitFieldInsn(Opcodes.GETSTATIC, "de/metacoder/edwardthreadlocal/EventBridgeHolder", "INSTANCE", "Lde/metacoder/edwardthreadlocal/EventBridge;");
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitVarInsn(Opcodes.ALOAD, 1);
      mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "de/metacoder/edwardthreadlocal/EventBridge", "trackSet", "(Ljava/lang/ThreadLocal;Ljava/lang/Object;)V", true);
    }, cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_REMOVE_METHOD =
    cv -> new MethodConsumingClassVisitor("remove", "change-ThreadLocal-remove", mv -> {
      mv.visitFieldInsn(Opcodes.GETSTATIC, "de/metacoder/edwardthreadlocal/EventBridgeHolder", "INSTANCE", "Lde/metacoder/edwardthreadlocal/EventBridge;");
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "de/metacoder/edwardthreadlocal/EventBridge", "trackRemove", "(Ljava/lang/ThreadLocal;)V", true);
    }, cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_THREAD_LOCAL_CLASS =
    MODIFY_SET_METHOD.andThen(MODIFY_REMOVE_METHOD);

  private static byte[] loadThreadLocalClassBytecode() {
    return IOUtils.loadResourceBytes("/java/lang/ThreadLocal.class");
  }
}
