package com.kwwsyk.suit.codec_config_lib.client;

import com.kwwsyk.suit.codec_config_lib.client.gui.OptionScreen;
import com.kwwsyk.suit.codec_config_lib.client.gui.widget.OptionEntry;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;

public interface BaseTypeOption {

    OptionEntry.ConfigEntry createEntry(OptionScreen screen, @Nullable List<FormattedCharSequence> tooltip, MapCodec<?> codec);
}
