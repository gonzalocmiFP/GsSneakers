package com.example.gssneakers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;
import com.example.gssneakers.database.User;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FirebaseUser user;

    private EditText nombre;
    private EditText correo;
    private EditText telefono;
    private EditText direccion;
    private ProgressBar progressBar;
    private LinearLayout iniciaSesion;
    private ExtendedFloatingActionButton btnLogOut;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    requireActivity().finish();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(requireContext(), "No se ha iniciado sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void initView(View view) {
        user = Database.recuperarUsuarioConectado();

        progressBar = view.findViewById(R.id.progressBarPerfil);
        iniciaSesion = view.findViewById(R.id.iniciarSesionPerfil);
        nombre = view.findViewById(R.id.nombrePerfil);
        correo = view.findViewById(R.id.correoPerfil);
        telefono = view.findViewById(R.id.telefonoPerfil);
        direccion = view.findViewById(R.id.direccionPerfil);
        btnLogOut = view.findViewById(R.id.btnLogOutPerfil);
        progressBar.setVisibility(View.GONE);

        if (user != null) {
            iniciaSesion.setVisibility(View.GONE);
            cargarUsuario(user.getUid());
        }
    }

    private void cargarUsuario(String userId) {
        progressBar.setVisibility(View.VISIBLE);

        Database.cargarDatosUsuario(userId, new FirestoreCallback<User>() {
            @Override
            public void onCallBack(User user) {
                progressBar.setVisibility(View.GONE);
                nombre.setText(user.getNombre());
                correo.setText(user.getEmail());
                telefono.setText(user.getTelefono());
                direccion.setText(user.getDireccion());
            }
        });
    }
}