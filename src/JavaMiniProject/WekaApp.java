package JavaMiniProject;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.*;
import java.util.*;

/**
 * Programme principal pour le Machine Learning (Partie 2).
 * 1. Génère 800 échantillons (400 trois, 400 cinq).
 * 2. Sépare en train (600) et test (200).
 * 3. Crée train-data.arff et test-data.arff.
 * 4. Entraîne Naive Bayes et Random Forest sur train-data.arff.
 * 5. Évalue sur test-data.arff.
 */
public class WekaApp {

    private static final int TOTAL_SAMPLES = 800;      // 400 trois + 400 cinq
    private static final int TRAIN_SIZE = 600;         // 300 de chaque (par exemple)
    private static final int TEST_SIZE = TOTAL_SAMPLES - TRAIN_SIZE; // 200

    public static void main(String[] args) {
        try {
            // Étape 1 : créer le fichier texte avec 400 trois + 400 cinq (R10)
            System.out.println("=== Étape 1 : Génération de 800 échantillons ===");
            Util.createTextField(400);  // produit output/chiffres.txt

            // Étape 2 : diviser le fichier en train.txt et test.txt
            System.out.println("\n=== Étape 2 : Séparation train/test ===");
            splitFile("output/chiffres.txt", "output/train.txt", "output/test.txt", TRAIN_SIZE);

            // Étape 3 : créer les fichiers ARFF (R11)
            System.out.println("\n=== Étape 3 : Création des fichiers ARFF ===");
            createArffFromText("output/train.txt", "output/train-data.arff");
            createArffFromText("output/test.txt", "output/test-data.arff");

            // Étape 4 : charger les ensembles ARFF
            Instances trainData = loadArff("output/train-data.arff");
            Instances testData = loadArff("output/test-data.arff");

            // Vérifier que la classe est bien définie (dernière variable nominale)
            if (trainData.classIndex() == -1) {
                trainData.setClassIndex(trainData.numAttributes() - 1);
            }
            if (testData.classIndex() == -1) {
                testData.setClassIndex(testData.numAttributes() - 1);
            }

            // Étape 5 : tester Naive Bayes (R12)
            System.out.println("\n=== Modèle 1 : Naive Bayes ===");
            Classifier nb = new NaiveBayes();
            nb.buildClassifier(trainData);
            Evaluation evalNB = new Evaluation(trainData);
            evalNB.evaluateModel(nb, testData);
            System.out.println(evalNB.toSummaryString("\nRésultats Naive Bayes :\n", true));
            System.out.println("Matrice de confusion :\n" + evalNB.toMatrixString());

            // Étape 6 : tester Random Forest
            System.out.println("\n=== Modèle 2 : Random Forest ===");
            RandomForest rf = new RandomForest();
            rf.setNumIterations(100); // 100 arbres
            rf.buildClassifier(trainData);
            Evaluation evalRF = new Evaluation(trainData);
            evalRF.evaluateModel(rf, testData);
            System.out.println(evalRF.toSummaryString("\nRésultats Random Forest :\n", true));
            System.out.println("Matrice de confusion :\n" + evalRF.toMatrixString());

        } catch (Exception e) {
            System.err.println("Erreur dans WekaApp : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sépare un fichier texte (chiffres.txt) en deux fichiers train et test.
     * Les premières 'trainSize' lignes vont dans train.txt, le reste dans test.txt.
     */
    private static void splitFile(String inputPath, String trainPath, String testPath, int trainSize) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        if (lines.size() < trainSize) {
            throw new IOException("Pas assez de lignes pour la séparation train/test");
        }
        try (PrintWriter trainW = new PrintWriter(new FileWriter(trainPath));
             PrintWriter testW = new PrintWriter(new FileWriter(testPath))) {
            for (int i = 0; i < lines.size(); i++) {
                if (i < trainSize) trainW.println(lines.get(i));
                else testW.println(lines.get(i));
            }
        }
        System.out.println("Fichiers créés : " + trainPath + " (" + trainSize + " lignes), " + testPath + " (" + (lines.size() - trainSize) + " lignes)");
    }

    /**
     * Convertit un fichier texte (format : 784 ints + label "trois"/"cinq") en fichier ARFF.
     */
    private static void createArffFromText(String txtPath, String arffPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(txtPath));
             PrintWriter arffWriter = new PrintWriter(new FileWriter(arffPath))) {

            // Écrire l'en-tête ARFF
            arffWriter.println("@relation digits");
            for (int i = 0; i < 784; i++) {
                arffWriter.println("@attribute pixel" + i + " numeric");
            }
            arffWriter.println("@attribute class {trois, cinq}");
            arffWriter.println("@data");

            // Écrire les données
            String line;
            while ((line = reader.readLine()) != null) {
                // Remplacer le label "trois"/"cinq" (déjà en français) et s'assurer qu'il n'y a pas d'espaces
                String cleaned = line.trim();
                arffWriter.println(cleaned);
            }
        }
        System.out.println("ARFF créé : " + arffPath);
    }

    /**
     * Charge un fichier ARFF dans un objet Instances Weka.
     */
    private static Instances loadArff(String path) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        Instances data = new Instances(reader);
        reader.close();
        // S'assurer que la classe est nominale et correctement indexée
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }
}