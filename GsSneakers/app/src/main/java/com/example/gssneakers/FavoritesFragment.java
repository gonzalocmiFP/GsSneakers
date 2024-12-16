package com.example.gssneakers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gssneakers.adapters.ProductAdapter;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.Product;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private static final String TAG = "FAVORITES_FRAGMENT";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewIniciaSesion;

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initView(view);
        cargarProductos();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.favoritesRecyclerView);

        progressBar = view.findViewById(R.id.progressBarFavorites);
        progressBar.setVisibility(View.GONE);

        textViewIniciaSesion = view.findViewById(R.id.textViewIniciaSesionFavorites);
        textViewIniciaSesion.setVisibility(View.GONE);
    }

    private void cargarProductos() {
        FirebaseUser usuarioConectado = Database.recuperarUsuarioConectado();

        if (usuarioConectado != null) {
            progressBar.setVisibility(View.VISIBLE);

            String userId = usuarioConectado.getUid();

            Database.cargarFavoritos(userId, new FirestoreCallback<ArrayList<Product>>() {
                @Override
                public void onCallBack(ArrayList<Product> result) {
                    progressBar.setVisibility(View.GONE);

                    RecyclerView.Adapter<ProductAdapter.ViewHolder> adapter = new ProductAdapter(result, requireActivity());

                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(adapter);
                }
            });
        } else {
            textViewIniciaSesion.setVisibility(View.VISIBLE);
            Log.e(TAG, "No hay ningun usuario conectado");
        }
    }
}