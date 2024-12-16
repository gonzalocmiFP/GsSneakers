package com.example.gssneakers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gssneakers.adapters.CustomTouchHelper;
import com.example.gssneakers.adapters.ShoppingCartAdapter;
import com.example.gssneakers.database.CartProduct;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.Product;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment {
    private static final String TAG = "SHOPPING_CART_FRAGMENT";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewIniciaSesion;
    private TextView textViewPrecio;
    private ArrayList<CartProduct> cartProducts;
    private ArrayList<Product> products;
    private String precio = "";

    public ShoppingCartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        initView(view);

        cargarPoductos();

        Button comprar = view.findViewById(R.id.botonComprar);
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartProducts.isEmpty()) {
                    Toast.makeText(requireContext(), "El carrito está vacio", Toast.LENGTH_SHORT).show();
                } else {
                    Fragment fragment = BuyFragment.newInstance(precio, cartProducts);
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.shoppingCartRecyclerView);

        progressBar = view.findViewById(R.id.progressBarShoppingCart);
        progressBar.setVisibility(View.GONE);

        textViewIniciaSesion = view.findViewById(R.id.textViewIniciaSesionShoppingCart);
        textViewIniciaSesion.setVisibility(View.GONE);

        textViewPrecio = view.findViewById(R.id.textViewPrecio);
    }

    /**
     * Esta funcion se encarga de leer los productos en bbdd que tiene en el carrito de compra el usuario conectado
     */
    private void cargarPoductos() {
        FirebaseUser usuarioConectado = Database.recuperarUsuarioConectado();

        if (usuarioConectado != null) {
            progressBar.setVisibility(View.VISIBLE);

            String userId = usuarioConectado.getUid();
            final ArrayList<CartProduct>[] carritoFirebase = new ArrayList[1];

            // Recoge información de del carrito que hay en el usuario en Firestore
            Database.cargarCarrito(userId,
                    // Devuelve el carrito
                    new FirestoreCallback<ArrayList<CartProduct>>() {
                        @Override
                        public void onCallBack(ArrayList<CartProduct> result) {
                            carritoFirebase[0] = result;
                            ShoppingCartFragment.this.cartProducts = result;
                        }
                    },
                    // Devuelve los datos completos de todos los productos del carrito
                    new FirestoreCallback<ArrayList<Product>>() {
                        @Override
                        public void onCallBack(ArrayList<Product> products) {
                            progressBar.setVisibility(View.GONE);

                            ShoppingCartFragment.this.products = products;
                            ShoppingCartAdapter shoppingCartAdapter = new ShoppingCartAdapter(ShoppingCartFragment.this, carritoFirebase[0], products, requireActivity());

                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            recyclerView.setAdapter(shoppingCartAdapter);

                            ItemTouchHelper itemTouchHelper = CustomTouchHelper.onSwipeDeleteCartSneacker(requireContext(), userId, shoppingCartAdapter);
                            itemTouchHelper.attachToRecyclerView(recyclerView);

                            double precio = 0;
                            for (Product product : products) {
                                precio += product.getPrecio();
                            }

                            actualizarPrecio(precio);
                        }
                    });
        } else {
            textViewIniciaSesion.setVisibility(View.VISIBLE);
            Log.e(TAG, "No hay ningun usuario conectado");
        }
    }

    /**
     * Actualiza el precio total del carrito
     * @param precioCalculado
     */
    public void actualizarPrecio(double precioCalculado){
        precio = String.format("%.2f", Float.valueOf(String.valueOf(precioCalculado))).concat("€");
        textViewPrecio.setText("PRECIO: " + precio);
    }
}