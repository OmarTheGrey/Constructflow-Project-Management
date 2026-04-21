package com.constructflow.service.factory;

public interface EntityFactory<E, D> {
    E create(D dto);
    void apply(E existing, D dto);
}
