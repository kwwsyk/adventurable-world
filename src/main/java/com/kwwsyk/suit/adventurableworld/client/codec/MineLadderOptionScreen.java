package com.kwwsyk.suit.adventurableworld.client.codec;

import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig;
import com.kwwsyk.suit.codec_config_lib.api.Constraints;
import com.kwwsyk.suit.codec_config_lib.client.gui.CodecOptionScreen;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Thin wrapper around {@link CodecOptionScreen} that wires the ladder configuration into the generic
 * codec-driven UI. All domain-specific behavior is handled through the builder, keeping the class
 * limited to providing translations, constraints and the final callback.
 */
public class MineLadderOptionScreen extends CodecOptionScreen<MineLadderConfig> {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ValuePath ROOT = ValuePath.root();
    private static final ValuePath MIN_LENGTH = ROOT.child("min_length");
    private static final ValuePath MAX_LENGTH = ROOT.child("max_length");
    private static final ValuePath LENGTH_INCLUDE = ROOT.child("length_include");

    public MineLadderOptionScreen(Screen lastScreen, MineLadderConfig config) {
        this(lastScreen, config, value -> LOGGER.info("User confirmed ladder configuration: {}", value));
    }

    public MineLadderOptionScreen(Screen lastScreen,
                                  MineLadderConfig config,
                                  Consumer<MineLadderConfig> onApply) {
        super(CodecOptionScreen.Builder.create(lastScreen, Component.translatable("adv_option.title"), config, MineLadderConfig.CODEC)
                .withLabelPrefix("adv_option.mine_ladder")
                .withLabel(ROOT, Component.translatable("adv_option.mine_ladder.root"))
                .withTooltip(MIN_LENGTH, Component.translatable("adv_option.mine_ladder.min_length.tooltip"))
                .withTooltip(MAX_LENGTH, Component.translatable("adv_option.mine_ladder.max_length.tooltip"))
                .withConstraints(MIN_LENGTH, new Constraints(Optional.of(1), Optional.empty(), Optional.of(1), Optional.empty()))
                .withConstraints(MAX_LENGTH, new Constraints(Optional.of(1), Optional.of(128), Optional.of(1), Optional.empty()))
                .withEnum(LENGTH_INCLUDE,
                        MineLadderConfig.LengthInclude.values(),
                        value -> Component.translatable("adv_option.mine_ladder.length_include." + value.getSerializedName()),
                        value -> value.getSerializedName().toLowerCase(Locale.ROOT))
                .onApply(onApply));
    }
}
