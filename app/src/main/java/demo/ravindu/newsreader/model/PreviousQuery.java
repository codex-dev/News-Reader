package demo.ravindu.newsreader.model;

public class PreviousQuery {
    private int id;
    private String queryDesc;

    public PreviousQuery() {
    }

    public PreviousQuery(String queryDesc) {
        this.queryDesc = queryDesc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQueryDesc() {
        return queryDesc;
    }

    public void setQueryDesc(String queryDesc) {
        this.queryDesc = queryDesc;
    }
}
