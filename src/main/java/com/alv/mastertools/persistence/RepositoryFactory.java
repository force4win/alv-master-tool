package com.alv.mastertools.persistence;

public class RepositoryFactory {

    // Aquí podríamos leer una variable de entorno o config para decidir qué
    // provider usar
    // Por ahora, devolvemos FileDataProvider por defecto.

    public static IDataProvider getProvider() {
        return new FileDataProvider();
    }
}
