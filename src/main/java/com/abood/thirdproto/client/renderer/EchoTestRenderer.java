package com.abood.thirdproto.client.renderer;

import com.abood.thirdproto.ThirdPrototypes;
import com.abood.thirdproto.entity.EchoTestEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

public class EchoTestRenderer extends MobRenderer<EchoTestEntity, EchoTestRenderState, PlayerModel> {

    private static final Identifier FAKE =
            Identifier.fromNamespaceAndPath(ThirdPrototypes.MODID, "textures/entity/does_not_exist.png");

    private static final PlayerSkin GREEN = PlayerSkin.insecure(
            new ClientAsset.ResourceTexture(
                    Identifier.fromNamespaceAndPath(ThirdPrototypes.MODID, "entity/ely_test")),
            null,
            null,
            PlayerModelType.WIDE
    );

    private boolean logged = false;

    public EchoTestRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public EchoTestRenderState createRenderState() {
        return new EchoTestRenderState();
    }

    @Override
    public Identifier getTextureLocation(EchoTestRenderState state) {
        if (!logged) {
            logged = true;
            ThirdPrototypes.LOGGER.info("GET_TEXTURE_LOCATION CALLED");
        }
        return FAKE;
    }

    @Override
    public void extractRenderState(EchoTestEntity entity, EchoTestRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.skin = GREEN;
    }
}