package com.kwwsyk.suit.adventurableworld.client.codec;

import com.kwwsyk.suit.adventurableworld.data.worldgen.WorldgenSettings;
import com.kwwsyk.suit.adventurableworld.data.worldgen.WorldgenSettingsManager;
import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig;
import com.kwwsyk.suit.codec_config_lib.api.Constraints;
import com.kwwsyk.suit.codec_config_lib.client.gui.CodecOptionScreen;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * Codec driven configuration screen exposing the world generation values used by the datapack.
 */
public class WorldgenOptionScreen extends CodecOptionScreen<WorldgenSettings> {

    private static final ValuePath ROOT = ValuePath.root();
    private static final ValuePath SIMPLE_LADDER = ROOT.child("simple_ladder");
    private static final ValuePath SHORT_LADDER = ROOT.child("short_ladder");
    private static final ValuePath LONG_LADDER = ROOT.child("long_ladder");
    private static final ValuePath SIMPLE_PLACEMENT = ROOT.child("simple_placement");
    private static final ValuePath SHORT_PLACEMENT = ROOT.child("short_placement");
    private static final ValuePath LONG_PLACEMENT = ROOT.child("long_placement");
    private static final ValuePath HIGH_PLACEMENT = ROOT.child("high_placement");

    private static Constraints lengthConstraint(int min, int max) {
        return new Constraints(Optional.of(min), Optional.of(max), Optional.of(1), Optional.empty());
    }

    private static Constraints countConstraint() {
        return new Constraints(Optional.of(0), Optional.of(256), Optional.of(1), Optional.empty());
    }

    public WorldgenOptionScreen(Screen lastScreen) {
        this(lastScreen, WorldgenSettingsManager.get());
    }

    public WorldgenOptionScreen(Screen lastScreen, WorldgenSettings config) {
        super(CodecOptionScreen.Builder.create(lastScreen,
                        Component.translatable("adv_option.worldgen.title"),
                        config,
                        WorldgenSettings.CODEC)
                .withLabelPrefix("adv_option.worldgen")
                .withLabel(ROOT, Component.translatable("adv_option.worldgen.root"))
                .withLabel(SIMPLE_LADDER, Component.translatable("adv_option.worldgen.simple_ladder"))
                .withLabel(SHORT_LADDER, Component.translatable("adv_option.worldgen.short_ladder"))
                .withLabel(LONG_LADDER, Component.translatable("adv_option.worldgen.long_ladder"))
                .withLabel(SIMPLE_PLACEMENT, Component.translatable("adv_option.worldgen.simple_placement"))
                .withLabel(SHORT_PLACEMENT, Component.translatable("adv_option.worldgen.short_placement"))
                .withLabel(LONG_PLACEMENT, Component.translatable("adv_option.worldgen.long_placement"))
                .withLabel(HIGH_PLACEMENT, Component.translatable("adv_option.worldgen.high_placement"))
                .withTooltip(SIMPLE_LADDER.child("min_length"), Component.translatable("adv_option.worldgen.min_length.tooltip"))
                .withTooltip(SIMPLE_LADDER.child("max_length"), Component.translatable("adv_option.worldgen.max_length.tooltip"))
                .withTooltip(SHORT_LADDER.child("min_length"), Component.translatable("adv_option.worldgen.min_length.tooltip"))
                .withTooltip(SHORT_LADDER.child("max_length"), Component.translatable("adv_option.worldgen.max_length.tooltip"))
                .withTooltip(LONG_LADDER.child("min_length"), Component.translatable("adv_option.worldgen.min_length.tooltip"))
                .withTooltip(LONG_LADDER.child("max_length"), Component.translatable("adv_option.worldgen.max_length.tooltip"))
                .withConstraints(SIMPLE_LADDER.child("min_length"), lengthConstraint(1, 128))
                .withConstraints(SIMPLE_LADDER.child("max_length"), lengthConstraint(1, 256))
                .withConstraints(SHORT_LADDER.child("min_length"), lengthConstraint(1, 128))
                .withConstraints(SHORT_LADDER.child("max_length"), lengthConstraint(1, 256))
                .withConstraints(LONG_LADDER.child("min_length"), lengthConstraint(1, 128))
                .withConstraints(LONG_LADDER.child("max_length"), lengthConstraint(1, 256))
                .withConstraints(SIMPLE_PLACEMENT.child("count"), countConstraint())
                .withConstraints(SHORT_PLACEMENT.child("count"), countConstraint())
                .withConstraints(LONG_PLACEMENT.child("count"), countConstraint())
                .withConstraints(HIGH_PLACEMENT.child("count"), countConstraint())
                .withConstraints(SHORT_PLACEMENT.child("min_above_bottom"), lengthConstraint(0, 512))
                .withConstraints(SHORT_PLACEMENT.child("max_above_bottom"), lengthConstraint(0, 512))
                .withConstraints(LONG_PLACEMENT.child("min_above_bottom"), lengthConstraint(0, 512))
                .withConstraints(LONG_PLACEMENT.child("max_above_bottom"), lengthConstraint(0, 512))
                .withConstraints(HIGH_PLACEMENT.child("min_above_bottom"), lengthConstraint(0, 512))
                .withConstraints(HIGH_PLACEMENT.child("max_above_bottom"), lengthConstraint(0, 512))
                .withEnum(SIMPLE_LADDER.child("length_include"), MineLadderConfig.LengthInclude.values(),
                        value -> Component.translatable("adv_option.worldgen.length_include." + value.getSerializedName()),
                        MineLadderConfig.LengthInclude::getSerializedName)
                .withEnum(SHORT_LADDER.child("length_include"), MineLadderConfig.LengthInclude.values(),
                        value -> Component.translatable("adv_option.worldgen.length_include." + value.getSerializedName()),
                        MineLadderConfig.LengthInclude::getSerializedName)
                .withEnum(LONG_LADDER.child("length_include"), MineLadderConfig.LengthInclude.values(),
                        value -> Component.translatable("adv_option.worldgen.length_include." + value.getSerializedName()),
                        MineLadderConfig.LengthInclude::getSerializedName)
                .onApply(WorldgenOptionScreen::applySettings));
    }

    private static void applySettings(WorldgenSettings settings) {
        WorldgenSettingsManager.set(settings);
    }
}
