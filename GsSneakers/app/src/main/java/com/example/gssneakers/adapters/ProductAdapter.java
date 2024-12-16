package com.example.gssneakers.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gssneakers.MainActivity;
import com.example.gssneakers.ProductFragment;
import com.example.gssneakers.R;
import com.example.gssneakers.database.Product;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> productList;
    private Activity activity;

    /**
     * Rpresentración de la vista en la celda de producto.
     * (custom ViewHolder)
     */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewMarca;
        private final TextView textViewPrecio;
        private final ImageView imageViewImagen;
        private final  MaterialCardView cardviewProduct;

        public ViewHolder(View view, Activity activity) {
            super(view);

            textViewNombre = view.findViewById(R.id.nombreProduct);
            textViewMarca = view.findViewById(R.id.marcaProduct);
            textViewPrecio = view.findViewById(R.id.precioProduct);
            imageViewImagen = view.findViewById(R.id.imagenProduct);
            cardviewProduct = view.findViewById(R.id.cardviewProduct);
        }

        public TextView getTextViewNombre() {
            return textViewNombre;
        }

        public TextView getTextViewMarca() {
            return textViewMarca;
        }

        public TextView getTextViewPrecio() {
            return textViewPrecio;
        }

        public ImageView getImageViewImagen() {
            return imageViewImagen;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public ProductAdapter(ArrayList<Product> dataSet, Activity activity) {
        productList = dataSet;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_product_view, viewGroup, false);

        return new ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Product product = productList.get(position);

        viewHolder.getTextViewNombre().setText(product.getNombre());
        viewHolder.getTextViewMarca().setText(product.getMarca());

        String precio = String.format( "%.2f", Float.valueOf(String.valueOf(product.getPrecio())));

        viewHolder.getTextViewPrecio().setText(precio.concat("€"));

        Glide.with(activity.getApplication())
                .load(product.getImagen())
                .into(viewHolder.getImageViewImagen());

        viewHolder.cardviewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) activity;

                Fragment newFragment = ProductFragment.newInstance(product);

                FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}