package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import de.metacoder.edwardthreadlocal.Configuration;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import de.metacoder.edwardthreadlocal.util.InstrumentationUtils;

import java.lang.instrument.Instrumentation;
import java.util.function.Function;

import static de.metacoder.edwardthreadlocal.util.logging.Log.fine;
import static de.metacoder.edwardthreadlocal.util.logging.Log.infoAround;

public class TestMainClassModification {
  public static void apply(Instrumentation inst) {
    inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
      if(Configuration.entryPointClassName().equals(className)) {
        return infoAround(Configuration.entryPointClassName(), () -> modifyTestMainBytecode(classfileBuffer));
      } else {
        fine("<TestMainClassModification> Skipping " + className);
        return classfileBuffer;
      }
    });
  }

  private static byte[] modifyTestMainBytecode(byte[] testMainBytecode) {
    return InstrumentationUtils.modifyBytecode(testMainBytecode, MODIFY_TEST_MAIN_CLASS);
  }

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_AFTER_BL_METHOD =
    cv -> new MethodConsumingClassVisitor(Configuration.afterMethodName(), "change-after-method", mv -> {
      mv.visitFieldInsn(Opcodes.GETSTATIC, "de/metacoder/edwardthreadlocal/EventBridgeHolder", "INSTANCE", "Lde/metacoder/edwardthreadlocal/EventBridge;");
      mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "de/metacoder/edwardthreadlocal/EventBridge", "deactivateTracingForThread", "()V", true);
    }
      , cv);

  // TODO what happens if before is being instrumented but after not? Memleak? 

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_BEFORE_BL_METHOD =
    cv -> new MethodConsumingClassVisitor(Configuration.beforeMethodName(), "change-before-method", mv -> {
      mv.visitFieldInsn(Opcodes.GETSTATIC, "de/metacoder/edwardthreadlocal/EventBridgeHolder", "INSTANCE", "Lde/metacoder/edwardthreadlocal/EventBridge;");
      mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "de/metacoder/edwardthreadlocal/EventBridge", "activateTracingForThread", "()V", true);
    }
      , cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_TEST_MAIN_CLASS =
    MODIFY_AFTER_BL_METHOD.andThen(MODIFY_BEFORE_BL_METHOD);
}
