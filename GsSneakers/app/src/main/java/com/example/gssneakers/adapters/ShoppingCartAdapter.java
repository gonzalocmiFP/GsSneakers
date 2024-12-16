package com.example.gssneakers.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gssneakers.MainActivity;
import com.example.gssneakers.ProductFragment;
import com.example.gssneakers.R;
import com.example.gssneakers.ShoppingCartFragment;
import com.example.gssneakers.database.CartProduct;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.Product;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
    private final ShoppingCartFragment parentFragment;
    private ArrayList<Product> productList;
    private ArrayList<CartProduct> carrito;
    private HashMap<String, Product> relacionIdPorProducto = new HashMap<>();
    private String userId;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewMarca;
        private final TextView textViewPrecio;
        private final ImageView imageViewImagen;

        protected final ImageButton imageButtonMenos;
        protected final ImageButton imageButtonMas;
        protected final TextView textViewCantidad;

        protected MaterialCardView cardviewProduct;

        public ViewHolder(View view) {
            super(view);
            textViewNombre = view.findViewById(R.id.nombreShoppingCart);
            textViewMarca = view.findViewById(R.id.marcaShoppingCart);
            textViewPrecio = view.findViewById(R.id.precioShoppingCart);
            textViewCantidad = view.findViewById(R.id.cantidadShoppingCart);

            imageButtonMenos = view.findViewById(R.id.botonMenosShoppingCart);
            imageButtonMas = view.findViewById(R.id.botonMasShoppingCart);

            imageViewImagen = view.findViewById(R.id.imagenShoppingCart);
            cardviewProduct = view.findViewById(R.id.cardviewShoppingCart);
        }

        public TextView getTextViewNombre() {
            return textViewNombre;
        }

        public TextView getTextViewMarca() {
            return textViewMarca;
        }

        public TextView getTextViewPrecio() {
            return textViewPrecio;
        }

        public ImageView getImageViewImagen() {
            return imageViewImagen;
        }


    }

    /**
     * Este consctructor recoge la información a mostrar
     *
     * @param parent   Fragment creador. Es necesario para poder actualizar luego el precio global que se calcula dentro de este adapter.
     * @param carrito  lista de productos que tiene el usuario en el carrito
     * @param products lista de productos completos (con toda la información) de SOLO los productos que haya en el carrito
     * @param activity activity
     */
    public ShoppingCartAdapter(ShoppingCartFragment parent, ArrayList<CartProduct> carrito, ArrayList<Product> products, Activity activity) {
        this.carrito = carrito;
        this.productList = products;
        this.activity = activity;
        this.parentFragment = parent;
    }

    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cell_shopping_cart_view, viewGroup, false);

        return new ShoppingCartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingCartAdapter.ViewHolder viewHolder, final int position) {
        Product product = productList.get(position);

        CartProduct cartProduct = buscarProductoEnCarrito(product.getId());

        viewHolder.textViewNombre.setText(product.getNombre());
        viewHolder.textViewMarca.setText(product.getMarca());

        //Mensajes dependiendo de si está null el producto o no. En caso de null, no se puede continuar.
        if (cartProduct == null) {
            throw new IllegalStateException("No hemos encontrado el producto " + product + "en el carrito");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Tiene que haber un usuario conectado");
        }

        userId = user.getUid();

        String[] tallaSeleccionada = {cartProduct.getTalla()};
        int[] cantidadSeleccionada = {cartProduct.getCantidad()};

        actualizarComponentes(viewHolder, cantidadSeleccionada[0], product);

        Glide.with(activity.getApplication())
                .load(product.getImagen())
                .into(viewHolder.getImageViewImagen());


        viewHolder.imageButtonMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stockActual = product.getStock().getOrDefault(tallaSeleccionada[0], 0);

                if (stockActual > 1) {
                    cantidadSeleccionada[0] += 1;

                    cartProduct.setCantidad(cantidadSeleccionada[0]);

                    actualizarComponentes(viewHolder, cantidadSeleccionada[0], product);
                }

                // (talla, cantidadTotal) -> cantidadTotal - 1 => representa como se ha de comportar dicha accion.
                actualizarBBDD(tallaSeleccionada[0], product, cartProduct, (talla, cantidadTotal) -> cantidadTotal - 1);

                actualizarPrecioGlobal();
            }
        });

        viewHolder.imageButtonMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadSeleccionada[0] > 1) {
                    cantidadSeleccionada[0] -= 1;

                    cartProduct.setCantidad(cantidadSeleccionada[0]);

                    actualizarComponentes(viewHolder, cantidadSeleccionada[0], product);
                }

                actualizarBBDD(tallaSeleccionada[0], product, cartProduct, (talla, cantidadTotal) -> cantidadTotal + 1);

                actualizarPrecioGlobal();
            }
        });

        viewHolder.cardviewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) activity;

                Fragment newFragment = ProductFragment.newInstance(product);

                FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    /**
     * Actualiza el precio de ese Producto en el carrito
     *
     * @param viewHolder           viewHolder
     * @param cantidadSeleccionada Cantidad que haya marcado el usuario en su carrito
     * @param product              Producto con la informacion completa
     */
    private void actualizarComponentes(ViewHolder viewHolder, int cantidadSeleccionada, Product product) {
        String precio = String.format("%.2f", Float.valueOf(String.valueOf(product.getPrecio() * cantidadSeleccionada)));

        viewHolder.textViewPrecio.setText(precio.concat("€"));
        viewHolder.textViewCantidad.setText(String.valueOf(cantidadSeleccionada));
    }

    /**
     * Recoge la infomacion del producto y llama a Database para devolver el stock reservado al
     * stock principal del producto. Tambien elimina el producto del carrito del usuario.
     *
     * @param tallaSeleccionada Talla guardada en el carrito
     * @param product           Producto con toda la informacion
     * @param cartProduct       Datos relativos al producto dentro del carrito
     */
    public void eliminarDelCarrito(String tallaSeleccionada, Product product, CartProduct cartProduct) {
        int cantidad = product.getStock().get(tallaSeleccionada);

        product.getStock().put(tallaSeleccionada, cantidad + cartProduct.getCantidad());

        // Vamos a devolverle el stock reservado en el carrito
        Database.actualizarProducto(product);

        Database.eliminarArticuloDelCarrito(userId, cartProduct.getId(), new FirestoreCallback<Boolean>() {
            @Override
            public void onCallBack(Boolean successful) {
                if (successful) {
                    Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Primero sumamos o restamos la cantidad al stock.
     * Segundo con el stock actualizado, reflejamos los cambios en la colecion "productos" de bbdd.
     * Por ultimo tambien reflejamos los cambios en la coleccion "carrito" de bbdd.
     *
     * @param tallaSeleccionada talla guardada en el carrito
     * @param product           Producto con toda la informacion
     * @param cartProduct       Datos relativos al producto dentro del carrito
     * @param accion            Operacion a realizar, definida en la llamada. Por ej. cantidadTotal - 1
     */
    private void actualizarBBDD(
            String tallaSeleccionada, Product product, CartProduct cartProduct,
            BiFunction</*Talla*/String, /*Cantidad*/Integer, /*Resultado de lo que hagamos*/Integer> accion) {

        /*
         * En caso de que el stock contenga la talla deseada. Ejecutaremos la accion deseada.
         * En caso de venir de imageButtonMenos ejecutaremos lo enviado -> cantidadTotal - 1
         * En caso de venir de imageButtonMas ejecutaremos lo enviado -> cantidadTotal + 1
         */
        product.getStock().computeIfPresent(tallaSeleccionada, accion);

        // Actualizamos el producto liverando o reservando productos.
        Database.actualizarProducto(product);

        // Actualizamos el carrito con la nueva info
        Database.actualizarCarrito(userId, cartProduct);
    }

    /**
     * Genera un mapa donde la clave es el id del producto y el valor, ese mismo producto.
     * Se usa para agilizar las busquedas donde tenemos el id por parte carrito y queremos buscar los datos completos del producto.
     */
    private void actualizarRelacionIdPorProducto() {
        relacionIdPorProducto.clear();

        for (Product product : productList) {
            relacionIdPorProducto.put(product.getId(), product);
        }
    }

    /**
     * Recorremos el carrito y vamoms preguntando por el Id hasta que encontramos en el carrito el producto buscado.
     * Se usa cuando necesitamos saber la talla o la cantidad del producto dentro del carrito
     *
     * @param id id del producto a buscar dentro del carrito
     * @return CartProducto con los datos del producto dentro del carrito. Null si no lo hemos encontrado.
     */
    private CartProduct buscarProductoEnCarrito(String id) {
        for (CartProduct cartProduct : carrito) {
            if (cartProduct.getId().equals(id)) {
                return cartProduct;
            }
        }

        return null;
    }

    /**
     * Método que elimina el producto del carrito.
     * Elimina el producto del carrito, llama a un metodo para devolver el stock reservado al stock principal y modifica el precio total del carrito.
     *
     * @param posicionActual hace referencia a la posición del producto en el RecyclerView del carrito
     */
    public void removeAt(int posicionActual) {
        Product product = productList.get(posicionActual);
        actualizarRelacionIdPorProducto();
        CartProduct cartProduct = buscarProductoEnCarrito(product.getId());

        if (cartProduct != null) {
            productList.remove(posicionActual);
            carrito.remove(posicionActual);
            notifyItemRangeRemoved(posicionActual, 1); // 1 = cantidad eliminada
            eliminarDelCarrito(cartProduct.getTalla(), product, cartProduct);
        }

        actualizarPrecioGlobal();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public String getCartIdAt(int posicion) {
        return productList.get(posicion).getId();
    }

    /**
     * Llamada al fragmento padre ({@link ShoppingCartFragment}) para actualizar el precio global.
     */
    private void actualizarPrecioGlobal() {
        actualizarRelacionIdPorProducto();

        double precio = 0;

        for (Map.Entry<String, Product> productEntry : relacionIdPorProducto.entrySet()) {
            CartProduct cartProduct = buscarProductoEnCarrito(productEntry.getKey());

            if (cartProduct != null) {
                precio += cartProduct.getCantidad() * productEntry.getValue().getPrecio();
            }
        }

        parentFragment.actualizarPrecio(precio);
    }
}
