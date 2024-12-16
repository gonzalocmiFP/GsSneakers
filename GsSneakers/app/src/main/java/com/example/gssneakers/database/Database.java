package com.example.gssneakers.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {
    public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DATABASE";

    public static FirebaseUser recuperarUsuarioConectado() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Recuperamos y devolvemos todos los productos incluidos en la lista de ids que nos envien
     *
     * @param ids    id de los productos a recuperar
     * @param result productos completos de la coleccion "productos"
     */
    private static void cargarProductosPorId(ArrayList<String> ids, FirestoreCallback<ArrayList<Product>> result) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<Product> products = new ArrayList<>();

        if (!ids.isEmpty()) {
            db.collection("productos")
                    .whereIn("id", ids)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Product product = document.toObject(Product.class);
                                    products.add(product);

                                    Log.d(TAG, "PRODUCTO - ID:" + document.getId() + " => " + product);
                                }

                                result.onCallBack(products);
                            } else {
                                result.onCallBack(new ArrayList<>()); // Devolvemos una lista vacia
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            result.onCallBack(products);
        }

    }

    /**
     * Recupera el listado de todos los productos guardados en la colección de Firestore de "productos".
     *
     * @param result FirestoreCallback que contiene la respuesta traída por Firebase.
     */
    public static void cargarProductos(FirestoreCallback<ArrayList<Product>> result) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<Product> products = new ArrayList<>();

        db.collection("productos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        products.add(product);

                        Log.d(TAG, document.getId() + " => " + product);
                    }

                    result.onCallBack(products);
                } else {
                    result.onCallBack(new ArrayList<>()); // Devolvemos una lista vacia
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Añade el producto a los favoritos del usuario conectado.
     *
     * @param context   context
     * @param userId    userId del usuario conectado
     * @param productId productId del producto "favorito"
     */
    public static void marcarFavorito(Context context, String userId, String productId) {
        DocumentReference productRef = db.collection("productos").document(productId);

        HashMap<String, Object> referenciaProducto = new HashMap<String, Object>() {{
            put("producto", productRef);
        }};

        db.collection("usuarios").document(userId)
                .collection("favoritos").document(productId).set(referenciaProducto).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Guardado en favoritos", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al guardar en favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Elimina el producto a los favoritos del usuario conectado.
     *
     * @param context   context
     * @param userId    userId del usuario conectado
     * @param productId productId del producto "favorito"
     */
    public static void desmarcarFavorito(Context context, String userId, String productId) {
        db.collection("usuarios").document(userId)
                .collection("favoritos").document(productId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar en favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Busca a través del id del usuario y producto, el producto que está dentro de la colección del favoritos del usuario.
     *
     * @param userId    userId del usuario conectado
     * @param productId productId del producto "favorito"
     * @param callback  "return" asincrono para cuando se complete el onComplete
     */
    public static void encontrarFavorito(String userId, String productId, FirestoreCallback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(userId)
                .collection("favoritos")
                .whereEqualTo("producto", db.collection("productos").document(productId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onCallBack(exists);
                        /*
                        Callback representa un "return" que devuelve los datos de cuando llamamos a onCallBack().
                         */
                    } else {
                        callback.onCallBack(false);
                    }
                });
    }

    /**
     * Añade el producto al carrito del usuario conectado.
     *
     * @param context   context
     * @param userId    userId del usuario conectado
     * @param talla     talla seleccionada en la vista {@link com.example.gssneakers.ProductFragment}
     * @param productId productId del producto
     */
    public static void marcarCarrito(Context context, String userId, String productId, String talla) {
        DocumentReference productRef = db.collection("productos").document(productId);

        // Representa los datos que se situan en el documento de firestore
        HashMap<String, Object> referenciaProducto = new HashMap<String, Object>() {{
            put("producto", productRef);
            put("talla", talla);
            put("cantidad", 1);
            put("id", productId);
        }};

        db.collection("usuarios").document(userId)
                .collection("carrito").document(productId).set(referenciaProducto).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Guardado en carrito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al guardar en carrito", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Elimina el producto al carrito del usuario conectado.
     *
     * @param context   context
     * @param userId    userId del usuario conectado
     * @param productId productId del producto
     */
    public static void desmarcarCarrito(Context context, String userId, String productId) {
        db.collection("usuarios").document(userId)
                .collection("carrito").document(productId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Eliminado de carrito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar en carrito", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void encontrarCarrito(String userId, String productId, FirestoreCallback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(userId)
                .collection("carrito")
                .whereEqualTo("producto", db.collection("productos").document(productId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onCallBack(exists);
                    } else {
                        callback.onCallBack(false);
                    }
                });
    }

    /**
     * Funcion que lee todos los ids de los productos favoritos de un usuario y luego esos ids,
     * los busca en la colleccion "productos", asi recuperamos toda la info del producto "favorito"
     *
     * @param userId   id del usuario conectado
     * @param callback return con los productos marcados como favoritos
     */
    public static void cargarFavoritos(String userId, FirestoreCallback<ArrayList<Product>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(userId)
                .collection("favoritos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> ids = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ids.add(document.getId());
                        }

                        // Despues de haber leido todos los ids, los buscamos en "productos"
                        Database.cargarProductosPorId(ids, new FirestoreCallback<ArrayList<Product>>() {
                            @Override
                            public void onCallBack(ArrayList<Product> result) {
                                callback.onCallBack(result);
                            }
                        });
                    } else {
                        callback.onCallBack(new ArrayList<>());
                    }
                });
    }

    /**
     * Carga el carrito e información de todos los productos del carrito.
     *
     * @param userId               id del usuario
     * @param callbackListaCarrito return de la lista de productos en el carrito ({@link CartProduct})
     * @param callbackProductos    return de los productos con su información completa (solo productos del carrito)
     */
    public static void cargarCarrito(String userId, FirestoreCallback<ArrayList<CartProduct>> callbackListaCarrito, FirestoreCallback<ArrayList<Product>> callbackProductos) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(userId)
                .collection("carrito")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> ids = new ArrayList<>();
                        ArrayList<CartProduct> cartProducts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            ids.add(id);

                            CartProduct cartProduct = new CartProduct();
                            cartProduct.setId(id);
                            cartProduct.setReferenceToRealProduct(document.getDocumentReference("producto"));
                            cartProduct.setCantidad(Integer.parseInt(String.valueOf(document.get("cantidad"))));
                            cartProduct.setTalla(String.valueOf(document.get("talla")));

                            cartProducts.add(cartProduct);
                        }

                        // Devolvemos el carrito del usuario (tal cual esta en firebase, lo necesitamos por las tallas y cantidad seleccionadas)
                        callbackListaCarrito.onCallBack(cartProducts);

                        // Despues de haber leido todos los ids, los buscamos en "productos"
                        Database.cargarProductosPorId(ids, new FirestoreCallback<ArrayList<Product>>() {
                            @Override
                            public void onCallBack(ArrayList<Product> result) {
                                callbackProductos.onCallBack(result);
                            }
                        });
                    } else {
                        callbackListaCarrito.onCallBack(new ArrayList<>());
                        callbackProductos.onCallBack(new ArrayList<>());
                    }
                });
    }

    /**
     * Actualiza el producto
     *
     * @param product product
     */
    public static void actualizarProducto(Product product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> updateParams = Map.of(
                "stock", product.getStock()
        );

        db.collection("productos").document(product.getId()).update(updateParams)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al actualizar producto", e);
                    }
                });
    }

    /**
     * Actualiza el carrito
     *
     * @param userId
     * @param cartProduct
     */
    public static void actualizarCarrito(String userId, CartProduct cartProduct) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> updateParams = Map.of(
                "cantidad", cartProduct.getCantidad()
        );

        db.collection("usuarios").document(userId).collection("carrito")
                .document(cartProduct.getId()).update(updateParams)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al actualizar carrito", e);
                    }
                });
    }

    /**
     * Eliminar articulo del carrito
     *
     * @param userId
     * @param cartProductId
     * @param result        FirestoreCallback que simula el return con la info de si se ha borrado bien o no
     */
    public static void eliminarArticuloDelCarrito(String userId, String cartProductId, FirestoreCallback<Boolean> result) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(userId).collection("carrito")
                .document(cartProductId).delete()
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                result.onCallBack(task.isSuccessful());
                            }
                        }
                );
    }

    /**
     * Recupera las noticias
     *
     * @param result "Return" con las noticias
     */
    public static void cargarNoticias(FirestoreCallback<ArrayList<Notification>> result) {
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        ArrayList<Notification> noticias = new ArrayList<>();

        fb.collection("noticias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Notification noticia = document.toObject(Notification.class);
                        noticias.add(noticia);

                        Log.d(TAG, document.getId() + " => " + noticia.getTitulo());
                    }

                    result.onCallBack(noticias);
                } else {
                    result.onCallBack(noticias); // Devolvemos una lista vacia
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Añade la compra a la collecion "historial_compras" con los datos de la compra.
     * Borramos todos los productos añadidos al carrito uno a uno, puesto que compras todo el carrito.
     *
     * @param userId
     * @param cartProducts
     * @param precio       precio de todo el carrito
     * @param result       "return" con la respuesta de si se ha borrado bien todo o no
     */
    public static void comprarCarrito(String userId, ArrayList<CartProduct> cartProducts, String precio, FirestoreCallback<Boolean> result) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> datosCompra = Map.ofEntries(
                Map.entry("timestampt", LocalDateTime.now()),
                Map.entry("carritoComprado", cartProducts.toString()),
                Map.entry("precioTotal", precio)
        );

        db.collection("usuarios").document(userId).collection("historial_compras").document().set(datosCompra).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "datos de la compra: " + datosCompra);
                Log.e(TAG, "Algo ha ido mal guardando la compra, error: {}", e);
            }
        });

        db.collection("usuarios").document(userId).collection("carrito").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection("usuarios")
                                .document(userId)
                                .collection("carrito")
                                .document(document.getId())
                                .delete(); // Borrar cada documento individualmente
                    }

                    result.onCallBack(task.isSuccessful());
                }
            }
        });

    }

    /**
     * Recupera toda la informacion del usuario
     *
     * @param userId userId
     * @param result "return" con el objeto {@link User}
     */
    public static void cargarDatosUsuario(String userId, FirestoreCallback<User> result) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    result.onCallBack(user);
                }
            }
        });
    }
}