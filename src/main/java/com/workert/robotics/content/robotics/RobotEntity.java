package com.workert.robotics.content.robotics;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.workert.robotics.base.roboscript.RoboScript;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RobotEntity extends PathfinderMob implements InventoryCarrier {
	private static final int maxAir = BackTankUtil.maxAirWithoutEnchants() * 10;
	private int air;
	private final SimpleContainer inventory = new SimpleContainer(9);

	private final RoboScript roboScript;

	private String script = "";
	private String terminal = "";
	private boolean running = false;

	protected RobotEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.air = maxAir;
		this.roboScript = new RoboScript() {
			@Override
			public void print(String message) {
				RobotEntity.this.terminal = RobotEntity.this.terminal.concat(message + "\n");
			}

			@Override
			public void error(String error) {
				RobotEntity.this.terminal = RobotEntity.this.terminal.concat(error + "\n");
			}

			@Override
			protected void defineDefaultFunctions() {
				super.defineDefaultFunctions();
				this.defineFunction("getAir", 0, (interpreter, objects) -> RobotEntity.this.air);
			}
		};
	}


	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (pSource == DamageSource.OUT_OF_WORLD) return super.hurt(pSource, pAmount);
		this.air -= (int) pAmount * 2;
		return false;
	}

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		//TODO: Create and open a menu for drones
		return super.mobInteract(pPlayer, pHand);
	}

	@Override
	public SimpleContainer getInventory() {
		return this.inventory;
	}

	@Override
	public boolean wantsToPickUp(ItemStack pStack) {
		return this.inventory.canAddItem(pStack);
	}

	@Override
	public boolean canPickUpLoot() {
		return true;
	}

	@Override
	public boolean requiresCustomPersistence() {
		return true;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance pEffectInstance) {
		return false;
	}

	@Override
	public boolean canBeLeashed(Player pPlayer) {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public void animateHurt() {
	}

	@Override
	public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
		return false;
	}

	@Override
	public boolean displayFireAnimation() {
		return false;
	}
}
