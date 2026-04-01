package dev.jaegyu.aegis.bulwark;

import dev.jaegyu.aegis.bulwark.network.BulwarkHandshakeTask;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;

@Mod("aegis_bulwark")
public class Bulwark {

    public Bulwark(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, BulwarkConfig.SERVER_SPEC);
        modBus.addListener(this::onRegisterConfigurationTasks);
    }

    private void onRegisterConfigurationTasks(RegisterConfigurationTasksEvent event) {
        event.register(new BulwarkHandshakeTask(event.getListener()));
    }
}