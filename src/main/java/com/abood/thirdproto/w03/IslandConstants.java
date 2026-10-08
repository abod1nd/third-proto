package com.abood.thirdproto.w03; // عدّلها لتطابق حزمة w02 عندك + .w03

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/** W0-3: كل الثوابت بمكان واحد — مفتاح البُعد، القطع، إحداثياتها، ونقطة الـ spawn. */
public final class IslandConstants {
    public static final String MOD_ID = "third_proto";

    public static final ResourceKey<Level> ISLAND =
            ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(MOD_ID, "island"));

    public static final Identifier PIECE_A = Identifier.fromNamespaceAndPath(MOD_ID, "piece_a");
    public static final BlockPos PIECE_A_ORIGIN = new BlockPos(0, 64, 0);      // غرفة 12x6x12

    public static final Identifier PIECE_B = Identifier.fromNamespaceAndPath(MOD_ID, "piece_b");
    public static final BlockPos PIECE_B_ORIGIN = new BlockPos(24, 60, -60);   // منصة 40x4x40، بتقطع حدود chunks

    /** نص الغرفة A فوق الأرضية مباشرة. */
    public static final BlockPos SPAWN = new BlockPos(6, 65, 6);

    /** بلوك bedrock بأسفل البُعد = "القطع انوضعت". علامة مؤقتة للنموذج بس. */
    public static final BlockPos PLACED_MARKER = new BlockPos(0, -64, 0);
    public static final Identifier PIECE_C = Identifier.fromNamespaceAndPath(MOD_ID, "piece_c");
    public static final BlockPos PIECE_C_ORIGIN = new BlockPos(-100, 60, 0);   // T8: 64x4x64
    private IslandConstants() {}
}
