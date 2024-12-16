package com.example.gssneakers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gssneakers.adapters.ProductAdapter;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {
    private static String TAG = "MenuFragment";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    public MenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Metodo auxiliar para rellenar/crear todos los datos en Firestore
        //rellenarBaseDatos();

        initView(view);

        cargarProductos();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.productsRecyclerView);
        progressBar = view.findViewById(R.id.progressBarMenu);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Método auxiliar para el desarrollo del TFG.
     * Lee el fichero bbdd.json con todos los productos creados.
     * Rellena los documentos con la información de los productos.
     */
    public void rellenarBaseDatos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Product> productos = null;

        System.out.println("rellenando base de datos");

        Gson gson = new Gson();

        try (InputStream is = this.getResources().openRawResource(R.raw.bbdd);
             InputStreamReader reader = new InputStreamReader(is)) {
            Type productListType = new TypeToken<List<Product>>() {
            }.getType();
            productos = gson.fromJson(reader, productListType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CollectionReference productosBBDD = db.collection("productos");

        for (Product product : productos) {
            Map<String, Object> data1 = new HashMap<>();
            data1.put("id", product.getId());
            data1.put("nombre", product.getNombre());
            data1.put("modelo", product.getModelo());
            data1.put("marca", product.getMarca());
            data1.put("color", product.getColor());
            data1.put("imagen", product.getImagen());
            data1.put("precio", product.getPrecio());
            data1.put("stock", product.getStock());
            data1.put("tallas", product.getTallas());

            productosBBDD.document(product.getId()).set(data1);
        }
    }

    public void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        Database.cargarProductos(new FirestoreCallback<ArrayList<Product>>() {
            @Override
            public void onCallBack(ArrayList<Product> result) {
                try {
                    RecyclerView.Adapter<ProductAdapter.ViewHolder> adapter = new ProductAdapter(result, requireActivity());

                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);
                } catch (IllegalStateException e) {
                    Log.w(TAG, "cargarProductos: ha lanzado un error al intentar cargar los productos. Error: ", e);
                    if (!e.getMessage().contains("not attached to an activity")) {
                        throw e;
                    }
                }
            }
        });
    }
}