package com.example.yegor.nbrb.models;

public class ContentWrapper<T> {

    private T content;
    private Exception exception;

    public ContentWrapper() {
    }

    public ContentWrapper(T content) {
        this.content = content;
    }

    public ContentWrapper(Exception exception) {
        this.exception = exception;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
