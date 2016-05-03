package de.metacoder.edwardthreadlocal;

import de.metacoder.edwardthreadlocal.bytecodemanipulation.EdwardTLClassModifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class AgentEdward {

	public static void premain(String agentArgs, Instrumentation inst) throws Exception {

        final EdwardTLClassModifier classModifier  = new EdwardTLClassModifier();

        byte[] classBytesFromThreadLocal = loadTL();
        byte[] patchedBytes = classModifier.patchByteCode(classBytesFromThreadLocal);
        //byte[] patchedBytes = classBytesFromThreadLocal;

		AsciiArt.show();

        // TODO retransform/define here

        System.out.println("Patched byte code size is: " + patchedBytes.length);
        System.out.println("Loaded " + classBytesFromThreadLocal.length + " bytes from ThreadLocal.class");

        final JarFile agentJar = new JarFile(new File(TraceReceiver.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        System.out.println("Attaching agent jar " + agentJar.getName());
        inst.appendToBootstrapClassLoaderSearch(agentJar);


        for(Class<?> clazz: inst.getAllLoadedClasses()){
            if("java.lang.ThreadLocal".equals(clazz.getCanonicalName())) {
                System.out.println("Redefining thread local class!");
                ClassDefinition classDefinition = new ClassDefinition(clazz, patchedBytes);
                inst.redefineClasses(classDefinition);
                System.out.println("Already loaded: " + clazz.getCanonicalName());
            }
        }

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if("de/metacoder/edwardthreadlocal/test/Main".equals(className)){
                System.out.println("Class file transformer now transforming " + className);
                return new EdwardTLClassModifier().patchByteCode(classfileBuffer);
            }
            System.out.println("Class file transformer ignoring saw class " + className);
            return classfileBuffer;
        });

	}


    private static byte[] loadTL() throws Exception {
        InputStream is = String.class.getResourceAsStream("/java/lang/ThreadLocal.class");
        System.out.println("Got thread local is: " + is);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int bytesRead = 0;
        while((bytesRead = is.read(buffer)) != -1){
            out.write(buffer, 0, bytesRead);
        }

        return out.toByteArray();
    }

}
