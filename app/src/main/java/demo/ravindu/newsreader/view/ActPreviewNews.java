package demo.ravindu.newsreader.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.ParseException;

import demo.ravindu.newsreader.R;
import demo.ravindu.newsreader.util.DateFormatter;
import demo.ravindu.newsreader.util.TextFormatter;

public class ActPreviewNews extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivImage, ivBack;
    private TextView tvSource, tvDate, tvTitle, tvDescription, tvContent, btnReadMore;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_preview_news);

        initViews();
        setEventListeners();
        setValues();
    }

    /**
     * initialize views
     */
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivImage = findViewById(R.id.ivImage);
        tvSource = findViewById(R.id.tvSource);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvContent = findViewById(R.id.tvContent);
        btnReadMore = findViewById(R.id.btnReadMore);
    }

    /**
     * set click listener for button
     */
    private void setEventListeners() {
        ivBack.setOnClickListener(this);
        btnReadMore.setOnClickListener(this);
    }

    /**
     * Set values to fields retrieved from previous UI
     */
    private void setValues() {
        Bundle b = getIntent().getExtras();
        url = b.getString("url");

        if (!TextFormatter.isNullOrEmpty(b.getString("image"))) {
            Glide.with(this).load(b.getString("image")).error(R.drawable.img_placeholder).into(ivImage);
        }

        tvSource.setText(b.getString("source"));

        try {
            tvDate.setText(DateFormatter.convertTimeFromUtcToLocal("dd MMM yyyy", b.getString("date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvTitle.setText(b.getString("title"));
        tvDescription.setText(b.getString("desc"));
        tvContent.setText(b.getString("content"));
    }

    /**
     * define click listener action for each view
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBack) {
            onBackPressed();
        } else if (view.getId() == R.id.btnReadMore) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }
}