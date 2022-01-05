package demo.ravindu.newsreader.network;

import static demo.ravindu.newsreader.pagination.PaginationListener.PAGE_SIZE;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import demo.ravindu.newsreader.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkManager {

    private static NetworkManager manager;
    private final OkHttpClient okHttpClient;
    private final Context context;

    private NetworkManager(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient();
    }

    public static NetworkManager getInstance(Context context) {
        // singleton pattern - only one instance is being used at any given moment.
        if (manager == null) {
            manager = new NetworkManager(context);
        }
        return manager;
    }

    public void initiateRequest(String query, int currentPage, NewsResultListener listener) {
        try {
            // if there are spaces in query, they'll be encoded before appending to url
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // values of sortBy and language query parameters have been hardcoded to url
        String url = "https://newsapi.org/v2/everything?"
                + "q=" + query
                + "&sortBy=relevancy"
                + "&language=en"
                + "&page=" + currentPage
                + "&pageSize=" + PAGE_SIZE;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", context.getString(R.string.api_key))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                listener.onSuccess(response.body().string());
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                listener.onError(e.getMessage());
            }
        });
    }
}
