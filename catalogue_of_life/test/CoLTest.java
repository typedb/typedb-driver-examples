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

package com.vaticle.typedb.example.catalogueOfLife.test;

import com.vaticle.typedb.client.TypeDB;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typedb.example.catalogueOfLife.Loader;
import com.vaticle.typeql.lang.TypeQL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static com.vaticle.typeql.lang.TypeQL.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CoLTest {
    private static final String DATABASE_NAME = "catalogue-of-life";

    private static TypeDBClient client;
    private static TypeDBSession session;

    @BeforeClass
    public static void loadTestData() throws IOException {
        Path dataDirectory = Path.of("catalogue_of_life/test/data");
        Loader.prepareData(dataDirectory);
        Loader.loadData(dataDirectory, Loader.parseCLIOptions(new String[]{}).get());

        client = TypeDB.coreClient("localhost:1729");
        session = client.session(DATABASE_NAME, TypeDBSession.Type.DATA);
    }

    @AfterClass
    public static void close() throws IOException {
        session.close();
        client.close();
    }

    @Test
    public void testLoaded() {
        TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.READ);

        long totalTaxa = transaction.query().match(TypeQL.match(var("x").isa("taxon")).count()).get().asLong();
        assertEquals(totalTaxa, 149);

        long totalReferences = transaction.query().match(TypeQL.match(var("x").isa("reference")).count()).get().asLong();
        assertEquals(totalReferences, 36);

        long totalRegions = transaction.query().match(TypeQL.match(var("x").isa("region")).count()).get().asLong();
        assertEquals(totalRegions, 85 + 14);

        long totalMarineRegions = transaction.query().match(TypeQL.match(var("x").isa("marine-region")).count()).get().asLong();
        assertEquals(totalMarineRegions, 85);

        long totalDescribedRegions = transaction.query().match(TypeQL.match(var("x").isa("catalogue-of-life-region")).count()).get().asLong();
        assertEquals(totalDescribedRegions, 14);

        long totalParenthoods = transaction.query().match(TypeQL.match(var("x").isa("parenthood")).count()).get().asLong();
        assertTrue(totalParenthoods > 0);

        long totalSources = transaction.query().match(TypeQL.match(var("x").isa("source")).count()).get().asLong();
        assertTrue(totalSources > 0);

        transaction.close();
    }
}
