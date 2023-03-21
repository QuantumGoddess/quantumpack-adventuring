package net.quantumgoddess.adventuring.entity.decoration;

import java.util.Optional;
import java.util.OptionalInt;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.quantumgoddess.adventuring.entity.QuantumEntityType;
import net.quantumgoddess.adventuring.item.QuantumItems;
import net.quantumgoddess.adventuring.recipe.DryingRackRecipe;
import net.quantumgoddess.adventuring.recipe.QuantumRecipeType;

public class DryingRackEntity extends AbstractDecorationEntity {
    private static final Logger ITEM_FRAME_LOGGER = LogUtils.getLogger();
    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(DryingRackEntity.class,
            TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> ROTATION = DataTracker.registerData(DryingRackEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    public static final int field_30454 = 8;
    private float itemDropChance = 1.0f;
    private boolean fixed;

    private final RecipeManager.MatchGetter<Inventory, DryingRackRecipe> matchGetter = RecipeManager
            .createCachedMatchGetter(QuantumRecipeType.DRYING_RACK);
    private int dryingTime;
    private int dryingTotalTime;

    public DryingRackEntity(EntityType<? extends DryingRackEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>) entityType, world);
    }

    public DryingRackEntity(World world, BlockPos pos, Direction facing) {
        this(QuantumEntityType.DRYING_RACK, world, pos, facing);
    }

    public DryingRackEntity(EntityType<? extends DryingRackEntity> type, World world, BlockPos pos, Direction facing) {
        super(type, world, pos);
        this.setFacing(facing);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            boolean bl = false;

            SimpleInventory inventory;
            ItemStack itemStack2;
            ItemStack itemStack = this.getHeldItemStack();
            if (itemStack.isEmpty() || getRecipeFor(itemStack) == null)
                return;
            bl = true;
            this.dryingTime = this.dryingTime + 1;

            if (this.dryingTime < this.dryingTotalTime
                    || !(itemStack2 = this.matchGetter.getFirstMatch(inventory = new SimpleInventory(itemStack), world)
                            .map(recipe -> recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack))
                            .isItemEnabled(world.getEnabledFeatures()))
                return;
            this.setHeldItemStack(itemStack2);
            world.updateListeners(getBlockPos(), getBlockStateAtPos(), getBlockStateAtPos(), Block.NOTIFY_ALL);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), GameEvent.Emitter.of(getBlockStateAtPos()));

            if (bl) {
                markDirty(world, getBlockPos(), getBlockStateAtPos());
            }
        }

    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.0f;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
        this.getDataTracker().startTracking(ROTATION, 0);
    }

    @Override
    protected void setFacing(Direction facing) {
        Validate.notNull(facing);
        this.facing = facing;
        if (facing.getAxis().isHorizontal()) {
            this.setPitch(0.0f);
            this.setYaw(this.facing.getHorizontal() * 90);
        } else {
            this.setPitch(-90 * facing.getDirection().offset());
            this.setYaw(0.0f);
        }
        this.prevPitch = this.getPitch();
        this.prevYaw = this.getYaw();
        this.updateAttachmentPosition();
    }

    @Override
    protected void updateAttachmentPosition() {
        if (this.facing == null) {
            return;
        }
        double d = 0.46875;
        double e = (double) this.attachmentPos.getX() + 0.5 - (double) this.facing.getOffsetX() * 0.46875;
        double f = (double) this.attachmentPos.getY() + 0.5 - (double) this.facing.getOffsetY() * 0.46875;
        double g = (double) this.attachmentPos.getZ() + 0.5 - (double) this.facing.getOffsetZ() * 0.46875;
        this.setPos(e, f, g);
        double h = this.getWidthPixels();
        double i = this.getHeightPixels();
        double j = this.getWidthPixels();
        Direction.Axis axis = this.facing.getAxis();
        switch (axis) {
            case X: {
                h = 1.0;
                break;
            }
            case Y: {
                i = 1.0;
                break;
            }
            case Z: {
                j = 1.0;
            }
        }
        this.setBoundingBox(
                new Box(e - (h /= 32.0), f + (4.5 / 16.0) - (i /= 32.0), g - (j /= 32.0), e + h, f + (4.5 / 16.0) + i,
                        g + j));
    }

    @Override
    public boolean canStayAttached() {
        if (this.fixed) {
            return true;
        }
        if (!this.world.isSpaceEmpty(this)) {
            return false;
        }
        BlockState blockState = this.world.getBlockState(this.attachmentPos.offset(this.facing.getOpposite()));
        if (!(blockState.getMaterial().isSolid()
                || this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState))) {
            return false;
        }
        return this.world.getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (!this.fixed) {
            super.move(movementType, movement);
        }
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        if (!this.fixed) {
            super.addVelocity(deltaX, deltaY, deltaZ);
        }
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    public void kill() {
        this.removeFromFrame(this.getHeldItemStack());
        super.kill();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.fixed) {
            if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.isSourceCreativePlayer()) {
                return super.damage(source, amount);
            }
            return false;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!source.isIn(DamageTypeTags.IS_EXPLOSION) && !this.getHeldItemStack().isEmpty()) {
            if (!this.world.isClient) {
                this.dropHeldStack(source.getAttacker(), false);
                this.emitGameEvent(GameEvent.BLOCK_CHANGE, source.getAttacker());
                this.playSound(this.getRemoveItemSound(), 1.0f, 1.0f);
            }
            return true;
        }
        return super.damage(source, amount);
    }

    public SoundEvent getRemoveItemSound() {
        return SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public int getWidthPixels() {
        return 16;
    }

    @Override
    public int getHeightPixels() {
        return 3;
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = 16.0;
        return distance < (d *= 64.0 * DryingRackEntity.getRenderDistanceMultiplier()) * d;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        this.playSound(this.getBreakSound(), 1.0f, 1.0f);
        this.dropHeldStack(entity, true);
        this.emitGameEvent(GameEvent.BLOCK_CHANGE, entity);
    }

    public SoundEvent getBreakSound() {
        return SoundEvents.ENTITY_ITEM_FRAME_BREAK;
    }

    @Override
    public void onPlace() {
        this.playSound(this.getPlaceSound(), 1.0f, 1.0f);
    }

    public SoundEvent getPlaceSound() {
        return SoundEvents.ENTITY_ITEM_FRAME_PLACE;
    }

    private void dropHeldStack(@Nullable Entity entity, boolean alwaysDrop) {
        if (this.fixed) {
            return;
        }
        ItemStack itemStack = this.getHeldItemStack();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (entity == null) {
                this.removeFromFrame(itemStack);
            }
            return;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (playerEntity.getAbilities().creativeMode) {
                this.removeFromFrame(itemStack);
                return;
            }
        }
        if (alwaysDrop) {
            this.dropStack(this.getAsItemStack());
        }
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            this.removeFromFrame(itemStack);
            if (this.random.nextFloat() < this.itemDropChance) {
                this.dropStack(itemStack);
            }
        }
    }

    private void removeFromFrame(ItemStack itemStack) {
        this.getMapId().ifPresent(i -> {
            MapState mapState = FilledMapItem.getMapState(i, this.world);
            if (mapState != null) {
                mapState.removeFrame(this.attachmentPos, this.getId());
                mapState.setDirty(true);
            }
        });
        itemStack.setHolder(null);
    }

    public ItemStack getHeldItemStack() {
        return this.getDataTracker().get(ITEM_STACK);
    }

    public OptionalInt getMapId() {
        Integer integer;
        ItemStack itemStack = this.getHeldItemStack();
        if (itemStack.isOf(Items.FILLED_MAP) && (integer = FilledMapItem.getMapId(itemStack)) != null) {
            return OptionalInt.of(integer);
        }
        return OptionalInt.empty();
    }

    public boolean containsMap() {
        return this.getMapId().isPresent();
    }

    public void setHeldItemStack(ItemStack stack) {
        this.setHeldItemStack(stack, true);
    }

    public void setHeldItemStack(ItemStack value, boolean update) {
        if (!value.isEmpty()) {
            value = value.copy();
            value.setCount(1);
        }
        this.setAsStackHolder(value);
        this.getDataTracker().set(ITEM_STACK, value);
        if (!value.isEmpty()) {
            this.playSound(this.getAddItemSound(), 1.0f, 1.0f);
        }
        if (update && this.attachmentPos != null) {
            this.world.updateComparators(this.attachmentPos, Blocks.AIR);
        }
    }

    public SoundEvent getAddItemSound() {
        return SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM;
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        if (mappedIndex == 0) {
            return new StackReference() {

                @Override
                public ItemStack get() {
                    return DryingRackEntity.this.getHeldItemStack();
                }

                @Override
                public boolean set(ItemStack stack) {
                    DryingRackEntity.this.setHeldItemStack(stack);
                    return true;
                }
            };
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (data.equals(ITEM_STACK)) {
            this.setAsStackHolder(this.getHeldItemStack());
        }
    }

    private void setAsStackHolder(ItemStack stack) {
        if (!stack.isEmpty() && stack.getHolder() != this) {
            stack.setHolder(this);
        }
        this.updateAttachmentPosition();
    }

    public int getRotation() {
        return this.getDataTracker().get(ROTATION);
    }

    public void setRotation(int value) {
        this.setRotation(value, true);
    }

    private void setRotation(int value, boolean updateComparators) {
        this.getDataTracker().set(ROTATION, value % 8);
        if (updateComparators && this.attachmentPos != null) {
            this.world.updateComparators(this.attachmentPos, Blocks.AIR);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.getHeldItemStack().isEmpty()) {
            nbt.put("Item", this.getHeldItemStack().writeNbt(new NbtCompound()));
            nbt.putByte("ItemRotation", (byte) this.getRotation());
            nbt.putFloat("ItemDropChance", this.itemDropChance);
        }
        nbt.putByte("Facing", (byte) this.facing.getId());
        nbt.putBoolean("Invisible", this.isInvisible());
        nbt.putBoolean("Fixed", this.fixed);
        nbt.putByte("DryingTime", (byte) this.dryingTime);
        nbt.putByte("DryingTotalTime", (byte) this.dryingTotalTime);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        NbtCompound nbtCompound = nbt.getCompound("Item");
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            ItemStack itemStack2;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (itemStack.isEmpty()) {
                ITEM_FRAME_LOGGER.warn("Unable to load item from: {}", (Object) nbtCompound);
            }
            if (!(itemStack2 = this.getHeldItemStack()).isEmpty() && !ItemStack.areEqual(itemStack, itemStack2)) {
                this.removeFromFrame(itemStack2);
            }
            this.setHeldItemStack(itemStack, false);
            this.setRotation(nbt.getByte("ItemRotation"), false);
            if (nbt.contains("ItemDropChance", NbtElement.NUMBER_TYPE)) {
                this.itemDropChance = nbt.getFloat("ItemDropChance");
            }
        }
        this.setFacing(Direction.byId(nbt.getByte("Facing")));
        this.setInvisible(nbt.getBoolean("Invisible"));
        this.fixed = nbt.getBoolean("Fixed");

        if (nbt.contains("DryingTime", NbtElement.INT_TYPE)) {
            this.dryingTime = nbt.getByte("DryingTime");

        }
        if (nbt.contains("DryingTotalTime", NbtElement.INT_TYPE)) {
            this.dryingTotalTime = nbt.getByte("DryingTotalTime");
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        boolean bl2;
        ItemStack itemStack = player.getStackInHand(hand);
        boolean bl = !this.getHeldItemStack().isEmpty();
        boolean bl3 = bl2 = !itemStack.isEmpty();
        if (this.fixed) {
            return ActionResult.PASS;
        }
        if (this.world.isClient) {
            return bl || bl2 ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        if (!bl) {
            if (bl2 && !this.isRemoved()) {
                Optional<DryingRackRecipe> optional;
                if ((optional = this.getRecipeFor(itemStack = player.getStackInHand(hand))).isPresent()) {
                    this.dryingTotalTime = optional.get().getCookTime();
                    this.dryingTime = 0;
                    this.setHeldItemStack(itemStack);
                    this.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
                    this.updateListeners();

                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                }

            }
        }
        return ActionResult.CONSUME;
    }

    public Optional<DryingRackRecipe> getRecipeFor(ItemStack stack) {
        if (!this.getHeldItemStack().equals(ItemStack.EMPTY)) {
            return Optional.empty();
        }
        return this.matchGetter.getFirstMatch(new SimpleInventory(stack), this.world);
    }

    private void updateListeners() {
        this.markDirty();
        world.updateListeners(getBlockPos(), getBlockStateAtPos(), getBlockStateAtPos(), Block.NOTIFY_ALL);
    }

    public void markDirty() {
        if (this.world != null) {
            markDirty(this.world, this.getBlockPos(), this.getBlockStateAtPos());
        }
    }

    public SoundEvent getRotateItemSound() {
        return SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM;
    }

    public int getComparatorPower() {
        if (this.getHeldItemStack().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setFacing(Direction.byId(packet.getEntityData()));
    }

    @Override
    public ItemStack getPickBlockStack() {
        ItemStack itemStack = this.getHeldItemStack();
        if (itemStack.isEmpty()) {
            return this.getAsItemStack();
        }
        return itemStack.copy();
    }

    protected ItemStack getAsItemStack() {
        return new ItemStack(QuantumItems.DRYING_RACK);
    }

    @Override
    public float getBodyYaw() {
        Direction direction = this.getHorizontalFacing();
        int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
        return MathHelper.wrapDegrees(180 + direction.getHorizontal() * 90 + this.getRotation() * 45 + i);
    }
}
