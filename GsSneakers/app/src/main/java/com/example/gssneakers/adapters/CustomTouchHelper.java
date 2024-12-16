package com.example.gssneakers.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gssneakers.R;
import com.example.gssneakers.database.Database;
import com.example.gssneakers.database.FirestoreCallback;

/**
 * Clase que recoge el comportameinto de eliminación de items en los RecyclerView
 */
public class CustomTouchHelper {

    /**
     * Constructor privado para que la clase solo se comporte de manera estatica. No queremos que se creen objetos.
     */
    private CustomTouchHelper() {
    }

    /**
     * Eliminar producto del carrito al deslizar el item del RecyclerView a la derecha
     * @param context Contexto
     * @param userId Id del usuario que esta conectado
     * @param adapter Adapter al que vamos a "enganchar" el onSwipe
     * @return
     */
    public static ItemTouchHelper onSwipeDeleteCartSneacker(Context context, String userId, ShoppingCartAdapter adapter) {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int posicionActual = viewHolder.getAdapterPosition();
                String cartProductId = adapter.getCartIdAt(posicionActual);

                Database.eliminarArticuloDelCarrito(userId, cartProductId, new FirestoreCallback<Boolean>() {
                    @Override
                    public void onCallBack(Boolean result) {
                        if (result) {
                            adapter.removeAt(posicionActual);
                            Toast.makeText(context, "Eliminado del carrito", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Drawable icon = ContextCompat.getDrawable(context, R.drawable.delete);

                ColorDrawable background = new ColorDrawable(context.getColor(R.color.red));

                View itemView = viewHolder.itemView;

                assert icon != null;

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                // Deslizar a la derecha
                if (dX > 0) {
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicHeight();

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    int dxInt = (int) dX;

                    background.setBounds(
                            itemView.getLeft(),
                            itemView.getTop(),
                            itemView.getLeft() + dxInt,
                            itemView.getBottom()
                    );
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
    }
}


