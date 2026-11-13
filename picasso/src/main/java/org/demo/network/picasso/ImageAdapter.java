package org.demo.network.picasso;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/** Grid of photos. The whole loading code is one Picasso chain in onBindViewHolder. */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    static class ImageHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        ImageHolder(ImageView image) {
            super(image);
            this.image = image;
        }
    }

    private Picasso picasso;
    private List<String> imageUrls = Collections.emptyList();

    public ImageAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    /** The screen builds a new Picasso after "Clear cache". */
    public void setPicasso(Picasso picasso) {
        this.picasso = picasso;
        notifyDataSetChanged();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView view = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        picasso.load(imageUrls.get(position))
                .placeholder(R.drawable.ic_dog_placeholder)
                .error(R.drawable.ic_broken_image)
                .fit()          // resize to the ImageView: centerCrop needs a target size
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
