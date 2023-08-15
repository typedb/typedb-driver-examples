/*
 * Copyright (C) 2023 Vaticle
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

package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class File extends StixCyberObservableObject {
    private Integer size;
    private String name;
    private String name_enc;
    private String magic_number_hex;
    private String mime_type;
    private Date ctime;
    private Date mtime;
    private Date atime;
    @JsonIgnore
    private final String typeString = super.getTypeString() + " name; name_enc; magic_number_hex; mime_type;";

    @Override
    public String getTypeString() {
        return typeString;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_enc() {
        return name_enc;
    }

    public void setName_enc(String name_enc) {
        this.name_enc = name_enc;
    }

    public String getMagic_number_hex() {
        return magic_number_hex;
    }

    public void setMagic_number_hex(String magic_number_hex) {
        this.magic_number_hex = magic_number_hex;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    public Date getAtime() {
        return atime;
    }

    public void setAtime(Date atime) {
        this.atime = atime;
    }
}
