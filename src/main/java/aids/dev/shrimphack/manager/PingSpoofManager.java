package aids.dev.shrimphack.manager;

import aids.dev.shrimphack.Shrimphack;
import aids.dev.shrimphack.event.impl.Render3DEvent;
import aids.dev.shrimphack.features.modules.exploit.PingSpoof;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;

import java.util.ArrayList;
import java.util.List;

import static aids.dev.shrimphack.manager.IManager.mc;
import static aids.dev.shrimphack.util.traits.Util.EVENT_BUS;

public class PingSpoofManager {
    private final List<DelayedPacket> delayed = new ArrayList<>();
    private DelayedPacket delayed1 = null;
    private DelayedPacket delayed2 = null;

    public PingSpoofManager() {
        EVENT_BUS.register(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRender(Render3DEvent event) {
        List<DelayedPacket> toSend = new ArrayList<>();

        if (delayed1 != null) {
            delayed.add(delayed1);
            delayed1 = null;
        }
        if (delayed2 != null) {
            delayed.add(delayed2);
            delayed2 = null;
        }

        for (DelayedPacket d : delayed) {
            if (System.currentTimeMillis() > d.time) toSend.add(d);
        }

        toSend.forEach(d -> {
            mc.getNetworkHandler().sendPacket(d.packet);
            delayed.remove(d);
        });

        toSend.clear();
    }

    public void addKeepAlive(long id) {
        PingSpoof pingSpoof = (PingSpoof) Shrimphack.moduleManager.getModuleByClass(PingSpoof.class);
        delayed1 = new DelayedPacket(new KeepAliveC2SPacket(id), System.currentTimeMillis() + pingSpoof.getPing());
    }

    public void addPong(int id) {
        PingSpoof pingSpoof = (PingSpoof) Shrimphack.moduleManager.getModuleByClass(PingSpoof.class);
        delayed2 = new DelayedPacket(new CommonPongC2SPacket(id), System.currentTimeMillis() + pingSpoof.getPing());
    }

    private record DelayedPacket(Packet<?> packet, long time) {}
}
