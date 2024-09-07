package aids.dev.shrimphack.features.modules.client;

import aids.dev.shrimphack.Shrimphack;
import aids.dev.shrimphack.event.impl.ClientEvent;
import aids.dev.shrimphack.features.commands.Command;
import aids.dev.shrimphack.features.gui.ShrimpHackGui;
import aids.dev.shrimphack.features.modules.Module;
import aids.dev.shrimphack.features.settings.Setting;
import com.google.common.eventbus.Subscribe;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix = this.register(new Setting<String>("Prefix", "."));
    //Color
    public Setting<Integer> red = this.register(new Setting<>("Red", 145, 0, 255));
    public Setting<Integer> green = this.register(new Setting<>("Green", 160, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<>("HoverAlpha", 247, 0, 255));
    public Setting<Integer> hoverAlpha = this.register(new Setting<>("Alpha", 212, 0, 255));
    public Setting<Boolean> rainbow = this.register(new Setting<>("Rainbow", false));
    public Setting<Integer> rainbowHue = this.register(new Setting<>("Delay", 240, 0, 600, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowBrightness = this.register(new Setting<>("Brightness ", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<>("Saturation", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));

    //BackGround
    public Setting<Boolean> background = this.register(new Setting<>("BackGround", true));
    public Setting<Integer> backgroundRed = this.register(new Setting<>("Red", 0, 0, 255, v -> this.background.getValue()));
    public Setting<Integer> backgroundGreen = this.register(new Setting<>("Green", 0, 0, 255, v -> this.background.getValue()));
    public Setting<Integer> backgroundBlue = this.register(new Setting<>("Blue", 0, 0, 255, v -> this.background.getValue()));
    public Setting<Integer> backgroundAlpha = this.register(new Setting<>("BGAlpha", 142, 0, 255, v -> this.background.getValue()));

    //FrameBackground
    public Setting<Boolean> frameBackground = this.register(new Setting<>("FrameBackGround", true));
    public Setting<Integer> frameBackgroundRed = this.register(new Setting<>("Red", 145, 0, 255, v -> this.frameBackground.getValue()));
    public Setting<Integer> frameBackgroundGreen = this.register(new Setting<>("Green", 160, 0, 255, v -> this.frameBackground.getValue()));
    public Setting<Integer> frameBackgroundBlue = this.register(new Setting<>("Blue", 255, 0, 255, v -> this.frameBackground.getValue()));
    public Setting<Integer> frameBackgroundAlpha = this.register(new Setting<>("FBGAlpha", 43, 0, 255, v -> this.frameBackground.getValue()));

    //PlusColor
    public Setting<Boolean> plusColor = this.register(new Setting<>("PlusColor", true));
    public Setting<Integer> plusRed = this.register(new Setting<>("PlusRed", 255, 0, 255));
    public Setting<Integer> plusGreen = this.register(new Setting<>("PlusGreen", 255, 0, 255));
    public Setting<Integer> plusBlue = this.register(new Setting<>("PlusBlue", 255, 0, 255));
    public Setting<Integer> plusAlpha = this.register(new Setting<>("PlusAlpha", 255, 0, 255));


    //Otherd
    public Setting<Boolean> Snowing = this.register(new Setting<>("Snowing", false));
    public Setting<Boolean> scaleFactorFix = this.register(new Setting<>("ScaleFactorFix", false));
    public Setting<Float> scaleFactorFixValue = this.register(new Setting<>("ScaleFactorFixValue", 2f, 0f, 4f));

    private ShrimpHackGui click;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Category.CLIENT, true, false, false);
        setBind(GLFW.GLFW_KEY_RIGHT_SHIFT);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        //remove
    }

    @Subscribe
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Shrimphack.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + Formatting.DARK_GRAY + Shrimphack.commandManager.getPrefix());
            }
            Shrimphack.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        mc.setScreen(ShrimpHackGui.getClickGui());
    }

    @Override
    public void onLoad() {
        Shrimphack.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        Shrimphack.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ShrimpHackGui)) {
            this.disable();
        }
    }
}