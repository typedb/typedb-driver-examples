package com.vaticle.typedb.example.xcom;

public class Result<T> {
    public Result() {
        this(null);
    }

    public Result(T entity) {
        this.valid = true;
        this.entity = entity;
        this.question = null;
    }

    private Result(boolean valid, T entity, Queries.Question<?, ?> question) {
        this.valid = valid;
        this.entity = entity;
        this.question = question;
    }

    public static Result<Object> nextQuestion(Queries.Question<?, ?> question) {
        return new Result<>(true, null, question);
    }

    public static Result<Object> invalid() {
        return new Result<>(false, null, null);
    }

    public T getEntity() {
        return entity;
    }

    public Queries.Question<?, ?> getQuestion() {
        return question;
    }

    public boolean isValid() {
        return valid;
    }

    private T entity;
    private boolean valid;
    private Queries.Question<?, ?> question;
}
