package ru.t.kora.techempower.loom.agent;


import java.lang.classfile.*;
import java.lang.classfile.attribute.*;
import java.lang.classfile.instruction.*;
import java.lang.constant.ClassDesc;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public final class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        transform(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        transform(inst);
    }

    private static void transform(Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                return switch (className) {
                    case "com/zaxxer/hikari/util/ConcurrentBag" -> transformConcurrentBug(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    case "io/undertow/server/DefaultByteBufferPool" -> transformDefaultByteBufferPool(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    case "com/fasterxml/jackson/core/util/BufferRecyclers" -> transformBufferRecyclers(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    default -> classfileBuffer;
                };
            }

        });
    }

    private static byte[] transformBufferRecyclers(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // replace new ThreadLocal() with new CarrierThreadLocal()
        return ClassFile.of()
            .transform(
                ClassFile.of().parse(classfileBuffer),
                ClassTransform.transformingMethodBodies(
                    m -> m.methodName().equalsString("<clinit>"),
                    (builder, element) -> {
                        switch (element) {
                            case NewObjectInstruction no -> {
                                if (no.className().name().equalsString("java/lang/ThreadLocal")) {
                                    builder.new_(ClassDesc.of("jdk.internal.misc", "CarrierThreadLocal"));
                                    return;
                                }
                            }
                            case InvokeInstruction i -> {
                                if (i.opcode() == Opcode.INVOKESPECIAL && i.owner().name().equalsString("java/lang/ThreadLocal")) {
                                    builder.invokespecial(ClassDesc.of("jdk.internal.misc", "CarrierThreadLocal"), "<init>", i.typeSymbol());
                                    return;
                                }
                            }
                            default -> {}
                        }
                        builder.accept(element);
                    }
                )
            );
    }

    private static byte[] transformDefaultByteBufferPool(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // replace ThreadLocal.withInitial
        return classfileBuffer;
    }

    private static byte[] transformConcurrentBug(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return ClassFile.of()
            .transform(
                ClassFile.of().parse(classfileBuffer),
                ClassTransform.transformingMethodBodies(
                    method -> method.methodName().equalsString("<init>"),
                    (builder, element) -> {
                        if (element instanceof InvokeInstruction i && i.opcode() == Opcode.INVOKESTATIC) {
                            if (i.method().name().equalsString("withInitial") && i.owner().name().equalsString("java/lang/ThreadLocal")) {
                                builder.invokestatic(
                                    ClassDesc.of("ru.tinkoff.kora.techempower.loom.undertow.pool", "CarrierThreadLocalFix"),
                                    "withInitial",
                                    i.typeSymbol()
                                );
                                return;
                            }
                        }
                        builder.accept(element);
                    }
                )
            );
    }
}
