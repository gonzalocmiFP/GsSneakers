package com.example.gssneakers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    EditText textoCorreo, textoContrasena;

    FirebaseAuth mAuth;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        textoCorreo = view.findViewById(R.id.correoLogin);
        textoContrasena = view.findViewById(R.id.contrasenaLogin);

        mAuth = FirebaseAuth.getInstance();

        Button aceptar = view.findViewById(R.id.botonAceptarLogin);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo, contrasena;

                correo = String.valueOf(textoCorreo.getText());
                contrasena = String.valueOf(textoContrasena.getText());

                if (TextUtils.isEmpty(correo.trim())) {
                    Toast.makeText(getContext(), "Introduzca el correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contrasena.trim())) {
                    Toast.makeText(getContext(), "Introduzca la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Inicio sesión con el proveedor Firebase Authentication
                mAuth.signInWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Inicio de sesión correcto.", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    Toast.makeText(getContext(), "Inicio de sesión fallido.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        Button cancelar = view.findViewById(R.id.botonCancelarLogin);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Button registrarse = view.findViewById(R.id.botonRegistrarseLogin);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RegisterFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}