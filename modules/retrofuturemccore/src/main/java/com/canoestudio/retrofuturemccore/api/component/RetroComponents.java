package com.canoestudio.retrofuturemccore.api.component;

import com.canoestudio.retrofuturemccore.internal.component.RetroEntityComponentsCapability;
import java.util.Optional;
import net.minecraft.entity.Entity;

public final class RetroComponents {

    private RetroComponents() {
    }

    public static Optional<IRetroEntityComponents> get(Entity entity) {
        if (entity == null || !entity.hasCapability(RetroEntityComponentsCapability.CAPABILITY, null)) {
            return Optional.empty();
        }
        return Optional.ofNullable(entity.getCapability(RetroEntityComponentsCapability.CAPABILITY, null));
    }

    public static <C extends RetroComponent> Optional<C> get(Entity entity, RetroComponentType<C> type) {
        Optional<IRetroEntityComponents> components = get(entity);
        return components.isPresent() ? Optional.ofNullable(components.get().get(type)) : Optional.<C>empty();
    }

    public static void syncAll(Entity entity) {
        Optional<IRetroEntityComponents> components = get(entity);
        if (components.isPresent()) {
            components.get().syncAll();
        }
    }

    public static void syncDirty(Entity entity) {
        Optional<IRetroEntityComponents> components = get(entity);
        if (components.isPresent()) {
            components.get().syncDirty();
        }
    }
}
