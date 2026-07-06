package com.canoestudio.retrofuturemccore.api.component;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public interface RetroComponent {

    void onAttached(Entity entity);

    void tick(Entity entity);

    void readFromNbt(NBTTagCompound tag);

    NBTTagCompound writeToNbt(NBTTagCompound tag);
}
