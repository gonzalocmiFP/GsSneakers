package com.example.gssneakers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gssneakers.adapters.NotificationAdapter;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.Notification;

import java.util.ArrayList;

/**
 * Vista resumen con TODAS las notificaciones.
 */
public class NotificationsFragment extends Fragment {
    private static final String TAG = "SHOPPING_CART_FRAGMENT";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public NotificationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initView(view);

        cargarPoductos();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerNoticias);

        progressBar = view.findViewById(R.id.progressBarNoticias);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Esta funcion se encarga de leer los productos en bbdd que tiene en el carrito de compra el usuario conectado
     */

    private void cargarPoductos() {
        progressBar.setVisibility(View.VISIBLE);

        Database.cargarNoticias(new FirestoreCallback<ArrayList<Notification>>() {
            @Override
            public void onCallBack(ArrayList<Notification> result) {
                RecyclerView.Adapter<NotificationAdapter.ViewHolder> adapter = new NotificationAdapter(result, requireActivity());
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            }
        });
    }
}