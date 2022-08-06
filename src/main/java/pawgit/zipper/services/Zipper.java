package pawgit.zipper.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper implements AutoCloseable {
    private static final String FILE_NAME = "file.txt";
    private final ZipOutputStream zipOut;
    private final String entryFileName;

    public Zipper(OutputStream zipOut) {
        this(zipOut, FILE_NAME);
    }

    protected Zipper(OutputStream zipOut, String zipFileName) {
        this.zipOut = new ZipOutputStream(zipOut);
        this.entryFileName = zipFileName;
    }

    public Zipper createEntry() throws IOException {
        this.zipOut.putNextEntry(new ZipEntry(entryFileName));
        return this;
    }

    public void writeAndFlush(byte[] bytes) throws IOException {
        zipOut.write(bytes);
        zipOut.flush();
    }

    @Override
    public void close() throws IOException {
        zipOut.closeEntry();
        zipOut.close();
    }
}
