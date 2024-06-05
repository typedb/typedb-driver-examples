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

package com.typedb.examples.cti.model.observable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.typedb.examples.cti.model.stix.StixDomainObject;
import com.typedb.examples.cti.model.stix.StixCyberObservableObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class File extends StixCyberObservableObject {
    @JsonIgnore
    public static final List<String> typeString = Stream.of(StixDomainObject.typeString,
            Arrays.asList("name", "name_enc", "magic_number_hex", "mime_type")).flatMap(List::stream).toList();

    @JsonIgnore
    public static final String nameEnt = "file";

    private Integer size;
    private String name;
    private String nameEnc;
    private String magicNumberHex;
    private String mimeType;
    private Date ctime;
    private Date mtime;
    private Date atime;

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

    public String getNameEnc() {
        return nameEnc;
    }

    public void setNameEnc(String nameEnc) {
        this.nameEnc = nameEnc;
    }

    public String getMagicNumberHex() {
        return magicNumberHex;
    }

    public void setMagicNumberHex(String magicNumberHex) {
        this.magicNumberHex = magicNumberHex;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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
