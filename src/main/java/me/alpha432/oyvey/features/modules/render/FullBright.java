package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
        private static FullBright INSTANCE = new FullBright();
    public FullBright() {
        super("FullBright", "see in the dark", Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static FullBright getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FullBright();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1000000, 254, true, false, false));
    }

    @Override
    public void onDisable() {
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
