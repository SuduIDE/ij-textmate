package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

public interface InjectionStrategy {
    String identifier();
    void registrar();
    void delete();
}
