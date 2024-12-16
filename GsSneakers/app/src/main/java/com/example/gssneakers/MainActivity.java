package com.example.gssneakers;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private Button carrito;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        splashScreen.setKeepOnScreenCondition(() -> false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.botonIniciarSesion);
        carrito = findViewById(R.id.botonCarritoCompra);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MenuFragment()).commit();
        }

        configureAuthStateListener();

        configureButtonLogin();
    }

    /**
     * Actualiza la interfaz según el usuario actual
     */
    private void configureAuthStateListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            updateAppBarBeforeLogin(currentUser);
        };
    }

    private void configureButtonLogin() {
        FirebaseAuth fb = FirebaseAuth.getInstance();
        FirebaseUser user = fb.getCurrentUser();

        // Si no es nulo, esta autenticado
        updateAppBarBeforeLogin(user);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new LoginFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new ShoppingCartFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int selectedItem = item.getItemId();

                    if (selectedItem == R.id.nav_favourite) {
                        selectedFragment = new FavoritesFragment();
                    } else if (selectedItem == R.id.nav_notification) {
                        selectedFragment = new NotificationsFragment();
                    } else if (selectedItem == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    } else {
                        selectedFragment = new MenuFragment();
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();

                    return true;
                }
    };

    private void updateAppBarBeforeLogin(FirebaseUser user) {
        if (user != null) {
            login.setVisibility(View.GONE);
            carrito.setVisibility(View.VISIBLE);
        } else {
            login.setVisibility(View.VISIBLE);
            carrito.setVisibility(View.GONE);
        }

    }

    /**
     * Agrega el AuthStateListener al iniciar la actividad
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    /**
     * Elimina el AuthStateListener al detener la actividad
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }
}