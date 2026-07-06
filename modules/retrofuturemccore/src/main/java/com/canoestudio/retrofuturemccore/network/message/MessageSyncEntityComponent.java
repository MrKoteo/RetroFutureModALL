package com.canoestudio.retrofuturemccore.network.message;

import com.canoestudio.retrofuturemccore.RetroFutureMCCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncEntityComponent implements IMessage {

    private int entityId;
    private ResourceLocation componentId;
    private NBTTagCompound tag;

    public MessageSyncEntityComponent() {
    }

    public MessageSyncEntityComponent(int entityId, ResourceLocation componentId, NBTTagCompound tag) {
        this.entityId = entityId;
        this.componentId = componentId;
        this.tag = tag;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public ResourceLocation getComponentId() {
        return this.componentId;
    }

    public NBTTagCompound getTag() {
        return this.tag == null ? new NBTTagCompound() : this.tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.componentId = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        this.tag = ByteBufUtils.readTag(buf);
        if (this.tag == null) {
            this.tag = new NBTTagCompound();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeUTF8String(buf, this.componentId.toString());
        ByteBufUtils.writeTag(buf, this.tag == null ? new NBTTagCompound() : this.tag);
    }

    public static class Handler implements IMessageHandler<MessageSyncEntityComponent, IMessage> {

        @Override
        public IMessage onMessage(MessageSyncEntityComponent message, MessageContext ctx) {
            RetroFutureMCCore.PROXY.handleEntityComponentSync(message);
            return null;
        }
    }
}
