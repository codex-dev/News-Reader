package demo.ravindu.newsreader.pagination;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.List;

import demo.ravindu.newsreader.R;
import demo.ravindu.newsreader.model.Article;
import demo.ravindu.newsreader.util.DateFormatter;
import demo.ravindu.newsreader.util.TextFormatter;
import demo.ravindu.newsreader.view.ActPreviewNews;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private final Context context;
    private final List<Article> listNews;
    private boolean isLoaderVisible = false;

    public NewsRecyclerAdapter(Context context, List<Article> postItems) {
        this.context = context;
        this.listNews = postItems;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == listNews.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return listNews == null ? 0 : listNews.size();
    }

    public void addItems(List<Article> postItems) {
        listNews.addAll(postItems);
        notifyDataSetChanged();
//        notifyItemRangeChanged(0, listNews.size() - 1);
    }

    public void addLoading() {
        isLoaderVisible = true;
        listNews.add(new Article());
        notifyItemInserted(listNews.size() - 1);
    }

    public void clear() {
        listNews.clear();
        notifyDataSetChanged();
//        notifyItemRangeChanged(0, listNews.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = listNews.size() - 1;
        Article item = getItem(position);
        if (item != null) {
            listNews.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Article getItem(int position) {
        return listNews.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void onBind(int position) {
            super.onBind(position);
            Article item = listNews.get(position);

            if (!TextFormatter.isNullOrEmpty(item.getUrlToImage())) {
                Glide.with(context).load(listNews.get(position).getUrlToImage()).error(R.drawable.img_placeholder).into(ivThumbnail);
            }

            try {
                tvDate.setText(DateFormatter.convertTimeFromUtcToLocal("dd MMM yyyy", item.getPublishedAt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ActPreviewNews.class);
                    i.putExtra("image", item.getUrlToImage());
                    i.putExtra("source", item.getSource().getName());
                    i.putExtra("date", item.getPublishedAt());
                    i.putExtra("title", item.getTitle());
                    i.putExtra("desc", item.getDescription());
                    i.putExtra("content", item.getContent());
                    i.putExtra("url", item.getUrl());
                    context.startActivity(i);
                }
            });
        }
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }
    }
}
