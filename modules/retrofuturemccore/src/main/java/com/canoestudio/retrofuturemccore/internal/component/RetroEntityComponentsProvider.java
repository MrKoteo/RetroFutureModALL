package com.canoestudio.retrofuturemccore.internal.component;

import com.canoestudio.retrofuturemccore.api.component.IRetroEntityComponents;
import com.canoestudio.retrofuturemccore.api.component.RetroComponentType;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class RetroEntityComponentsProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final RetroEntityComponents components;

    public RetroEntityComponentsProvider(Entity entity, Collection<RetroComponentType<?>> types) {
        this.components = new RetroEntityComponents(entity, types);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == RetroEntityComponentsCapability.CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == RetroEntityComponentsCapability.CAPABILITY ? (T) this.components : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.components.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.components.deserializeNBT(nbt);
    }
}
