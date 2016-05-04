package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import de.metacoder.edwardthreadlocal.org.objectweb.asm.ClassVisitor;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.MethodVisitor;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.Opcodes;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.commons.AdviceAdapter;

import java.util.Objects;
import java.util.function.Consumer;

import static de.metacoder.edwardthreadlocal.util.logging.Log.*;

public class MethodConsumingClassVisitor extends ClassVisitor {
  private final Consumer<MethodVisitor> methodConsumer;
  private final String methodName;
  private final String shortDescription;

  public MethodConsumingClassVisitor(String methodName, String shortDescription, Consumer<MethodVisitor> methodConsumer, ClassVisitor delegate) {
    super(Opcodes.ASM5, delegate);
    this.methodConsumer = methodConsumer;
    this.methodName = methodName;
    this.shortDescription = shortDescription;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    if(Objects.equals(name, methodName)) return visitingTargettedMethod(access, name, desc, mv);
    else return visitingSomeOtherMethod(name, mv);
  }

  private MethodVisitor visitingTargettedMethod(final int access, final String name, final String desc, final MethodVisitor mv) {
    info("<" + shortDescription + "> Found method \"" + name + "\". Returning my advice adapter that calls methodConsumer.");
    return new AdviceAdapter(api, mv, access, name, desc) {
      @Override
      protected void onMethodEnter() {
        infoBlock(shortDescription + ".methodConsumer", () -> methodConsumer.accept(this));
      }
    };
  }

  private MethodVisitor visitingSomeOtherMethod(String name, MethodVisitor mv) {
    fine("<" + shortDescription + "> Visiting method \"" + name + "\". Not what I'm looking for.");
    return mv;
  }
}
