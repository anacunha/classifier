package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static String[] readFileLineByLine(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            for(String line; (line = br.readLine()) != null; ) {
                lines.add(line);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Couldn't read file " + filePath);
        }

        return lines.toArray(new String[lines.size()]);
    }

    public static Map<String, String> getDocumentsAsStrings(String folderPath) {
        Map<String, String> documents = new HashMap<>();

        try {
            Files.walk(Paths.get(folderPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && isTextFile(filePath))
                    try {
                        documents.put(FilenameUtils.removeExtension(filePath.getFileName().toString()), FileUtils.readFileToString(filePath.toFile()));
                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.out.println("Couldn't read file " + filePath);
                    }
            });

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Couldn't find folder " + folderPath);
        }

        return documents;
    }
}
