package net.blay09.mods.trashslot.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GuiContainerClassTransformer implements IClassTransformer {

    public static final Logger logger = LogManager.getLogger();

    public static final String OBF_CLASS = "byl";
    public static final String MCP_CLASS = "net.minecraft.client.gui.inventory.GuiContainer";
    public static final String OBF_METHOD_MOVEDORUP = "func_146286_b";
    public static final String MCP_METHOD_MOVEDORUP = "mouseReleased";
    public static final String OBF_METHOD_CLICKED = "func_73864_a";
    public static final String MCP_METHOD_CLICKED = "mouseClicked";
    private static final String METHOD_DESC = "(III)V";

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
        String methodNameMovedOrUp;
        String methodNameClicked;
        if(className.equals(OBF_CLASS)) {
            methodNameMovedOrUp = OBF_METHOD_MOVEDORUP;
            methodNameClicked = OBF_METHOD_CLICKED;
        } else if(className.equals(MCP_CLASS)) {
            methodNameMovedOrUp = MCP_METHOD_MOVEDORUP;
            methodNameClicked = MCP_METHOD_CLICKED;
        } else {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        for(MethodNode method : classNode.methods) {
            if(method.name.equals(methodNameMovedOrUp) && method.desc.equals(METHOD_DESC)) {
                logger.info("TrashSlot will now patch {} in {}...", methodNameMovedOrUp, className);
                MethodNode mn = new MethodNode();
                mn.visitVarInsn(Opcodes.ILOAD, 7); // push flag
                mn.visitVarInsn(Opcodes.ILOAD, 1); // push mouseX
                mn.visitVarInsn(Opcodes.ILOAD, 2); // push mouseY
                mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/trashslot/TrashSlot", "canDropStack", "(ZII)Z", false);
                mn.visitVarInsn(Opcodes.ISTORE, 7); // pop into flag1
                AbstractInsnNode insertBefore = null;
                for(int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode node = method.instructions.get(i);
                    if(node instanceof VarInsnNode) {
                        if(node.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) node).var == 7) {
                            insertBefore = node;
                            break;
                        }
                    }
                }
                if(insertBefore != null) {
                    method.instructions.insert(insertBefore, mn.instructions);
                    logger.info("TrashSlot successfully patched {} in {}!", methodNameMovedOrUp, className);
                } else {
                    logger.warn("TrashSlot failed to patch {0}::{1} ({2} not found) - items will drop when being taken out of the trash slot!", className, methodNameMovedOrUp, "ISTORE 7");
                }
            } else if(method.name.equals(methodNameClicked) && method.desc.equals(METHOD_DESC)) {
                logger.info("TrashSlot will now patch {} in {}...", methodNameClicked, className);
                MethodNode mn = new MethodNode();
                mn.visitVarInsn(Opcodes.ILOAD, 10); // push flag1
                mn.visitVarInsn(Opcodes.ILOAD, 1); // push mouseX
                mn.visitVarInsn(Opcodes.ILOAD, 2); // push mouseY
                mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/trashslot/TrashSlot", "canDropStack", "(ZII)Z", false);
                mn.visitVarInsn(Opcodes.ISTORE, 10); // pop into flag1
                AbstractInsnNode insertBefore = null;
                for(int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode node = method.instructions.get(i);
                    if(node instanceof VarInsnNode) {
                        if(node.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) node).var == 10) {
                            insertBefore = node;
                            break;
                        }
                    }
                }
                if(insertBefore != null) {
                    method.instructions.insert(insertBefore, mn.instructions);
                    logger.info("TrashSlot successfully patched {} in {}!", methodNameClicked, className);
                } else {
                    logger.warn("TrashSlot failed to patch {0}::{1} ({2} not found) - items will drop when being taken out of the trash slot!", className, methodNameClicked, "ISTORE 10");
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
