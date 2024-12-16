package com.example.gssneakers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gssneakers.database.CartProduct;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;

public class BuyFragment extends Fragment {
    private static String ARG_BUY_PRECIO = "ARG_BUY_PRECIO";
    private static String ARG_BUY_CART_PRODUCTS = "ARG_BUY_CART_PRODUCTS";

    private RadioGroup opciones;
    private MaterialRadioButton opcionPayPal;
    private MaterialRadioButton opcionCredito;
    private String precio;
    private ArrayList<CartProduct> cartProducts;

    public BuyFragment() {
    }

    /**
     * Metodo estatico para crear la vista y enviarle un parametro que se comportará como argumento.
     *
     * @param precio argumento ARG_BUY_PRECIO
     * @param cartProducts argummento ARG_BUY_CART_PRODUCTS
     * @return Fragment con los argumentos cargados
     */
    public static BuyFragment newInstance(String precio, ArrayList<CartProduct> cartProducts) {
        BuyFragment fragment = new BuyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUY_PRECIO, precio);
        String cartProdductsJson = new Gson().toJson(cartProducts);
        args.putString(ARG_BUY_CART_PRODUCTS, cartProdductsJson);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            precio = getArguments().getString(ARG_BUY_PRECIO);

            String cartProductsJson = getArguments().getString(ARG_BUY_CART_PRODUCTS);
            cartProducts = new Gson().fromJson(cartProductsJson, ArrayList.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        opciones = view.findViewById(R.id.options);
        opcionPayPal = view.findViewById(R.id.paypalOption);
        opcionCredito = view.findViewById(R.id.cardOption);

        opcionPayPal.setChecked(true);

        Button aceptar = view.findViewById(R.id.botonAceptarCompra);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprarCarrito();
            }
        });

        Button cancelar = view.findViewById(R.id.botonCancelarCompra);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ShoppingCartFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    /**
     * Llamamos a Database para gestionar la compra del carrito. Si todo ha ido OK, viajamos a {@link MenuFragment}
     */
    private void comprarCarrito() {
        FirebaseUser user = Database.recuperarUsuarioConectado();
        if (user != null) {
            Database.comprarCarrito(user.getUid(), cartProducts, precio, new FirestoreCallback<Boolean>() {
                @Override
                public void onCallBack(Boolean ok) {
                    if (ok) {
                        Toast.makeText(getContext(), "Compra realizada correctamente", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new MenuFragment();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(getContext(), "La compra no se ha realizado correctamente.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}