package arson.lynn.cat.enchantment;

import arson.lynn.cat.item.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MagicalPower extends Enchantment {
    public MagicalPower(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() == ModItems.VELOCITY_STAFF;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

}
