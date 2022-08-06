package pawgit.zipper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper implements AutoCloseable {
    private final ZipOutputStream zipOut;

    public Zipper(OutputStream zipOut) {
        this.zipOut = new ZipOutputStream(zipOut);
    }

    public Zipper createEntry() throws IOException {
        this.zipOut.putNextEntry(new ZipEntry("file.txt"));
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
