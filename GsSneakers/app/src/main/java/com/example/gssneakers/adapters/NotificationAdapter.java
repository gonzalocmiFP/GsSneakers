package com.example.gssneakers.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gssneakers.MainActivity;
import com.example.gssneakers.NotificationFragment;
import com.example.gssneakers.R;
import com.example.gssneakers.database.Notification;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

/**
 * Clase que recoge la funcionalidad de los RecyclerView del fragment de notificaciones.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ArrayList<Notification> notifications;
    private Activity activity;

    /**
     * Contructor por defecto que recoge las notificaciones a mostrar
     * @param notificaciones lista de notificaciones
     * @param activity
     */
    public NotificationAdapter(ArrayList<Notification> notificaciones, Activity activity) {
        this.notifications = notificaciones;
        this.activity = activity;
    }

    /**
     * Representacion de la vista de la celda
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewImagen;
        private final TextView textViewTitulo;
        private final TextView textViewCuerpo;
        private final MaterialCardView notificationCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewImagen = itemView.findViewById(R.id.imagenNotification);
            textViewTitulo = itemView.findViewById(R.id.tituloNotification);
            textViewCuerpo = itemView.findViewById(R.id.cuerpoNotification);
            notificationCardView = itemView.findViewById(R.id.cardViewNotifications);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_notification_view, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Este metodo carga la informacion de la notificacion en la celda correspondiente. Tambien le da funcionalidad a la celda.
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Notification notification = notifications.get(position);
        viewHolder.textViewTitulo.setText(notification.getTitulo());
        viewHolder.textViewCuerpo.setText(notification.getCuerpo());

        Glide.with(activity.getApplication())
                .load(notification.getImagen())
                .into(viewHolder.imageViewImagen);

        viewHolder.notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) activity;

                Fragment newFragment = NotificationFragment.newInstance(notification);

                FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
