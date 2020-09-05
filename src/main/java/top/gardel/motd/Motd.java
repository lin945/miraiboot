package top.gardel.motd;

public class Motd {
    private long id;
    private String text;
    private String from;
    private long createTime;

    public Motd() {
        this(-1, null, null, -1);
    }

    public Motd(long id, String text, String from, long createTime) {
        this.id = id;
        this.text = text;
        this.from = from;
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
