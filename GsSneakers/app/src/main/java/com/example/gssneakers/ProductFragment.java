package com.example.gssneakers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.Product;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Vista-Detalle del producto
 */
public class ProductFragment extends Fragment {
    private static String ARG_PRODUCT = "ARG_PRODUCT";

    private Product product;
    private boolean isFavorite;
    private String tallaSeleccionada;

    FirebaseUser firebaseUser = Database.recuperarUsuarioConectado();

    private ImageView imagenProductView;
    private TextView nombreProductView;
    private TextView marcaProductView;
    private TextView modeloProductView;
    private Spinner tallasSpinnerProductView;
    private ImageButton favoritoButtonProductView;
    private TextView precioProductView;
    private ExtendedFloatingActionButton anadirCarritoButtonProductView;

    public ProductFragment() {
    }

    public static ProductFragment newInstance(Product product) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        String productJson = new Gson().toJson(product);
        args.putString(ARG_PRODUCT, productJson);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String productJson = getArguments().getString(ARG_PRODUCT);
            product = new Gson().fromJson(productJson, Product.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        initView(view);

        if (product != null) {
            nombreProductView.setText(product.getNombre());
            marcaProductView.setText(product.getMarca());
            modeloProductView.setText(product.getModelo());

            // Recorre el stock de la zapatilla y define como disponible la talla si es superior a 1.
            ArrayList<String> tallasDisponibles = new ArrayList<>();
            // Map.Entry<String, Integer> --> Map.Entry<Talla, Cantidad>
            for (Map.Entry<String, Integer> stock : product.getStock().entrySet()){
                if (stock.getValue() > 0){
                    tallasDisponibles.add(stock.getKey());
                }
            }

            tallaSeleccionada = tallasDisponibles.get(0);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    tallasDisponibles
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tallasSpinnerProductView.setAdapter(adapter);
            tallasSpinnerProductView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tallaSeleccionada = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { /* NOTHING TO DO */}
            });


            favoritoButtonProductView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavorite = !isFavorite;
                    actualizarImgFav();
                    actualizarFavBBDD();
                }
            });

            precioProductView.setText(String.format("Precio: \n%.2f€", Float.valueOf(String.valueOf(product.getPrecio()))));

            anadirCarritoButtonProductView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agregarAlCarrito();
                }
            });
        }

        return view;
    }

    private void actualizarFavBBDD() {
        if (isFavorite){
            agregarFavorito();
        }else{
            eliminarFavorito();
        }
    }

    private void agregarAlCarrito() {
        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            Database.marcarCarrito(requireContext(), id, product.getId(), tallaSeleccionada);
        }
    }

    private void agregarFavorito() {
        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            Database.marcarFavorito(requireContext(), id, product.getId());
        }
    }

    private void eliminarFavorito() {
        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            Database.desmarcarFavorito(requireContext(), id, product.getId());
        }
    }

    private void initView(View view) {
        imagenProductView = view.findViewById(R.id.imagenProductView);
        Glide.with(requireContext())
                .load(product.getImagen())
                .into(imagenProductView);

        nombreProductView = view.findViewById(R.id.nombreProductView);
        marcaProductView = view.findViewById(R.id.marcaProductView);
        modeloProductView = view.findViewById(R.id.modeloProductView);
        tallasSpinnerProductView = view.findViewById(R.id.tallasSpinnerProductView);
        favoritoButtonProductView = view.findViewById(R.id.favoritoButtonProductView);

        if (firebaseUser != null) {
            Database.encontrarFavorito(firebaseUser.getUid(), product.getId(), new FirestoreCallback<Boolean>() {
                @Override
                public void onCallBack(Boolean result) {
                    isFavorite = result;
                    actualizarImgFav();
                }
            });
        }

        precioProductView = view.findViewById(R.id.precioProductView);

        anadirCarritoButtonProductView = view.findViewById(R.id.anadirCarritoButtonProductView);
    }

    private void actualizarImgFav() {
        Drawable nuevaImagen;
        if (isFavorite) {
            nuevaImagen = ContextCompat.getDrawable(requireContext(), R.drawable.favorite);
        } else {
            nuevaImagen = ContextCompat.getDrawable(requireContext(), R.drawable.favorite_border);
        }

        favoritoButtonProductView.setImageDrawable(nuevaImagen);
    }
}