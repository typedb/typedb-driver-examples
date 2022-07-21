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
import com.vaticle.typedb.client.api.TypeDBOptions;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typedb.example.catalogueOfLife.Loader;
import com.vaticle.typeql.lang.TypeQL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static com.vaticle.typeql.lang.TypeQL.var;
import static org.junit.Assert.assertArrayEquals;
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
        assertEquals(149, totalTaxa);

        long totalNames = transaction.query().match(TypeQL.match(var("x").isa("vernacular-name")).count()).get().asLong();
        assertEquals(21, totalNames);

        long totalReferences = transaction.query().match(TypeQL.match(var("x").isa("reference")).count()).get().asLong();
        assertEquals(36, totalReferences);

        long totalRegions = transaction.query().match(TypeQL.match(var("x").isa("region")).count()).get().asLong();
        assertEquals(85 + 14, totalRegions);

        long totalMarineRegions = transaction.query().match(TypeQL.match(var("x").isa("marine-region")).count()).get().asLong();
        assertEquals(85, totalMarineRegions);

        long totalDescribedRegions = transaction.query().match(TypeQL.match(var("x").isa("catalogue-of-life-region")).count()).get().asLong();
        assertEquals(14, totalDescribedRegions);

        long totalParenthoods = transaction.query().match(TypeQL.match(var("x").isa("parenthood")).count()).get().asLong();
        assertEquals(147, totalParenthoods);

        long totalSources = transaction.query().match(TypeQL.match(var("x").isa("source")).count()).get().asLong();
        assertEquals(149, totalSources);

        long totalNamings = transaction.query().match(TypeQL.match(var("x").isa("naming")).count()).get().asLong();
        assertEquals(21, totalNamings);

        transaction.close();
    }

    @Test
    public void testQueries() {
        TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.READ, TypeDBOptions.core().infer(true));
        ArrayList<String> subspecies = new ArrayList<>();
        transaction.query().match(
                "match $a isa taxon; $a has scientific-name \"Gelliodes\"; "
                        + "(ancestor: $a, $x) isa common-taxon; "
                        + "$x has taxon-rank \"variety\", has scientific-name $sn; get $sn; sort $sn asc;"
        ).forEach(result -> subspecies.add(result.get("sn").asAttribute().asString().getValue()));
        String[] expectedSubspecies = {
                "Gelliodes carnosa var. laxa",
                "Gelliodes fayalensis var. minor",
                "Gelliodes petrosioides var. fibrosa",
                "Gellius varius var. fibrosus",
        };
        assertArrayEquals(expectedSubspecies, subspecies.toArray());

        ArrayList<String> commonAncestors = new ArrayList<>();
        transaction.query().match(
                "match $x isa taxon; ($x, $x-name) isa naming; $x-name has name \"Simien fox\"; "
                        + "$y isa taxon; ($y, $y-name) isa naming; $y-name has name \"Wolf\"; "
                        + "(ancestor: $a, $x, $y) isa common-taxon; $a has scientific-name $sn; get $sn;"
        ).forEach(result -> commonAncestors.add(result.get("sn").asAttribute().asString().getValue()));
        assertTrue(commonAncestors.size() >= 1);  // FIXME exactly one with negation in rule
        assertTrue(commonAncestors.contains("Canis"));
        transaction.close();
    }
}
