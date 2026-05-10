package JavaMiniProject;

import java.io.*;

/**
 * Classe de test pour toutes les méthodes de Util (R7).
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("=== Test de la classe Util ===\n");

        // 1. Tester createTextField (R3)
        try {
            System.out.println("1. createTextField(3)");
            Util.createTextField(3);  // fichier output/chiffres.txt avec 6 lignes
            System.out.println("   -> OK. Vérifiez output/chiffres.txt");
        } catch (Exception e) {
            System.err.println("Erreur createTextField : " + e.getMessage());
        }

        // 2. Tester createExcelFile (R6) à partir du fichier généré
        try {
            System.out.println("\n2. createExcelFile(\"output/chiffres.txt\")");
            Util.createExcelFile("output/chiffres.txt");
            System.out.println("   -> OK. Vérifiez output/chiffres.xlsx");
        } catch (Exception e) {
            System.err.println("Erreur createExcelFile : " + e.getMessage());
        }

        // 3. Tester imageToFile (R4) : besoin d'une image 28x28. On en crée une via fileToImage d'abord.
        try {
            System.out.println("\n3. Préparation d'une image de test via fileToImage");
            // Créer un fichier texte artificiel valide
            try (PrintWriter w = new PrintWriter(new FileWriter("test_temp.txt"))) {
                for (int i = 0; i < 784; i++) {
                    w.print(i % 256);
                    if (i < 783) w.print(",");
                }
                w.println();
            }
            Util.fileToImage("test_temp.txt");  // génère test_temp.png
            System.out.println("   -> Image test_temp.png créée.");

            System.out.println("\n4. imageToFile(\"test_temp.png\")");
            Util.imageToFile("test_temp.png"); // doit créer test_temp.txt
            System.out.println("   -> OK. Vérifiez test_temp.txt (doit contenir 784 valeurs)");
        } catch (Exception e) {
            System.err.println("Erreur lors du test imageToFile/fileToImage : " + e.getMessage());
        }

        // 5. Nettoyage facultatif (commenté)
        // new File("test_temp.txt").delete();
        // new File("test_temp.png").delete();

        System.out.println("\n=== Fin des tests ===");
    }
}