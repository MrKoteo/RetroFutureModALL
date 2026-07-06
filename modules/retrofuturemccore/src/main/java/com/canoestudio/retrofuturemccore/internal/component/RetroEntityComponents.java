package com.canoestudio.retrofuturemccore.internal.component;

import com.canoestudio.retrofuturemccore.api.component.IRetroEntityComponents;
import com.canoestudio.retrofuturemccore.api.component.RetroComponent;
import com.canoestudio.retrofuturemccore.api.component.RetroComponentType;
import com.canoestudio.retrofuturemccore.api.component.SyncedComponent;
import com.canoestudio.retrofuturemccore.network.RetroFutureCoreNetwork;
import com.canoestudio.retrofuturemccore.network.message.MessageSyncEntityComponent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class RetroEntityComponents implements IRetroEntityComponents {

    private final Entity entity;
    private final Map<RetroComponentType<?>, RetroComponent> components =
            new LinkedHashMap<RetroComponentType<?>, RetroComponent>();

    public RetroEntityComponents(Entity entity, Collection<RetroComponentType<?>> types) {
        this.entity = entity;
        for (RetroComponentType<?> type : types) {
            RetroComponent component = type.createComponent(entity);
            this.components.put(type, component);
            component.onAttached(entity);
        }
    }

    @Override
    public boolean has(RetroComponentType<?> type) {
        return this.components.containsKey(type);
    }

    @Override
    public <C extends RetroComponent> C get(RetroComponentType<C> type) {
        RetroComponent component = this.components.get(type);
        return component == null ? null : type.getComponentClass().cast(component);
    }

    @Override
    public RetroComponent get(ResourceLocation id) {
        for (Map.Entry<RetroComponentType<?>, RetroComponent> entry : this.components.entrySet()) {
            if (entry.getKey().getId().equals(id)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<RetroComponentType<?>> getTypes() {
        return Collections.unmodifiableCollection(this.components.keySet());
    }

    @Override
    public void tick() {
        for (RetroComponent component : this.components.values()) {
            component.tick(this.entity);
        }
        if (!this.entity.world.isRemote) {
            this.syncDirty();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound root = new NBTTagCompound();
        for (Map.Entry<RetroComponentType<?>, RetroComponent> entry : this.components.entrySet()) {
            NBTTagCompound componentTag = entry.getValue().writeToNbt(new NBTTagCompound());
            root.setTag(entry.getKey().getId().toString(), componentTag);
        }
        return root;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        for (Map.Entry<RetroComponentType<?>, RetroComponent> entry : this.components.entrySet()) {
            String key = entry.getKey().getId().toString();
            if (tag.hasKey(key, 10)) {
                entry.getValue().readFromNbt(tag.getCompoundTag(key));
            }
        }
    }

    @Override
    public NBTTagCompound writeSyncNbt(RetroComponentType<?> type) {
        RetroComponent component = this.components.get(type);
        if (component instanceof SyncedComponent) {
            return ((SyncedComponent) component).writeSyncNbt(new NBTTagCompound());
        }
        return new NBTTagCompound();
    }

    @Override
    public void readSyncNbt(ResourceLocation id, NBTTagCompound tag) {
        RetroComponent component = this.get(id);
        if (component instanceof SyncedComponent) {
            ((SyncedComponent) component).readSyncNbt(tag);
            ((SyncedComponent) component).markClean();
        }
    }

    @Override
    public void syncDirty() {
        for (Map.Entry<RetroComponentType<?>, RetroComponent> entry : this.components.entrySet()) {
            if (entry.getValue() instanceof SyncedComponent) {
                SyncedComponent component = (SyncedComponent) entry.getValue();
                if (component.isDirty()) {
                    this.sendSync(entry.getKey(), component);
                    component.markClean();
                }
            }
        }
    }

    @Override
    public void syncAll() {
        for (Map.Entry<RetroComponentType<?>, RetroComponent> entry : this.components.entrySet()) {
            if (entry.getValue() instanceof SyncedComponent) {
                SyncedComponent component = (SyncedComponent) entry.getValue();
                this.sendSync(entry.getKey(), component);
                component.markClean();
            }
        }
    }

    public void syncAllTo(EntityPlayerMP player) {
        for (Map.Entry<RetroComponentType<?>, RetroComponent> entry : this.components.entrySet()) {
            if (entry.getValue() instanceof SyncedComponent) {
                this.sendSyncTo(entry.getKey(), (SyncedComponent) entry.getValue(), player);
            }
        }
    }

    private void sendSync(RetroComponentType<?> type, SyncedComponent component) {
        MessageSyncEntityComponent message = new MessageSyncEntityComponent(this.entity.getEntityId(), type.getId(),
                component.writeSyncNbt(new NBTTagCompound()));
        RetroFutureCoreNetwork.sendToTrackingAndSelf(message, this.entity);
    }

    private void sendSyncTo(RetroComponentType<?> type, SyncedComponent component, EntityPlayerMP player) {
        MessageSyncEntityComponent message = new MessageSyncEntityComponent(this.entity.getEntityId(), type.getId(),
                component.writeSyncNbt(new NBTTagCompound()));
        RetroFutureCoreNetwork.sendTo(message, player);
    }
}
