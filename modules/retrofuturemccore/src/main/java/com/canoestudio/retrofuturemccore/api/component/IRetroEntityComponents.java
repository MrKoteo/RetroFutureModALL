package com.canoestudio.retrofuturemccore.api.component;

import java.util.Collection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface IRetroEntityComponents {

    boolean has(RetroComponentType<?> type);

    <C extends RetroComponent> C get(RetroComponentType<C> type);

    RetroComponent get(ResourceLocation id);

    Collection<RetroComponentType<?>> getTypes();

    void tick();

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound tag);

    NBTTagCompound writeSyncNbt(RetroComponentType<?> type);

    void readSyncNbt(ResourceLocation id, NBTTagCompound tag);

    void syncDirty();

    void syncAll();
}
