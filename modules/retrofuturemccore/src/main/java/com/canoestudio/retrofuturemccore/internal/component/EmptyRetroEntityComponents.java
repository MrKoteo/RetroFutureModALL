package com.canoestudio.retrofuturemccore.internal.component;

import com.canoestudio.retrofuturemccore.api.component.IRetroEntityComponents;
import com.canoestudio.retrofuturemccore.api.component.RetroComponent;
import com.canoestudio.retrofuturemccore.api.component.RetroComponentType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

final class EmptyRetroEntityComponents implements IRetroEntityComponents {

    @Override
    public boolean has(RetroComponentType<?> type) {
        return false;
    }

    @Override
    public <C extends RetroComponent> C get(RetroComponentType<C> type) {
        return null;
    }

    @Override
    public RetroComponent get(ResourceLocation id) {
        return null;
    }

    @Override
    public Collection<RetroComponentType<?>> getTypes() {
        return Collections.emptyList();
    }

    @Override
    public void tick() {
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
    }

    @Override
    public NBTTagCompound writeSyncNbt(RetroComponentType<?> type) {
        return new NBTTagCompound();
    }

    @Override
    public void readSyncNbt(ResourceLocation id, NBTTagCompound tag) {
    }

    @Override
    public void syncDirty() {
    }

    @Override
    public void syncAll() {
    }
}
