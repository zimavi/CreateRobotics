package com.workert.robotics.entities;

import com.workert.robotics.entities.goals.MineBlockAndDropGoal;
import com.workert.robotics.entities.goals.RobotFollowPlayerOwnerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class Miner extends AbstractRobotEntity implements InventoryCarrier {

    private final SimpleContainer inventory = new SimpleContainer(36);

    public Miner(EntityType<? extends PathfinderMob> entity, Level world) {
        super(entity, world);
    }


    @Override
    public Container getInventory() {
        return this.inventory;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.put("Inventory", this.inventory.createTag());
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.inventory.fromTag(pCompound.getList("Inventory", 10));
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (this.wantsToPickUp(itemstack)) {
            this.onItemPickup(pItemEntity);
            ItemStack itemstack1 = this.inventory.addItem(itemstack);
            this.take(pItemEntity, 64 - itemstack1.getCount());
            if (itemstack1.isEmpty()) {
                pItemEntity.discard();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        player.openMenu(new SimpleMenuProvider(new MenuConstructor() {

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                return new ChestMenu(MenuType.GENERIC_9x4, id, playerInventory, Miner.this.inventory, 1);
            }
        }, this.getDisplayName()));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean wantsToPickUp(ItemStack pStack) {
        return this.inventory.canAddItem(pStack);
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

}
