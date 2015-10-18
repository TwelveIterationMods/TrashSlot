package net.blay09.mods.trashslot.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MinecraftClassTransformer implements IClassTransformer {

    public static final Logger logger = LogManager.getLogger();

    public static final String OBF_CLASS = "bsu";
    public static final String MCP_CLASS = "net.minecraft.client.Minecraft";

    public static final String OBF_METHOD = "func_147112_ai";
    public static final String MCP_METHOD = "middleClickMouse";

    private static final String METHOD_DESC = "()V";

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
        String methodName;
        if(className.equals(OBF_CLASS)) {
            methodName = OBF_METHOD;
        } else if(className.equals(MCP_CLASS)) {
            methodName = MCP_METHOD;
        } else {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        for(MethodNode method : classNode.methods) {
            if(method.name.equals(methodName) && method.desc.equals(METHOD_DESC)) {
                logger.info("TrashSlot will now patch {} in {}...", methodName, className);
                AbstractInsnNode insertAfter = null;
                for(int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode node = method.instructions.get(i);
                    if(node instanceof VarInsnNode) {
                        if(node.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) node).var == 3) { // ISTORE 3
                            insertAfter = node;
                            break;
                        }
                    }
                }
                if(insertAfter != null) {
                    MethodNode mn = new MethodNode();
                    mn.visitIincInsn(3, -1); // IINC 3 -1
                    method.instructions.insert(insertAfter, mn.instructions);
                    logger.info("TrashSlot successfully patched {} in {}!", methodName, className);
                } else {
                    logger.warn("TrashSlot failed to patch {0}::{1} ({2} not found) - pick block in creative will be weird!", className, methodName, "ISTORE 3");
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
