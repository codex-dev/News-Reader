package demo.ravindu.newsreader.view;

import static demo.ravindu.newsreader.pagination.PaginationListener.PAGE_START;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;

import demo.ravindu.newsreader.R;
import demo.ravindu.newsreader.model.NewsResponse;
import demo.ravindu.newsreader.network.NetworkManager;
import demo.ravindu.newsreader.network.NewsResultListener;
import demo.ravindu.newsreader.pagination.NewsRecyclerAdapter;
import demo.ravindu.newsreader.pagination.PaginationListener;
import demo.ravindu.newsreader.util.TextFormatter;

// https://developer.android.com/guide/topics/search/adding-recent-query-suggestions#java
public class ActSearchNews extends AppCompatActivity
        implements View.OnFocusChangeListener, View.OnClickListener {

    private static final String TAG = "ActSearchNews";

    private NewsRecyclerAdapter adapter;
    private LinearLayoutManager layoutManager;

    private String currentQuery = "";
    private int currentPage = PAGE_START;
    private boolean isLoading = false;

    private TextInputEditText etSearch;
    private TextView btnCancel;
    private RecyclerView rvNews;

    private final TextWatcher twSearch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            btnCancel.setVisibility(!TextFormatter.isNullOrEmpty(etSearch) ? View.VISIBLE : View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search_news);

        initViews();
        setEventListeners();
    }

    private void initViews() {
        btnCancel = findViewById(R.id.btnCancel);
        etSearch = findViewById(R.id.etSearch);
        rvNews = findViewById(R.id.rvNews);
    }

    private void setEventListeners() {
        etSearch.setOnFocusChangeListener(this);
        etSearch.addTextChangedListener(twSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        && !TextFormatter.isNullOrEmpty(etSearch)) { // user clicks search icon on soft input keyboard
                    currentPage = PAGE_START;
                    currentQuery = TextFormatter.getTrimmedText(etSearch);
                    if (adapter != null) {
                        adapter.clear();
                    }
                    searchNews();
                    return true;
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(this);

        // add scroll listener while user reach in bottom load more will call
        rvNews.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                searchNews();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void searchNews() {
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.initiateRequest(currentQuery, currentPage, new NewsResultListener() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, response);
                NewsResponse newsResponse = new Gson().fromJson(response, NewsResponse.class);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentPage == PAGE_START) {
                                adapter = new NewsRecyclerAdapter(ActSearchNews.this, new ArrayList<>());
                                rvNews.setAdapter(adapter);
                                rvNews.setHasFixedSize(true);
                                layoutManager = new LinearLayoutManager(ActSearchNews.this);
                                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rvNews.setLayoutManager(layoutManager);
                            } else if (currentPage > PAGE_START) {
                                // there's no loading bar to remove at the beginning to be removed before setting
                                // first set of data to rvNews
                                adapter.removeLoading();
                            }
                            adapter.addItems(newsResponse.getArticles());
                            adapter.addLoading();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActSearchNews.this, getString(R.string.err_loading), Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
                isLoading = false;
                Toast.makeText(ActSearchNews.this, getString(R.string.err_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCancel && btnCancel.getVisibility() == View.VISIBLE) {
            currentQuery = null;
            etSearch.setText(null);
            btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
//        if (view.getId() == R.id.etSearch) {
//            if (hasFocus) {
//                // TODO if has previous queries, show latest 6
//            }
//        }
    }
}