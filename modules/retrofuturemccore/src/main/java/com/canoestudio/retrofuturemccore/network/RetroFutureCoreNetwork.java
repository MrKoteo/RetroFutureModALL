package com.canoestudio.retrofuturemccore.network;

import com.canoestudio.retrofuturemccore.Tags;
import com.canoestudio.retrofuturemccore.network.message.MessageSyncEntityComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class RetroFutureCoreNetwork {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

    private static int discriminator;

    private RetroFutureCoreNetwork() {
    }

    public static void registerMessages() {
        CHANNEL.registerMessage(MessageSyncEntityComponent.Handler.class, MessageSyncEntityComponent.class,
                discriminator++, Side.CLIENT);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        CHANNEL.sendTo(message, player);
    }

    public static void sendToTrackingAndSelf(IMessage message, Entity entity) {
        CHANNEL.sendToAllTracking(message, entity);
        if (entity instanceof EntityPlayerMP) {
            CHANNEL.sendTo(message, (EntityPlayerMP) entity);
        }
    }
}
