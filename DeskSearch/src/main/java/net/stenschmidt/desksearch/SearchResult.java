package net.stenschmidt.desksearch;

public final class SearchResult {
    private String id;
    private String path;

    public SearchResult(final String id, final String path) {
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return id + " " + path;

    }

}