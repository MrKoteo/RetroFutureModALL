package com.canoestudio.retrofuturemccore.api.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public final class RetroComponentRegistry {

    private static final Map<ResourceLocation, RetroComponentType<?>> TYPES =
            new LinkedHashMap<ResourceLocation, RetroComponentType<?>>();
    private static final Map<Class<? extends Entity>, List<RetroComponentType<?>>> ENTITY_TYPES =
            new LinkedHashMap<Class<? extends Entity>, List<RetroComponentType<?>>>();

    private RetroComponentRegistry() {
    }

    public static synchronized <C extends RetroComponent> RetroComponentType<C> register(ResourceLocation id,
            Class<C> componentClass, ComponentFactory<? extends C> factory) {
        RetroComponentType<C> type = RetroComponentType.create(id, componentClass, factory);
        register(type);
        return type;
    }

    public static synchronized void register(RetroComponentType<?> type) {
        if (TYPES.containsKey(type.getId())) {
            throw new IllegalArgumentException("Duplicate retro component id: " + type.getId());
        }
        TYPES.put(type.getId(), type);
    }

    public static synchronized void attachToEntity(Class<? extends Entity> entityClass, RetroComponentType<?> type) {
        if (!TYPES.containsKey(type.getId())) {
            register(type);
        }
        List<RetroComponentType<?>> types = ENTITY_TYPES.get(entityClass);
        if (types == null) {
            types = new ArrayList<RetroComponentType<?>>();
            ENTITY_TYPES.put(entityClass, types);
        }
        if (!types.contains(type)) {
            types.add(type);
        }
    }

    public static synchronized RetroComponentType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    public static synchronized Collection<RetroComponentType<?>> getRegisteredTypes() {
        return Collections.unmodifiableCollection(new ArrayList<RetroComponentType<?>>(TYPES.values()));
    }

    public static synchronized List<RetroComponentType<?>> getTypesFor(Entity entity) {
        List<RetroComponentType<?>> result = new ArrayList<RetroComponentType<?>>();
        Class<?> current = entity.getClass();
        while (current != null && Entity.class.isAssignableFrom(current)) {
            List<RetroComponentType<?>> exact = ENTITY_TYPES.get(current);
            if (exact != null) {
                addMissing(result, exact);
            }
            current = current.getSuperclass();
        }

        for (Map.Entry<Class<? extends Entity>, List<RetroComponentType<?>>> entry : ENTITY_TYPES.entrySet()) {
            if (entry.getKey().isAssignableFrom(entity.getClass())) {
                addMissing(result, entry.getValue());
            }
        }
        return result;
    }

    private static void addMissing(List<RetroComponentType<?>> result, List<RetroComponentType<?>> additions) {
        for (RetroComponentType<?> type : additions) {
            if (!result.contains(type)) {
                result.add(type);
            }
        }
    }
}
