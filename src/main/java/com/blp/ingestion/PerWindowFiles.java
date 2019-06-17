package com.blp.ingestion;

import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.fs.ResolveOptions;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class PerWindowFiles extends FileBasedSink.FilenamePolicy {

    private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.hourMinute();
    private static final DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy/MM/dd/HH");

    private final ResourceId baseFilename;

    public PerWindowFiles(ResourceId baseFilename) {
        this.baseFilename = baseFilename;
    }

    public String filenamePrefixForWindow(IntervalWindow window) {

        String prefix = "output";
        return String.format("%s/%s-%s-%s",
                dtfOut.print(window.start()),
                prefix,
                FORMATTER.print(window.start()),
                FORMATTER.print(window.end()));
    }

    @Override
    public ResourceId windowedFilename(
            int shardNumber,
            int numShards,
            BoundedWindow window,
            PaneInfo paneInfo,
            FileBasedSink.OutputFileHints outputFileHints) {
        IntervalWindow intervalWindow = (IntervalWindow) window;
        String filename =
                String.format(
                        "%s-%s-of-%s%s",
                        filenamePrefixForWindow(intervalWindow),
                        shardNumber,
                        numShards,
                        outputFileHints.getSuggestedFilenameSuffix());
        return baseFilename
                .getCurrentDirectory()
                .resolve(filename, ResolveOptions.StandardResolveOptions.RESOLVE_FILE);
    }

    @Override
    public ResourceId unwindowedFilename(
            int shardNumber, int numShards, FileBasedSink.OutputFileHints outputFileHints) {
        throw new UnsupportedOperationException("Unsupported.");
    }
}
