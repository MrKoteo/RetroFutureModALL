package com.canoestudio.retrofuturemccore.api.component;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public abstract class AbstractSyncedComponent extends AbstractRetroComponent implements SyncedComponent {

    private boolean dirty = true;
    private int syncRange = 64;

    protected void markDirty() {
        this.dirty = true;
    }

    public AbstractSyncedComponent setSyncRange(int syncRange) {
        this.syncRange = syncRange;
        return this;
    }

    @Override
    public NBTTagCompound writeSyncNbt(NBTTagCompound tag) {
        return this.writeToNbt(tag);
    }

    @Override
    public void readSyncNbt(NBTTagCompound tag) {
        this.readFromNbt(tag);
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void markClean() {
        this.dirty = false;
    }

    @Override
    public int getSyncRange(Entity entity) {
        return this.syncRange;
    }
}
