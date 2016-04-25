package classifier;

import org.apache.commons.io.FileUtils;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Classifier {

    public static void train(String trainingDirectory, String modelFile) {

        int posCount = FileUtil.getTotalItemsOnFolder(trainingDirectory + "/pos");
        int negCount = FileUtil.getTotalItemsOnFolder(trainingDirectory + "/neg");
        int totCount = posCount + negCount;

        double posProb = (double) posCount / totCount;
        System.out.println("Positive Probability: " + posProb);
        double negProb = (double) negCount / totCount;
        System.out.println("Negative Probability: " + negProb);

        // Positive Reviews
        String pos = FileUtil.concatenateDocuments(trainingDirectory + "/pos");
        Map<String, Integer> posMap = getWordCount(pos);
        System.out.println("\nTotal Positive Words: " + getTotalWords(pos));

        // Negative Reviews
        String neg = FileUtil.concatenateDocuments(trainingDirectory + "/neg");
        Map<String, Integer> negMap = getWordCount(neg);
        System.out.println("\nTotal Negative Words: " + getTotalWords(neg));

        int vocabularySize = getVocabulary(pos, neg).size();
        System.out.println("\nVocabulary Size: " + vocabularySize);

        saveModel(modelFile, getModel(getVocabulary(pos, neg), posMap, pos), getModel(getVocabulary(pos, neg), negMap, neg));
    }

//    public static void test(String modelFile, String testDirectory, String predictionsFile) { }

    private static Map<String, Double> getModel(Set<String> vocabulary, Map<String, Integer> map, String doc) {
        Map<String, Double> model = new HashMap<>();

        for (String word : vocabulary) {
            model.put(word, getWordFrequency(word, map, getTotalWords(doc), vocabulary.size()));
            //System.out.println(word + " - " + model.get(word));
        }

        return model;
    }

    private static void saveModel(String modelFile, Map<String, Double> pos, Map<String, Double> neg) {
        StringBuilder model = new StringBuilder();

        try {
            // Pos Model
            for (Map.Entry<String, Double> entry : pos.entrySet())
                model.append("pos ").append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");

            // Neg Model
            for (Map.Entry<String, Double> entry : neg.entrySet())
                model.append("neg ").append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
            FileUtils.writeStringToFile(new File(modelFile), model.toString());

        } catch (IOException e) {
            System.out.println("Couldn't save model to file " + modelFile);
        }
    }

    private static double getWordFrequency(String word, Map<String, Integer> documentWordCount, int documentSize, int vocabularySize) {

        int wordCount = documentWordCount.containsKey(word) ? documentWordCount.get(word) : 0;
        return (double) (wordCount + 1) / (documentSize + vocabularySize);

        //return (double) (getWordCount(word, document) + 1) / (getTotalWords(document) + getVocabularySize(document));
    }

    private static int getTotalWords(String document) {
        return document.split("\\s").length;
    }

    private static Set<String> getVocabulary(String pos, String neg) {
        Set<String> vocabulary = new HashSet<>();

        Collections.addAll(vocabulary, pos.split("\\s"));
        Collections.addAll(vocabulary, neg.split("\\s"));

        return vocabulary;
    }

    private static Map<String, Integer> getWordCount(String document) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : document.split("\\s")) {
            if (wordCount.containsKey(word))
                wordCount.put(word, wordCount.get(word) + 1);
            else
                wordCount.put(word, 1);
        }

        return wordCount;
    }
}
