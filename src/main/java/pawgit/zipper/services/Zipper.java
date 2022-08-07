package pawgit.zipper.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper implements AutoCloseable {
    private static final String FILE_NAME = "file.txt";
    private final ZipOutputStream zipOutputStream;
    private final String entryFileName;

    public Zipper(OutputStream outputStream) {
        this(outputStream, FILE_NAME);
    }

    protected Zipper(OutputStream outputStream, String zipFileName) {
        this.zipOutputStream = new ZipOutputStream(outputStream);
        this.entryFileName = zipFileName;
    }

    public Zipper createEntry() throws IOException {
        this.zipOutputStream.putNextEntry(new ZipEntry(entryFileName));
        return this;
    }

    public void writeAndFlush(byte[] bytes) throws IOException {
        zipOutputStream.write(bytes);
        zipOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        zipOutputStream.closeEntry();
        zipOutputStream.close();
    }
}
