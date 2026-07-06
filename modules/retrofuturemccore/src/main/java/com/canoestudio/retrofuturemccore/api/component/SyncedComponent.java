package com.canoestudio.retrofuturemccore.api.component;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public interface SyncedComponent extends RetroComponent {

    NBTTagCompound writeSyncNbt(NBTTagCompound tag);

    void readSyncNbt(NBTTagCompound tag);

    boolean isDirty();

    void markClean();

    int getSyncRange(Entity entity);
}
