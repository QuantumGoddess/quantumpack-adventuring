package net.quantumgoddess.adventuring.client.render.entity;

import java.util.OptionalInt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.quantumgoddess.adventuring.client.render.model.DryingRackEntityModel;
import net.quantumgoddess.adventuring.client.render.model.QuantumEntityModelLayers;
import net.quantumgoddess.adventuring.entity.decoration.DryingRackEntity;

@Environment(value = EnvType.CLIENT)
public class DryingRackEntityRenderer<T extends DryingRackEntity>
        extends EntityRenderer<T> {
    private final DryingRackEntityModel<DryingRackEntity> model;
    private static final Identifier TEXTURE = new Identifier("textures/block/oak_planks.png");
    // private static final ModelIdentifier MODEL = new
    // ModelIdentifier("quantumadventuring", "drying_rack", "default");
    public static final int field_32933 = 30;
    private final ItemRenderer itemRenderer;
    private final BlockRenderManager blockRenderManager;

    public DryingRackEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderManager = context.getBlockRenderManager();
        this.model = new DryingRackEntityModel<DryingRackEntity>(context.getPart(QuantumEntityModelLayers.DRYING_RACK));
    }

    @Override
    public void render(T itemFrameEntity, float f, float g, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(itemFrameEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        Direction direction = ((AbstractDecorationEntity) itemFrameEntity).getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double) direction.getOffsetX() * 0.46875, (double) direction.getOffsetY() * 0.46875,
                (double) direction.getOffsetZ() * 0.46875);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(((Entity) itemFrameEntity).getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - ((Entity) itemFrameEntity).getYaw()));
        boolean bl = ((Entity) itemFrameEntity).isInvisible();
        ItemStack itemStack = ((DryingRackEntity) itemFrameEntity).getHeldItemStack();
        if (!bl) {

            matrixStack.push();
            // matrixStack.scale(-1.0f, -1.0f, 1.0f);
            // matrixStack.translate(-0.5f, -0.5f, -0.5f);
            this.model.setAngles(itemFrameEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            matrixStack.pop();
            // BakedModelManager bakedModelManager =
            // this.blockRenderManager.getModels().getModelManager();
            // matrixStack.push();
            // matrixStack.translate(-0.5f, -0.5f, -0.5f);
            // this.blockRenderManager.getModelRenderer().render(matrixStack.peek(),
            // vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()),
            // null,
            // bakedModelManager.getModel(MODEL), 1.0f, 1.0f, 1.0f, i,
            // OverlayTexture.DEFAULT_UV);
            // matrixStack.pop();
        }
        if (!itemStack.isEmpty()) {
            OptionalInt optionalInt = ((DryingRackEntity) itemFrameEntity).getMapId();
            if (bl) {
                matrixStack.translate(0.0f, 0.0f, 0.5f);
            } else {
                matrixStack.translate(0.0f, 0.0f, 0.4375f);
            }
            int j = optionalInt.isPresent() ? ((DryingRackEntity) itemFrameEntity).getRotation() % 4 * 2
                    : ((DryingRackEntity) itemFrameEntity).getRotation();
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) j * 360.0f / 8.0f));

            int l = this.getLight(itemFrameEntity, LightmapTextureManager.MAX_LIGHT_COORDINATE, i);
            matrixStack.scale(.75f, .75f, .75f);
            this.itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, l, OverlayTexture.DEFAULT_UV,
                    matrixStack, vertexConsumerProvider, ((DryingRackEntity) itemFrameEntity).world,
                    ((Entity) itemFrameEntity).getId());

        }
        matrixStack.pop();
    }

    private int getLight(T itemFrame, int glowLight, int regularLight) {
        return ((Entity) itemFrame).getType() == EntityType.GLOW_ITEM_FRAME ? glowLight : regularLight;
    }

    @Override
    public Vec3d getPositionOffset(T itemFrameEntity, float f) {
        return new Vec3d((float) ((AbstractDecorationEntity) itemFrameEntity).getHorizontalFacing().getOffsetX() * 0.3f,
                -0.25, (float) ((AbstractDecorationEntity) itemFrameEntity).getHorizontalFacing().getOffsetZ() * 0.3f);
    }

    @Override
    public Identifier getTexture(T itemFrameEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    protected boolean hasLabel(T itemFrameEntity) {
        if (!MinecraftClient.isHudEnabled() || ((DryingRackEntity) itemFrameEntity).getHeldItemStack().isEmpty()
                || !((DryingRackEntity) itemFrameEntity).getHeldItemStack().hasCustomName()
                || this.dispatcher.targetedEntity != itemFrameEntity) {
            return false;
        }
        double d = this.dispatcher.getSquaredDistanceToCamera((Entity) itemFrameEntity);
        float f = ((Entity) itemFrameEntity).isSneaky() ? 32.0f : 64.0f;
        return d < (double) (f * f);
    }

    @Override
    protected void renderLabelIfPresent(T itemFrameEntity, Text text, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i) {
        super.renderLabelIfPresent(itemFrameEntity, ((DryingRackEntity) itemFrameEntity).getHeldItemStack().getName(),
                matrixStack, vertexConsumerProvider, i);
    }
}
