package com.hsdemo.auction;

/**
 * Created by User on 11/2/2015.
 */
public class AuctionItem {


    private String id;
    private String title;
    private String content;

    AuctionItem(String noteId, String noteTitle, String noteContent) {
        id = noteId;
        title = noteTitle;
        content = noteContent;

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

}