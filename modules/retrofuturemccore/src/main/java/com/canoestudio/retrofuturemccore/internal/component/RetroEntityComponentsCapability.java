package com.canoestudio.retrofuturemccore.internal.component;

import com.canoestudio.retrofuturemccore.api.component.IRetroEntityComponents;
import java.util.concurrent.Callable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class RetroEntityComponentsCapability {

    @CapabilityInject(IRetroEntityComponents.class)
    public static Capability<IRetroEntityComponents> CAPABILITY = null;

    private RetroEntityComponentsCapability() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IRetroEntityComponents.class, new Storage(), new Callable<IRetroEntityComponents>() {
            @Override
            public IRetroEntityComponents call() {
                return new EmptyRetroEntityComponents();
            }
        });
    }

    private static final class Storage implements Capability.IStorage<IRetroEntityComponents> {

        @Override
        public NBTBase writeNBT(Capability<IRetroEntityComponents> capability, IRetroEntityComponents instance,
                EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IRetroEntityComponents> capability, IRetroEntityComponents instance,
                EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }
    }
}
