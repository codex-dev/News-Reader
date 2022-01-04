package demo.ravindu.newsreader.view;

import static demo.ravindu.newsreader.pagination.PaginationListener.PAGE_START;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import demo.ravindu.newsreader.R;
import demo.ravindu.newsreader.database.DatabaseManager;
import demo.ravindu.newsreader.model.NewsResponse;
import demo.ravindu.newsreader.model.PreviousQuery;
import demo.ravindu.newsreader.network.NetworkManager;
import demo.ravindu.newsreader.network.NewsResultListener;
import demo.ravindu.newsreader.pagination.NewsRecyclerAdapter;
import demo.ravindu.newsreader.pagination.PaginationListener;
import demo.ravindu.newsreader.util.TextFormatter;

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

        layoutManager = new LinearLayoutManager(ActSearchNews.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        initViews();
        setEventListeners();
    }

    /**
     * initialize views in the ui
     */
    private void initViews() {
        btnCancel = findViewById(R.id.btnCancel);
        etSearch = findViewById(R.id.etSearch);
        rvNews = findViewById(R.id.rvNews);
    }

    /**
     * define click listener action for views
     */
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

                    // hide keyboard
//                    hideSoftKeyboard(ActSearchNews.this);

                    // clear list set to adapter before performing a new search
                    if (adapter != null) {
                        adapter.clear();
                    }

                    saveQueryToDB();

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



    /**
     * hide soft keyboard
     * @param activity
     */
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * save query to db
     */
    private void saveQueryToDB() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        databaseManager.insertQuery(currentQuery);
    }

    /**
     * retrieve news result for the input query from external api
     */
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
                                rvNews.setLayoutManager(layoutManager);

                            } else if (currentPage > PAGE_START) {
                                // there's no loading bar to remove at the beginning to be removed before setting first set of data to rvNews
                                // remove loading at the end of the adapter before appending new items
                                adapter.removeLoading();
                            }

                            // append new news article items to adapter's list
                            adapter.addItems(newsResponse.getArticles());

                            // add loading again after appending new items
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

    /**
     * define click listener for cancel button
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCancel && btnCancel.getVisibility() == View.VISIBLE) {
            etSearch.clearFocus();
            currentQuery = null;
            etSearch.setText(null);
            btnCancel.setVisibility(View.GONE);

            // trigger focus change listener
            etSearch.requestFocus();
        }
    }

    /**
     * show latest 6 of past queries when search field gained focus
     *
     * @param view
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view.getId() == R.id.etSearch) {
            if (hasFocus && TextFormatter.isNullOrEmpty(etSearch)) {
                getAllPreviousQueries();
            }
        }
    }

    /**
     * get previously stored queried from db
     */
    private void getAllPreviousQueries() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        List<PreviousQuery> listPreviousQueries = databaseManager.getAllQueries();

        // extract latest 6 queries to be shown as search suggestions
        List<PreviousQuery> listQuerySuggestions = listPreviousQueries
                .subList(listPreviousQueries.size()-6, listPreviousQueries.size());

        // reverse list so that last item becomes the first item of the suggestion list
        Collections.reverse(listQuerySuggestions);
    }
}