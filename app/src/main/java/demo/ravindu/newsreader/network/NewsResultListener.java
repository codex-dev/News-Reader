package demo.ravindu.newsreader.network;

public interface NewsResultListener {
    void onSuccess(String response);

    void onError(String error);
}
