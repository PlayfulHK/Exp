package com.example.exp2;

class Diary_list_item {
    String title,createTime,content;

    public Diary_list_item(String title, String createTime, String content) {
        this.title = title;
        this.createTime = createTime;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
