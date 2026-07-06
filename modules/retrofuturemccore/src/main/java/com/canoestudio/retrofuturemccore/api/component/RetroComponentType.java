package com.canoestudio.retrofuturemccore.api.component;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public final class RetroComponentType<C extends RetroComponent> {

    private final ResourceLocation id;
    private final Class<C> componentClass;
    private final ComponentFactory<? extends C> factory;

    private RetroComponentType(ResourceLocation id, Class<C> componentClass, ComponentFactory<? extends C> factory) {
        this.id = Objects.requireNonNull(id, "id");
        this.componentClass = Objects.requireNonNull(componentClass, "componentClass");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public static <C extends RetroComponent> RetroComponentType<C> create(ResourceLocation id, Class<C> componentClass,
            ComponentFactory<? extends C> factory) {
        return new RetroComponentType<C>(id, componentClass, factory);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Class<C> getComponentClass() {
        return this.componentClass;
    }

    public C createComponent(Entity entity) {
        return this.factory.create(entity);
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}
