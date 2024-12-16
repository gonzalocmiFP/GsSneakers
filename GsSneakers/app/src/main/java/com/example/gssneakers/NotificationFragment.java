package com.example.gssneakers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.gssneakers.database.Notification;
import com.google.gson.Gson;

/**
 * Vista-Detalle de la notificacion
 */
public class NotificationFragment extends Fragment {
    private static final String ARG_NOTIFICATION = "ARG_NOTIFICATION";

    private Notification notification;
    private ImageView imagenNotification;
    private TextView tituloNotification;
    private TextView cuerpoNotification;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(Notification notification) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        String notificationJson = new Gson().toJson(notification);
        args.putString(ARG_NOTIFICATION, notificationJson);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String notificationJson = getArguments().getString(ARG_NOTIFICATION);

            notification = new Gson().fromJson(notificationJson, Notification.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        imagenNotification = view.findViewById(R.id.imagenNotification);
        Glide.with(requireActivity().getApplication())
                .load(notification.getImagen())
                .into(imagenNotification);
        tituloNotification = view.findViewById(R.id.tituloNotification);
        cuerpoNotification = view.findViewById(R.id.cuerpoNotification);

        tituloNotification.setText(notification.getTitulo());
        cuerpoNotification.setText(notification.getCuerpo());

        return view;
    }
}