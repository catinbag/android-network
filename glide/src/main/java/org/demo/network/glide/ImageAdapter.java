package org.demo.network.glide;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/** Grid of photos. The whole loading code is one Glide chain in onBindViewHolder. */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    static class ImageHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        ImageHolder(ImageView image) {
            super(image);
            this.image = image;
        }
    }

    private List<String> imageUrls = Collections.emptyList();

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
        Glide.with(holder.image)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.ic_dog_placeholder)
                .error(R.drawable.ic_broken_image)
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
