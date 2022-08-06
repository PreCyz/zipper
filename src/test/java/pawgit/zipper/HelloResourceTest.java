package pawgit.zipper;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class HelloResourceTest {

    @Test
    void name() throws IOException {
        Scanner myObj = new Scanner(Files.newInputStream(Paths.get("C:\\Users\\Gawa\\Downloads\\companies", "file.txt")));  // Create a Scanner object
        while (myObj.hasNextLine()) {
            String line = myObj.nextLine();
            System.out.println(line);
            byte[] decode = Base64.getDecoder().decode(line.getBytes(StandardCharsets.UTF_8));
            System.out.println(new String(decode));
        }
    }
}