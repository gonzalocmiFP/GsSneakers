package com.example.gssneakers.database;

/**
 * Interfaz genérica para manejar los resultados de operaciones asíncronas en Firestore.
 * <p>
 * Dado que Firestore realiza las operaciones de forma asíncrona (por ejemplo, consultas o escrituras),
 * esta interfaz permite capturar el resultado de dichas operaciones y procesarlo en el método `onCallback`.
 * <p>
 * En lugar de devolver el resultado directamente (lo cual no es posible con operaciones asíncronas),
 * el método `onCallback` es llamado con el resultado cuando la operación termina, es decir simula
 * el comportamiento de un "return" clasico.
 *
 * @param <T> El tipo de dato que se espera recibir como resultado de la operación asíncrona.
 *            Por ejemplo, puede ser un objeto de tipo `List<User>`, un `Boolean` o lo que se quiera.
 */

public interface FirestoreCallback<T> {

    /**
     * Método para manejar el resultado de la operación asíncrona.
     *
     * @param result El resultado de la operación Firestore. Este puede ser de cualquier tipo,
     *               como un objeto, una lista de objetos, o incluso un valor nulo si la operación falló.
     */
    void onCallBack(T result);
}
