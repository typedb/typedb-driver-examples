/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typedb.example.gaming.xcom;

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
