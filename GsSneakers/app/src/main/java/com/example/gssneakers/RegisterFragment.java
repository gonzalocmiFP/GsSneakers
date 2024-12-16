package com.example.gssneakers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gssneakers.database.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {
    EditText textoUsuario, textoCorreo,  textoTelefono,  textoDireccion, textoContrasena;

    FirebaseAuth mAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();

        textoUsuario = view.findViewById(R.id.nombreRegistro);
        textoCorreo = view.findViewById(R.id.correoRegistro);
        textoDireccion = view.findViewById(R.id.direccionRegistro);
        textoTelefono = view.findViewById(R.id.telefonoRegistro);
        textoContrasena = view.findViewById(R.id.contrasenaRegistro);

        Button aceptar = view.findViewById(R.id.botonAceptarRegistro);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = String.valueOf(textoUsuario.getText());
                String correo = String.valueOf(textoCorreo.getText());
                String direccion = String.valueOf(textoDireccion.getText());
                String telefono = String.valueOf(textoTelefono.getText());
                String contrasena = String.valueOf(textoContrasena.getText());

                if (TextUtils.isEmpty(usuario)) {
                    Toast.makeText(getContext(), "Introduzca el nombre de usuario", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(correo)) {
                    Toast.makeText(getContext(), "Introduzca el correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(direccion)) {
                    Toast.makeText(getContext(), "Introduzca la dirección", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(telefono)) {
                    Toast.makeText(getContext(), "Introduzca el número de teléfono", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(getContext(), "Introduzca la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String id = task.getResult().getUser().getUid();

                                    User user = new User(id, usuario, correo, direccion, telefono, contrasena);

                                    guardarUsuario(user);
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                    Toast.makeText(getContext(), "Error en el registro: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        Button cancelar = view.findViewById(R.id.botonCancelarRegistro);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    /**
     * Método para guardar en Firestore la información del usuario añadida en el registro.
     * En caso de error, no swe guardará en Firestore el usuario, y se borra de el Firestore Authentication.
     * @param user
     */
    private void guardarUsuario(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usuariosBBDD = db.collection("usuarios");

        usuariosBBDD.document(user.getId()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();

                    mAuth.signOut();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    // como el registro no ha sido satisfactorio, borramos el usuario creado. No se han guardado bien los datos en Firestore
                    mAuth.signInWithEmailAndPassword(user.getEmail(), user.getContrasena()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               mAuth.getCurrentUser().delete();
                           }
                        }
                    });
                }
            }
        });
    }
}