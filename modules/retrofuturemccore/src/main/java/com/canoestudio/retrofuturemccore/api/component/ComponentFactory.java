package com.canoestudio.retrofuturemccore.api.component;

import net.minecraft.entity.Entity;

public interface ComponentFactory<C extends RetroComponent> {

    C create(Entity entity);
}
