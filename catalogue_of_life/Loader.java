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

package com.vaticle.typedb.example.catalogueOfLife;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import com.vaticle.typedb.osi.loader.cli.LoadOptions;
import com.vaticle.typedb.osi.loader.loader.TypeDBLoader;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Loader {
    public static void main(String[] args) throws IOException {
        Optional<CLIOptions> parsedOptions = parseCLIOptions(args);
        if (parsedOptions.isEmpty()) return;

        CLIOptions options = parsedOptions.get();

        Path workingDirectory = Path.of(options.workingDirectory());
        Path zipFile = fetchZip(workingDirectory, options);

        Path dataDirectory = workingDirectory.resolve("coldp");
        if (Files.exists(zipFile) && (options.forceUnzip() || !Files.exists(dataDirectory))) unzip(zipFile, dataDirectory);

        prepareData(dataDirectory);
        loadData(dataDirectory, options);
    }

    public static Optional<CLIOptions> parseCLIOptions(String[] args) {
        CommandLine commandLine = new CommandLine(new CLIOptions());
        try {
            CommandLine.ParseResult parseResult = commandLine.parseArgs(args);
            if (commandLine.isUsageHelpRequested()) {
                commandLine.usage(commandLine.getOut());
                return Optional.empty();
            } else if (commandLine.isVersionHelpRequested()) {
                commandLine.printVersionHelp(commandLine.getOut());
                return Optional.empty();
            } else {
                return Optional.of(parseResult.asCommandLineList().get(0).getCommand());
            }
        } catch (CommandLine.ParameterException ex) {
            commandLine.getErr().println(ex.getMessage());
            if (!CommandLine.UnmatchedArgumentException.printSuggestions(ex, commandLine.getErr())) {
                ex.getCommandLine().usage(commandLine.getErr());
            }
            return Optional.empty();
        }
    }

    public static Path fetchZip(Path workingDirectory, CLIOptions options) throws MalformedURLException {
        URL url = new URL("https://api.checklistbank.org/dataset/9817/export.zip?format=ColDP");

        Path zipFile = workingDirectory.resolve("coldp.zip");
        if (!options.skipDownload() && (options.forceDownload() || !Files.exists(zipFile))) {
            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, zipFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return zipFile;
    }

    private static void unzip(Path zipFile, Path workingDirectory) {
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFile))) {
            for (ZipEntry zipEntry; (zipEntry = zipIn.getNextEntry()) != null; ) {
                Path resolvedPath = workingDirectory.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    Files.copy(zipIn, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void prepareData(Path dataDirectory) throws IOException {
        giveNamesUniqueID(dataDirectory);
        separateTaxonDistributions(dataDirectory);
    }

    private static void giveNamesUniqueID(Path dataDirectory) throws IOException {
        TsvParser parser = createTsvParser(dataDirectory.resolve("VernacularName.tsv"));
        TsvWriter writer = createTsvWriter(dataDirectory.resolve("VernacularNameWithID.tsv"));

        Record record;
        Map<String, String> recordValues = new HashMap<>();
        long i = 0;
        while ((record = parser.parseNextRecord()) != null) {
            record.fillFieldMap(recordValues);
            recordValues.put("col:nameID", String.valueOf(i++));
            writer.writeRow(recordValues);
        }

        writer.close();
    }

    private static void separateTaxonDistributions(Path dataDirectory) throws IOException {
        TsvParser distributionsParser = createTsvParser(dataDirectory.resolve("Distribution.tsv"));
        TsvWriter marineRegionsWriter = createTsvWriter(dataDirectory.resolve("MarineDistribution.tsv"));
        TsvWriter describedRegionsWriter = createTsvWriter(dataDirectory.resolve("DescribedDistribution.tsv"));

        Record record;
        Map<String, String> recordValues = new HashMap<>();
        Map<String, ArrayList<String>> seen = new HashMap<>();
        while ((record = distributionsParser.parseNextRecord()) != null) {
            String taxonID = record.getString("col:taxonID");
            String areaID = record.getString("col:areaID");
            if (!seen.containsKey(taxonID) || !seen.get(taxonID).contains(areaID)) {
                String gazetteer = record.getString("col:gazetteer");
                if (gazetteer.equals("text") || gazetteer.equals("iso")) {
                    record.fillFieldMap(recordValues);
                    describedRegionsWriter.writeRow(recordValues);
                } else if (gazetteer.equals("mrgid") && isValidMarineRegionID(record.getString("col:areaID"))) {
                    seen.computeIfAbsent(taxonID, key -> new ArrayList<>()).add(areaID);
                    record.fillFieldMap(recordValues);
                    marineRegionsWriter.writeRow(recordValues);
                }
            }
        }

        marineRegionsWriter.close();
        describedRegionsWriter.close();
    }

    private static TsvParser createTsvParser(Path tsvFile) throws IOException {
        TsvParserSettings settings = new TsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setMaxCharsPerColumn(1_000_000);

        TsvParser parser = new TsvParser(settings);
        InputStream inputStream = Files.newInputStream(tsvFile);
        parser.beginParsing(inputStream);

        return parser;
    }

    private static TsvWriter createTsvWriter(Path tsvFile) throws IOException {
        TsvWriterSettings writerSettings = new TsvWriterSettings();
        writerSettings.setHeaderWritingEnabled(true);
        writerSettings.setMaxCharsPerColumn(1_000_000);

        return new TsvWriter(Files.newOutputStream(tsvFile), writerSettings);
    }

    private static boolean isValidMarineRegionID(String areaID) {
        return isNumeric(areaID);
    }

    private static boolean isNumeric(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static void loadData(Path dataDirectory, CLIOptions options) throws IOException {
        extractSchema(dataDirectory);

        LoadOptions loadOptions = new LoadOptions();
        loadOptions.dataConfigFilePath = populateLoaderConfigTemplate(dataDirectory).toString();
        loadOptions.databaseName = "catalogue-of-life";
        loadOptions.typedbURI = options.typedbURI();
        loadOptions.cleanMigration = true;
        loadOptions.loadSchema = false;

        TypeDBLoader loader = new TypeDBLoader(loadOptions);
        loader.load();
    }

    public static void extractSchema(Path dataDirectory) throws IOException {
        InputStream schema = Loader.class.getResourceAsStream("/catalogue_of_life/schema.tql");
        if (schema == null) throw new FileNotFoundException("schema.tql not found within the jar!");
        Files.copy(schema, dataDirectory.resolve("schema.tql"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static Path populateLoaderConfigTemplate(Path dataDirectory) throws IOException {
        Path config = dataDirectory.resolve("loader-config.json");

        try (FileWriter configWriter = new FileWriter(config.toFile())) {
            InputStream template = Loader.class.getResourceAsStream("/catalogue_of_life/loader-config.json.template");
            if (template == null) throw new FileNotFoundException("loader-config.json.template not found within the jar!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(template));
            String replacement = dataDirectory.toString();
            while (reader.ready()) {
                String line = reader.readLine().replace("${data-directory}", replacement);
                configWriter.write(line + "\n");
            }
        }

        return config;
    }

    @CommandLine.Command(name = "loader", mixinStandardHelpOptions = true)
    public static class CLIOptions {
        @CommandLine.Option(
                names = {"--working-directory", "--dir"},
                defaultValue = ".",
                description = "directory in which to download and extract data to (default: current)"
        )
        private String workingDirectory;

        @CommandLine.Option(
                names = {"--skip-download"},
                description = "do not download the dataset .zip if missing"
        )
        private boolean skipDownload;

        @CommandLine.Option(
                names = {"--force-download"},
                description = "redownload the dataset .zip even if present (overrides --skip-download, implies --force-unzip)"
        )
        private boolean forceDownload;

        @CommandLine.Option(
                names = {"--force-unzip"},
                description = "unpack the dataset .zip even if the data directory already present"
        )
        private boolean forceUnzip;

        @CommandLine.Option(
                names = {"--typedb"},
                description = {"optional - TypeDB server in format: server:port (default: localhost:1729)"},
                defaultValue = "localhost:1729"
        )
        private String typedbURI;

        public String workingDirectory() {
            return workingDirectory;
        }

        public boolean skipDownload() {
            return !forceDownload() && skipDownload;
        }

        public boolean forceDownload() {
            return forceDownload;
        }

        public boolean forceUnzip() {
            return forceDownload() || forceUnzip;
        }

        public String typedbURI() {
            return typedbURI;
        }
    }
}
