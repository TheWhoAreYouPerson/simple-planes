package xyz.przemyk.simpleplanes.upgrades.shooter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import xyz.przemyk.simpleplanes.entities.furnacePlane.FurnacePlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesUpgrades;
import xyz.przemyk.simpleplanes.upgrades.Upgrade;

import java.util.Random;

import static net.minecraft.item.Items.FIREWORK_ROCKET;
import static net.minecraft.item.Items.FIRE_CHARGE;

public class ShooterUpgrade extends Upgrade {

    public static final ShooterModel SHOOTER_MODEL = new ShooterModel();
    //TODO: different texture
    public static final ResourceLocation TEXTURE = new ResourceLocation("simpleplanes", "textures/plane_upgrades/shooter.png");
    private boolean shootSide = false;
    public ShooterUpgrade(FurnacePlaneEntity planeEntity) {
        super(SimplePlanesUpgrades.SHOOTER.get(),planeEntity);
    }

    @Override
    public boolean onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getPlayer().getHeldItem(event.getHand());
        Vec3d motion = planeEntity.getMotion();
        Random random = event.getWorld().rand;
        Vec2f front = this.planeEntity.getHorizontalFrontPos();
        float pitch = FurnacePlaneEntity.getPitch(motion);
        if(planeEntity.getOnGround())
        {
            pitch = 30;
        }
        motion = planeEntity.getVec(planeEntity.rotationYaw, pitch)
                .scale(Math.max(0.25,motion.length()));
        double x = planeEntity.getPosX() + 1 * front.x;
        double z = planeEntity.getPosZ() + 1 * front.y;
        double y = planeEntity.getPosY() + 0.5;
        if(shootSide){
            z+= 1*front.x;
            x+= 1*front.y;
        }
        else
        {
            z-= 1*front.x;
            x-= 1*front.y;
        }
        shootSide=!shootSide;
        if (itemStack.getItem().equals(FIREWORK_ROCKET)) {
            shootSide=!shootSide;
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(event.getWorld(), itemStack,
                    x, y, z, true);
            fireworkrocketentity.shoot(-motion.x, -motion.y, -motion.z, -(float) Math.max(0.5F, motion.length() * 1.5), 1.0F);
            event.getWorld().addEntity(fireworkrocketentity);
            if (!event.getPlayer().isCreative()) {
                itemStack.shrink(1);
            }
        }
        if (itemStack.getItem().equals(FIRE_CHARGE)) {

            double d3 = random.nextGaussian() * 0.05D+2*motion.x;
            double d4 = random.nextGaussian() * 0.05D;
            double d5 = random.nextGaussian() * 0.05D+2*motion.z;
            AbstractFireballEntity fireBallEntity = Util.make(new SmallFireballEntity(event.getWorld(), event.getPlayer(), d3, d4, d5), (p_229425_1_) -> p_229425_1_.setStack(itemStack));
            fireBallEntity.forceSetPosition(x,y,z);
            fireBallEntity.setMotion(motion.scale(2));
            event.getWorld().addEntity(fireBallEntity);

        }
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        IVertexBuilder ivertexbuilder = buffer.getBuffer(SHOOTER_MODEL.getRenderType(TEXTURE));
        SHOOTER_MODEL.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}