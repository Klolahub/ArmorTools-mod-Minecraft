package com.klprod.mod;

import net.minecraft.item.*;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.NonNullList;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("klprod")
public class KlprodMod {
    public static final String MODID = "klprod";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final ItemGroup KLGROUP = new ItemGroup("klprod_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.EMERALD);
        }
    };

    public KlprodMod() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

        registerArmorSet("emerald", KlArmorMaterial.EMERALD);
        registerArmorSet("redstone", KlArmorMaterial.REDSTONE);
        registerArmorSet("lapis", KlArmorMaterial.LAPIS);

        registerToolSet("emerald", KlToolTier.EMERALD);
        registerToolSet("redstone", KlToolTier.REDSTONE);
        registerToolSet("lapis", KlToolTier.LAPIS);
    }

    enum KlArmorMaterial implements IArmorMaterial {
        EMERALD("emerald", 20, new int[]{3, 6, 8, 3}, 25, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.1F),
        REDSTONE("redstone", 15, new int[]{2, 5, 6, 2}, 30, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F),
        LAPIS("lapis", 18, new int[]{2, 6, 6, 2}, 22, SoundEvents.ARMOR_EQUIP_CHAIN, 1.5F, 0.05F);

        private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};

        private final String name;
        private final int durabilityMultiplier;
        private final int[] armorValues;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;

        KlArmorMaterial(String name, int durabilityMultiplier, int[] armorValues, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.armorValues = armorValues;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlotType slot) {
            return BASE_DURABILITY[slot.getIndex()] * this.durabilityMultiplier;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlotType slot) {
            return this.armorValues[slot.getIndex()];
        }

        @Override
        public int getEnchantmentValue() {
            return this.enchantability;
        }

        @Override
        public SoundEvent getEquipSound() {
            return this.equipSound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return MODID + ":" + this.name;
        }

        @Override
        public float getToughness() {
            return this.toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }

    enum KlToolTier implements IItemTier {
        EMERALD(3, 1500, 8.0F, 3.0F, 15, () -> Ingredient.of(Items.EMERALD)),
        REDSTONE(2, 600, 6.0F, 2.5F, 18, () -> Ingredient.of(Items.REDSTONE)),
        LAPIS(2, 800, 7.0F, 2.0F, 20, () -> Ingredient.of(Items.LAPIS_LAZULI));

        private final int level;
        private final int uses;
        private final float speed;
        private final float attackDamageBonus;
        private final int enchantability;
        private final Lazy<Ingredient> repairIngredient;

        KlToolTier(int level, int uses, float speed, float attackDamageBonus, int enchantability, Lazy<Ingredient> repairIngredient) {
            this.level = level;
            this.uses = uses;
            this.speed = speed;
            this.attackDamageBonus = attackDamageBonus;
            this.enchantability = enchantability;
            this.repairIngredient = repairIngredient;
        }

        @Override public int getUses() { return uses; }
        @Override public float getSpeed() { return speed; }
        @Override public float getAttackDamageBonus() { return attackDamageBonus; }
        @Override public int getLevel() { return level; }
        @Override public int getEnchantmentValue() { return enchantability; }
        @Override public Ingredient getRepairIngredient() { return repairIngredient.get(); }
    }

    static void registerArmorSet(String name, IArmorMaterial material) {
        ITEMS.register(name + "_helmet", () -> new ArmorItem(material, EquipmentSlotType.HEAD, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_chestplate", () -> new ArmorItem(material, EquipmentSlotType.CHEST, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_leggings", () -> new ArmorItem(material, EquipmentSlotType.LEGS, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_boots", () -> new ArmorItem(material, EquipmentSlotType.FEET, new Item.Properties().tab(KLGROUP)));
    }

    static void registerToolSet(String name, IItemTier tier) {
        ITEMS.register(name + "_sword", () -> new SwordItem(tier, 3, -2.4F, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_pickaxe", () -> new PickaxeItem(tier, 1, -2.8F, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_axe", () -> new AxeItem(tier, 5.0F, -3.0F, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_shovel", () -> new ShovelItem(tier, 1.5F, -3.0F, new Item.Properties().tab(KLGROUP)));
        ITEMS.register(name + "_hoe", () -> new HoeItem(tier, 0, -1.0F, new Item.Properties().tab(KLGROUP)));
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class ArmorEffects {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.player.level.isClientSide || event.phase != TickEvent.Phase.END) return;

            ItemStack head = event.player.getItemBySlot(EquipmentSlotType.HEAD);
            ItemStack chest = event.player.getItemBySlot(EquipmentSlotType.CHEST);
            ItemStack legs = event.player.getItemBySlot(EquipmentSlotType.LEGS);
            ItemStack feet = event.player.getItemBySlot(EquipmentSlotType.FEET);

            String set = null;
            if (head.getItem().getRegistryName().getPath().contains("emerald") &&
                    chest.getItem().getRegistryName().getPath().contains("emerald") &&
                    legs.getItem().getRegistryName().getPath().contains("emerald") &&
                    feet.getItem().getRegistryName().getPath().contains("emerald")) {
                set = "emerald";
            } else if (head.getItem().getRegistryName().getPath().contains("redstone") &&
                    chest.getItem().getRegistryName().getPath().contains("redstone") &&
                    legs.getItem().getRegistryName().getPath().contains("redstone") &&
                    feet.getItem().getRegistryName().getPath().contains("redstone")) {
                set = "redstone";
            } else if (head.getItem().getRegistryName().getPath().contains("lapis") &&
                    chest.getItem().getRegistryName().getPath().contains("lapis") &&
                    legs.getItem().getRegistryName().getPath().contains("lapis") &&
                    feet.getItem().getRegistryName().getPath().contains("lapis")) {
                set = "lapis";
            }

            if (set != null) {
                switch (set) {
                    case "emerald":
                        event.player.addEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 220, 0, false, false));
                        break;
                    case "redstone":
                        event.player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 220, 0, false, false));
                        break;
                    case "lapis":
                        event.player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, false, false));
                        break;
                }
            }
        }
    }
}
