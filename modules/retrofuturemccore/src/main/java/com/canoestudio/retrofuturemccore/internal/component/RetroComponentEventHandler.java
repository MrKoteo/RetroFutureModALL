package com.canoestudio.retrofuturemccore.internal.component;

import com.canoestudio.retrofuturemccore.Tags;
import com.canoestudio.retrofuturemccore.api.component.IRetroEntityComponents;
import com.canoestudio.retrofuturemccore.api.component.RetroComponentRegistry;
import com.canoestudio.retrofuturemccore.api.component.RetroComponentType;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RetroComponentEventHandler {

    private static final ResourceLocation COMPONENTS_ID = new ResourceLocation(Tags.MOD_ID, "entity_components");

    @SubscribeEvent
    public void attachEntityComponents(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        List<RetroComponentType<?>> types = RetroComponentRegistry.getTypesFor(entity);
        if (!types.isEmpty()) {
            event.addCapability(COMPONENTS_ID, new RetroEntityComponentsProvider(entity, types));
        }
    }

    @SubscribeEvent
    public void syncEntityComponentsWhenJoining(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!entity.world.isRemote && entity.hasCapability(RetroEntityComponentsCapability.CAPABILITY, null)) {
            IRetroEntityComponents components =
                    entity.getCapability(RetroEntityComponentsCapability.CAPABILITY, null);
            if (components != null) {
                components.syncAll();
            }
        }
    }

    @SubscribeEvent
    public void tickEntityComponents(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }

        for (Entity entity : event.world.loadedEntityList) {
            if (entity.hasCapability(RetroEntityComponentsCapability.CAPABILITY, null)) {
                IRetroEntityComponents components =
                        entity.getCapability(RetroEntityComponentsCapability.CAPABILITY, null);
                if (components != null) {
                    components.tick();
                }
            }
        }
    }

    @SubscribeEvent
    public void startTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (event.getEntityPlayer() instanceof EntityPlayerMP
                && target.hasCapability(RetroEntityComponentsCapability.CAPABILITY, null)) {
            IRetroEntityComponents components = target.getCapability(RetroEntityComponentsCapability.CAPABILITY, null);
            if (components instanceof RetroEntityComponents) {
                ((RetroEntityComponents) components).syncAllTo((EntityPlayerMP) event.getEntityPlayer());
            }
        }
    }
}
