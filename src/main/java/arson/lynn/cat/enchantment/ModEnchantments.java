package arson.lynn.cat.enchantment;

import arson.lynn.cat.CatUtilities;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static Enchantment MAGICAL_POWER = register("magical_power",
            new MagicalPower(Enchantment.Rarity.UNCOMMON,
                    EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(CatUtilities.MOD_ID, name), enchantment);
    }
    public static void registerModEnchantments() {
        System.out.println("Registering Enchantments for " + CatUtilities.MOD_ID);
    }
}
