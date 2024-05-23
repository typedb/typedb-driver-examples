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

package com.vaticle.typedb.example.biology.catalogueOfLife.test;

import com.vaticle.typedb.driver.TypeDB;
import com.vaticle.typedb.driver.api.TypeDBDriver;
import com.vaticle.typedb.driver.api.TypeDBOptions;
import com.vaticle.typedb.driver.api.TypeDBSession;
import com.vaticle.typedb.driver.api.TypeDBTransaction;
import com.vaticle.typedb.example.biology.catalogueOfLife.Loader;
import com.vaticle.typeql.lang.TypeQL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static com.vaticle.typeql.lang.TypeQL.cVar;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CoLTest {
    private static final String DATABASE_NAME = "catalogue-of-life";

    private static TypeDBDriver driver;
    private static TypeDBSession session;

    @BeforeClass
    public static void loadTestData() throws IOException {
        Path dataDirectory = Path.of("biology/catalogue_of_life/test/data");
        Loader.prepareData(dataDirectory);
        Loader.loadData(dataDirectory, Loader.parseCLIOptions(new String[]{}).get());

        driver = TypeDB.coreDriver("localhost:1729");
        session = driver.session(DATABASE_NAME, TypeDBSession.Type.DATA);
    }

    @AfterClass
    public static void close() throws IOException {
        session.close();
        driver.close();
    }

    @Test
    public void testLoaded() {
        TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.READ);

        long totalTaxa = transaction.query().get(TypeQL.match(cVar("x").isa("taxon")).get().count()).resolve().get().asLong();
        assertEquals(149, totalTaxa);

        long totalNames = transaction.query().get(TypeQL.match(cVar("x").isa("vernacular-name")).get().count()).resolve().get().asLong();
        assertEquals(21, totalNames);

        long totalReferences = transaction.query().get(TypeQL.match(cVar("x").isa("reference")).get().count()).resolve().get().asLong();
        assertEquals(36, totalReferences);

        long totalRegions = transaction.query().get(TypeQL.match(cVar("x").isa("region")).get().count()).resolve().get().asLong();
        assertEquals(43 + 6, totalRegions);

        long totalMarineRegions = transaction.query().get(TypeQL.match(cVar("x").isa("marine-region")).get().count()).resolve().get().asLong();
        assertEquals(43, totalMarineRegions);

        long totalDescribedRegions = transaction.query().get(TypeQL.match(cVar("x").isa("catalogue-of-life-region")).get().count()).resolve().get().asLong();
        assertEquals(6, totalDescribedRegions);

        long totalParenthoods = transaction.query().get(TypeQL.match(cVar("x").isa("parenthood")).get().count()).resolve().get().asLong();
        assertEquals(147, totalParenthoods);

        long totalNamings = transaction.query().get(TypeQL.match(cVar("x").isa("naming")).get().count()).resolve().get().asLong();
        assertEquals(21, totalNamings);

        transaction.close();
    }

    @Test
    public void testQueries() {
        TypeDBTransaction transaction = session.transaction(TypeDBTransaction.Type.READ, new TypeDBOptions().infer(true));
        ArrayList<String> subspecies = new ArrayList<>();
        transaction.query().get(
                "match $a isa taxon; $a has scientific-name \"Gelliodes\"; "
                        + "(ancestor: $a, $x) isa common-taxon; "
                        + "$x has taxon-rank \"variety\", has scientific-name $sn; get $sn; sort $sn asc;"
        ).forEach(result -> subspecies.add(result.get("sn").asAttribute().getValue().asString()));
        String[] expectedSubspecies = {
                "Gelliodes carnosa var. laxa",
                "Gelliodes fayalensis var. minor",
                "Gelliodes petrosioides var. fibrosa",
                "Gellius varius var. fibrosus",
        };
        assertArrayEquals(expectedSubspecies, subspecies.toArray());

        ArrayList<String> commonAncestors = new ArrayList<>();
        transaction.query().get(
                "match $x isa taxon; ($x, $x-name) isa naming; $x-name has name \"Simien fox\"; "
                        + "$y isa taxon; ($y, $y-name) isa naming; $y-name has name \"Wolf\"; "
                        + "(ancestor: $a, $x, $y) isa common-taxon; $a has scientific-name $sn; get $sn;"
        ).forEach(result -> commonAncestors.add(result.get("sn").asAttribute().getValue().asString()));
        assertTrue(commonAncestors.size() >= 1);  // FIXME exactly one with negation in rule
        assertTrue(commonAncestors.contains("Canis"));
        transaction.close();
    }
}
