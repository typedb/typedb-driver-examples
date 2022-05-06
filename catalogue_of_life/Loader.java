package com.vaticle.typedb.example.catalogueOfLife;

import cli.LoadOptions;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import loader.TypeDBLoader;
import picocli.CommandLine;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Loader {
    public static void main(String[] args) throws IOException {
        Optional<Options> maybeOptions = parseCLIOptions(args);
        if (maybeOptions.isEmpty()) return;

        Options options = maybeOptions.get();

        Path workingDirectory = Path.of(options.workingDirectory());
        Path zipFile = fetchZip(workingDirectory, options);

        Path dataDirectory = workingDirectory.resolve("coldp");
        if (Files.exists(zipFile) && (options.forceUnzip() || !Files.exists(dataDirectory))) unzip(zipFile, dataDirectory);

        prepareData(dataDirectory);
        loadData(dataDirectory, options);
    }

    public static Optional<Options> parseCLIOptions(String[] args) {
        CommandLine commandLine = new CommandLine(new Options());
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

    public static Path fetchZip(Path workingDirectory, Options options) throws MalformedURLException {
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
        TsvParser distributionsParser = beginParsingFile(dataDirectory.resolve("Distribution.tsv"));
        TsvWriter marineRegionsWriter = createTsvWriter(dataDirectory.resolve("MarineDistribution.tsv"));
        TsvWriter describedRegionsWriter = createTsvWriter(dataDirectory.resolve("DescribedDistribution.tsv"));

        Record record;
        Map<String, String> recordValues = new HashMap<>();
        while ((record = distributionsParser.parseNextRecord()) != null) {
            String gazetteer = record.getString("col:gazetteer");
            if (gazetteer.equals("text") || gazetteer.equals("iso")) {
                record.fillFieldMap(recordValues);
                describedRegionsWriter.writeRow(recordValues);
            } else if (gazetteer.equals("mrgid") && isValidMarineRegionID(record.getString("col:areaID"))) {
                record.fillFieldMap(recordValues);
                marineRegionsWriter.writeRow(recordValues);
            }
        }

        marineRegionsWriter.close();
        describedRegionsWriter.close();
    }

    private static TsvParser beginParsingFile(Path tsvFile) throws IOException {
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

    public static void loadData(Path dataDirectory, Options options) throws IOException {
        extractSchema(dataDirectory);


        LoadOptions loadOptions = new LoadOptions();
        loadOptions.dataConfigFilePath = createTypeDBLoaderConfig(dataDirectory).toString();
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

    public static Path createTypeDBLoaderConfig(Path dataDirectory) throws IOException {
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
    public static class Options {
        @CommandLine.Option(
                names = {"--working-directory", "--dir"},
                defaultValue = ".",
                description = "directory in which to store data (default: current)"
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
