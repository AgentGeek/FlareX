package uk.redcode.flarex.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.inflater = LayoutInflater.from(context);
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_community_category, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView name;
        final TextView description;
        final ChipGroup subcategories;
        final View colorBar;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            description = itemView.findViewById(R.id.category_description);
            subcategories = itemView.findViewById(R.id.category_subcategories);
            colorBar = itemView.findViewById(R.id.category_color);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //if (listener != null) listener.onDNSSelected(records.get(getAdapterPosition()));
        }

        public void bind(Category category) {
            name.setText(category.name);
            description.setText(Html.fromHtml(category.description));
            colorBar.setBackgroundColor(Color.parseColor(category.color));

            subcategories.removeAllViews();
            for (Category.SubCategory cat : category.subCategories) {
                Chip chip = new Chip(context);
                chip.setText(cat.name);
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(category.color)));
                subcategories.addView(chip);
            }
        }
    }
}
