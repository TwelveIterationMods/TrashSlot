package net.blay09.mods.trashslot.coremod;

import net.minecraft.inventory.Container;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

public class InventoryTweaksClassTransformer implements IClassTransformer {

    public static final Logger logger = LogManager.getLogger();

    public static final String OBF_CLASS = "ajb";
    public static final String MCP_CLASS = "net.minecraft.inventory.ContainerPlayer";

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {

        if(!className.equals(OBF_CLASS) && !className.equals(MCP_CLASS)) {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        logger.info("TrashSlot is now looking for Inventory Tweaks in {}", className);
        boolean found = false;
        for(MethodNode method : classNode.methods) {
            if(method.name.equals("invtweaks$slotMap")) {
                logger.info("TrashSlot will now patch {} in {} to fix Inventory Tweaks...", method.name, className);
                method.instructions.clear();
                method.visitVarInsn(Opcodes.ALOAD, 0);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/trashslot/coremod/InventoryTweaksClassTransformer", "fixInventoryTweaks", "(Lnet/minecraft/inventory/Container;)Ljava/util/Map;", false);
                method.visitInsn(Opcodes.ARETURN);
                found = true;
                break;
            }
        }
        if(!found) {
            logger.info("TrashSlot tried to fix Inventory Tweaks, but it's probably not installed.");
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static Map fixInventoryTweaks(Container container) {
        Map map = new HashMap<>();
        map.put(getContainerSection("CRAFTING_OUT"), container.inventorySlots.subList(0, 1));
        map.put(getContainerSection("CRAFTING_IN"), container.inventorySlots.subList(1, 5));
        map.put(getContainerSection("ARMOR"), container.inventorySlots.subList(5, 9));
        map.put(getContainerSection("INVENTORY"), container.inventorySlots.subList(9, 45));
        map.put(getContainerSection("INVENTORY_NOT_HOTBAR"), container.inventorySlots.subList(9, 36));
        map.put(getContainerSection("INVENTORY_HOTBAR"), container.inventorySlots.subList(36, 45));
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Enum getContainerSection(String name) {
        try {
            Class<? extends Enum> clazz = (Class<? extends Enum>) Class.forName("invtweaks.api.container.ContainerSection");
            for(Enum obj : clazz.getEnumConstants()) {
                if(obj.name().equals(name)) {
                    return obj;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
