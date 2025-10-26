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
            add("adv_option.mine_ladder.hangable", "Allow hanging ladders");
            add("adv_option.mine_ladder.min_length", "Minimum length");
            add("adv_option.mine_ladder.max_length", "Maximum length");
            add("adv_option.mine_ladder.climbable", "Enable climbing");
            add("adv_option.mine_ladder.length_include", "Length counting mode");
            add("adv_option.mine_ladder.min_length.tooltip", "Shortest ladder length permitted.");
            add("adv_option.mine_ladder.max_length.tooltip", "Longest ladder length permitted.");
            add("adv_option.codec.lifecycle", "Lifecycle: %s");
            add("adv_option.codec.error", "Codec error: %s");
            add("adv_option.codec.schema_missing", "Unable to build codec schema.");
            add("adv_option.codec.unsupported", "%s nodes are not supported yet (%s)");
            add("adv_option.codec.preview", "JSON Preview");
        }
    }

    public static class SimplifiedChineseProvider extends LanguageProvider {

        public SimplifiedChineseProvider(PackOutput output){
            super(output, MOD_ID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            add("adv_option.title","冒险适应性设定");
            add("adv_option.mine_ladder.hangable", "允许悬空梯子");
            add("adv_option.mine_ladder.min_length", "最小梯长");
            add("adv_option.mine_ladder.max_length", "最大梯长");
            add("adv_option.mine_ladder.climbable", "允许攀爬");
            add("adv_option.mine_ladder.length_include", "长度统计模式");
            add("adv_option.mine_ladder.min_length.tooltip", "允许生成的最短梯子长度。");
            add("adv_option.mine_ladder.max_length.tooltip", "允许生成的最长梯子长度。");
            add("adv_option.codec.lifecycle", "生命周期：%s");
            add("adv_option.codec.error", "编解码错误：%s");
            add("adv_option.codec.schema_missing", "无法构建编解码架构。");
            add("adv_option.codec.unsupported", "%s 节点尚未支持（%s）");
            add("adv_option.codec.preview", "JSON 预览");
        }
    }
}
