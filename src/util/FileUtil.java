package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static int getTotalItemsOnFolder(String folderPath) {
        final int[] size = {0};

        try {
            Files.walk(Paths.get(folderPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && isTextFile(filePath))
                    size[0]++;
            });

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Couldn't find folder " + folderPath);
        }

        return size[0];
    }

    private static boolean isTextFile(Path filePath) {
        return FilenameUtils.getExtension(filePath.getFileName().toString()).equals("txt");
    }

    public static String concatenateDocuments(String folderPath) {
        StringBuilder documents = new StringBuilder();

        try {
            Files.walk(Paths.get(folderPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && isTextFile(filePath))
                    try {
                        documents.append(FileUtils.readFileToString(filePath.toFile())).append("\n");
                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.out.println("Couldn't read file " + filePath);
                    }
            });
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Couldn't find folder " + folderPath);
        }

        return documents.toString();
    }
}
