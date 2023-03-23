package net.quantumgoddess.adventuring.item;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents.AllowBed;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents.AllowSettingSpawn;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents.ModifySleepingDirection;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepFailureReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SleepingBagItem extends Item implements ModifySleepingDirection, AllowBed, AllowSettingSpawn {

    public SleepingBagItem(Settings settings) {
        super(settings.maxDamageIfAbsent(50));
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(this);
        EntitySleepEvents.ALLOW_BED.register(this);
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register(this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.CONSUME;
        }
        if (!BedBlock.isBedWorking(world)) {
            Vec3d vec3d = context.getBlockPos().toCenterPos();
            world.createExplosion(null, world.getDamageSources().badRespawnPoint(vec3d), null,
                    vec3d, 5.0f, true,
                    World.ExplosionSourceType.BLOCK);
            return ActionResult.SUCCESS;
        }
        BlockPos sleepingPos = world.getBlockState(context.getBlockPos()).isFullCube(world, context.getBlockPos())
                ? context.getBlockPos().up()
                : context.getBlockPos();
        Either<SleepFailureReason, Unit> result = context.getPlayer().trySleep(sleepingPos)
                .ifLeft(reason -> {
                    if (reason.getMessage() != null) {
                        context.getPlayer().sendMessage(reason.getMessage(), true);
                    }
                }).ifRight(unit -> {
                    context.getStack().damage(1, context.getPlayer(),
                            e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                });
        if (result.right().isPresent())
            return ActionResult.SUCCESS;
        return ActionResult.PASS;

    }

    @Override
    public @Nullable Direction modifySleepDirection(LivingEntity entity, BlockPos sleepingPos,
            @Nullable Direction sleepingDirection) {
        if (entity.getStackInHand(entity.getActiveHand()).isOf(this))
            return entity.getHorizontalFacing();
        return sleepingDirection;
    }

    @Override
    public ActionResult allowBed(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        if (entity.getStackInHand(entity.getActiveHand()).isOf(this))
            return ActionResult.SUCCESS;
        return ActionResult.PASS;
    }

    @Override
    public boolean allowSettingSpawn(PlayerEntity player, BlockPos sleepingPos) {
        if (player.getStackInHand(player.getActiveHand()).isOf(this))
            return false;
        return true;
    }

}
