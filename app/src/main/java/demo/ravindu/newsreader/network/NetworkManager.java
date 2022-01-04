package demo.ravindu.newsreader.network;

import static demo.ravindu.newsreader.pagination.PaginationListener.PAGE_SIZE;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import demo.ravindu.newsreader.BuildConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkManager {

    private static NetworkManager manager;
    private final OkHttpClient okHttpClient;
    private final String API = "https://newsapi.org/v2/everything?";

    private NetworkManager() {
        okHttpClient = new OkHttpClient();
    }

    public static NetworkManager getInstance() {
        if (manager == null) {
            manager = new NetworkManager();
        }
        return manager;
    }

    public void initiateRequest(String currentQuery, int currentPage, NewsResultListener listener) {
        try {
            // if there are spaces in query, they'll be encoded before appending to url
            currentQuery = URLEncoder.encode(currentQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // non-variable values have been hardcoded to url
        String url = API + "q=" + currentQuery + "&sortBy=relevancy" +
                "&language=en&page=" + currentPage + "&pageSize=" + PAGE_SIZE;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", BuildConfig.NEWS_API_KEY)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                listener.onSuccess(response.body().string());
            }
        });
    }
}
