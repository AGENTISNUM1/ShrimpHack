package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class Cape extends Module {
    private static Cape INSTANCE = new Cape();
        public final Setting<CapeMode> cape = this.register(new Setting<CapeMode>("CapeMode", CapeMode.NONE, "Cape."));

    public Cape() {
        super("Cape", "Cape", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static Cape getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Cape();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public enum CapeMode {
        FUTURE,
        RUSHERHACK,
        HYPER,
        COBALT,
        FOUNDER,
        HIGHLAND,
        CAPY,
        MINECON2011,
        MINECON2012,
        MINECON2013,
        MINECON2015,
        MINECON2016,
        MOJANG,
        MOJANG_CLASSIC,
        MOJANG_STUDIOS,
        PHOBOS,
        RUSHERHACKEK,
        SNOWMAN,
        NONE
    }
}
