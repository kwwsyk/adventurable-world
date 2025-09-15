package com.kwwsyk.suit.adventurableworld.data;

import com.kwwsyk.suit.adventurableworld.ModInit;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * Helper class of mod I18n and L10n
 */
public abstract class Translator {

    public static final String MOD_ID = ModInit.MODID;

    public static class EnglishProvider extends LanguageProvider {

        public EnglishProvider(PackOutput output){
            super(output, MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("adv_option.title","Adventure Suit Options");
        }
    }

    public static class SimplifiedChineseProvider extends LanguageProvider {

        public SimplifiedChineseProvider(PackOutput output){
            super(output, MOD_ID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            add("adv_option.title","冒险适应性设定");
        }
    }
}
