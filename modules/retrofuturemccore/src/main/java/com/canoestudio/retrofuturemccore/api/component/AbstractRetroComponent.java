package com.canoestudio.retrofuturemccore.api.component;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public abstract class AbstractRetroComponent implements RetroComponent {

    @Override
    public void onAttached(Entity entity) {
    }

    @Override
    public void tick(Entity entity) {
    }

    @Override
    public void readFromNbt(NBTTagCompound tag) {
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        return tag;
    }
}
