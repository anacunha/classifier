package classifier;

import org.apache.commons.io.FileUtils;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static util.FileUtil.getDocumentsAsStrings;
import static util.FileUtil.readFileLineByLine;

public class Classifier {

    public static void train(String trainingDirectory, String modelFile) {

        int posCount = FileUtil.getTotalItemsOnFolder(trainingDirectory + "/pos");
        int negCount = FileUtil.getTotalItemsOnFolder(trainingDirectory + "/neg");
        int totCount = posCount + negCount;

        double posPrior = (double) posCount / totCount;
        double negPrior = (double) negCount / totCount;

        // Positive Reviews
        String pos = FileUtil.concatenateDocuments(trainingDirectory + "/pos");
        Map<String, Integer> posMap = getWordCount(pos);

        // Negative Reviews
        String neg = FileUtil.concatenateDocuments(trainingDirectory + "/neg");
        Map<String, Integer> negMap = getWordCount(neg);

        saveModel(modelFile, posPrior, getModel(getVocabulary(pos, neg), posMap, pos), negPrior, getModel(getVocabulary(pos, neg), negMap, neg));
    }

    public static void test(String modelFile, String testDirectory, String predictionsFile) {
        Map<String, Double> posModel = readModelFromFile(modelFile, "pos");
        Map<String, Double> negModel = readModelFromFile(modelFile, "neg");
        double posPrior = readPriorFromFile(modelFile, "pos");
        double negPrior = readPriorFromFile(modelFile, "neg");

        // Read all documents in test data
        Map<String, String> testDocuments = getDocumentsAsStrings(testDirectory);

        // Predictions for every document
        Map<String, Double> posPredictions = new HashMap<>();
        Map<String, Double> negPredictions = new HashMap<>();

        for (Map.Entry<String, String> document : testDocuments.entrySet()) {
            double posPrediction = Math.log(posPrior);
            double negPrediction = Math.log(negPrior);

            for (String word : document.getValue().split("\\s+")) {

                if (posModel.containsKey(word))
                    posPrediction = posPrediction + posModel.get(word);
                else
                    posPrediction = posPrediction + posModel.get("null");

                if (negModel.containsKey(word))
                    negPrediction = negPrediction + negModel.get(word);
                else
                    negPrediction = negPrediction + negModel.get("null");
            }

            posPredictions.put(document.getKey(), posPrediction);
            negPredictions.put(document.getKey(), negPrediction);
        }
        savePredictions(predictionsFile, posPredictions, negPredictions);
    }

    private static Map<String, Double> getModel(Set<String> vocabulary, Map<String, Integer> map, String doc) {
        Map<String, Double> model = new HashMap<>();
        int totalWords = getTotalWords(doc);
        int vocabularySize = vocabulary.size();

        model.put(null, getWordFrequency(null, map, totalWords, vocabularySize));

        for (String word : vocabulary) {
            double wordFrequency = getWordFrequency(word, map, totalWords, vocabularySize);
            model.put(word, wordFrequency);
        }

        return model;
    }

    private static void saveModel(String modelFile, double posPrior, Map<String, Double> pos, double negPrior, Map<String, Double> neg) {
        StringBuilder model = new StringBuilder();

        try {
            // Pos Prior
            model.append("pos ").append(posPrior).append("\n");

            // Neg Prior
            model.append("neg ").append(negPrior).append("\n");

            // Pos Model
            for (Map.Entry<String, Double> entry : pos.entrySet())
                model.append("pos ").append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");

            // Neg Model
            for (Map.Entry<String, Double> entry : neg.entrySet())
                model.append("neg ").append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");

            FileUtils.writeStringToFile(new File(modelFile), model.toString());
            System.out.println("Saved model file to " + modelFile);

        } catch (IOException e) {
            System.out.println("Couldn't save model to file " + modelFile);
        }
    }

    private static void savePredictions(String predictionsFile, Map<String, Double> posPredictions, Map<String, Double> negPredictions) {
        StringBuilder predictions = new StringBuilder();

        try {
            predictions.append("document,pos score,neg score,classification\n");

            for (String document : posPredictions.keySet()) {
                double posPrediction = posPredictions.get(document);
                double negPrediction = negPredictions.get(document);
                predictions.append(document).append(",").append(posPrediction).append(",").append(negPrediction);

                if (posPrediction >= negPrediction)
                    predictions.append(",").append("pos").append("\n");
                else
                    predictions.append(",").append("neg").append("\n");

            }

            FileUtils.writeStringToFile(new File(predictionsFile + ".csv"), predictions.toString());
            System.out.println("Saved predictions file to " + predictionsFile);

        } catch (IOException e) {
            System.out.println("Couldn't save predictions to file " + predictionsFile);
        }
    }

    private static Map<String, Double> readModelFromFile(String modelFile, String className) {
        Map<String, Double> model = new HashMap<>();
        String[] modelStr = readFileLineByLine(modelFile);

        for (String probability : modelStr) {
            String[] line = probability.split("\\s");

            if (line.length == 3 && line[0].equals(className))
                model.put(line[1], Double.parseDouble(line[2]));
        }
        return model;
    }

    private static double readPriorFromFile(String modelFile, String className) {
        String[] modelStr = readFileLineByLine(modelFile);

        for (String probability : modelStr) {
            String[] line = probability.split("\\s");

            if (line.length == 2 && line[0].equals(className)) {
                return Double.parseDouble(line[1]);
            }
        }
        return 0;
    }

    private static double getWordFrequency(String word, Map<String, Integer> documentWordCount, int documentSize, int vocabularySize) {

        int wordCount = documentWordCount.containsKey(word) ? documentWordCount.get(word) : 0;
        return Math.log((double) (wordCount + 1) / (documentSize + vocabularySize));
    }

    private static int getTotalWords(String document) {
        return document.split("\\s+").length;
    }

    private static Set<String> getVocabulary(String pos, String neg) {
        Set<String> vocabulary = new HashSet<>();

        Collections.addAll(vocabulary, pos.split("\\s+"));
        Collections.addAll(vocabulary, neg.split("\\s+"));

        return vocabulary;
    }

    private static Map<String, Integer> getWordCount(String document) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : document.split("\\s+")) {
            if (wordCount.containsKey(word))
                wordCount.put(word, wordCount.get(word) + 1);
            else
                wordCount.put(word, 1);
        }

        return wordCount;
    }
}
