package JavaMiniProject;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.*;

public class Util {

    private static final int IMAGE_SIZE = 28;
    private static final int PIXEL_COUNT = IMAGE_SIZE * IMAGE_SIZE;

    // ---------- Lecture correcte des fichiers MNIST (sans reverseBytes) ----------
    private static List<int[]> readMnistSamples(int n) throws IOException {
        String imagePath = "data/train-images.idx3-ubyte";
        String labelPath = "data/train-labels.idx1-ubyte";

        // Vérifier que les fichiers existent
        File imgFile = new File(imagePath);
        File lblFile = new File(labelPath);
        if (!imgFile.exists()) throw new FileNotFoundException("Fichier images manquant : " + imagePath);
        if (!lblFile.exists()) throw new FileNotFoundException("Fichier labels manquant : " + labelPath);

        try (DataInputStream imageStream = new DataInputStream(new BufferedInputStream(new FileInputStream(imgFile)));
             DataInputStream labelStream = new DataInputStream(new BufferedInputStream(new FileInputStream(lblFile)))) {

            // Lire les magic numbers directement (big-endian)
            int magicImages = imageStream.readInt();
            int magicLabels = labelStream.readInt();

            if (magicImages != 2051) throw new IOException("Magic number images incorrect : " + magicImages + " (attendu 2051)");
            if (magicLabels != 2049) throw new IOException("Magic number labels incorrect : " + magicLabels + " (attendu 2049)");

            int numImages = imageStream.readInt();
            int rows = imageStream.readInt();
            int cols = imageStream.readInt();
            int numLabels = labelStream.readInt();

            if (rows != IMAGE_SIZE || cols != IMAGE_SIZE) {
                throw new IOException("Taille d'image incorrecte : " + rows + "x" + cols);
            }
            if (numImages != numLabels) {
                throw new IOException("Nombre d'images et de labels différent");
            }

            List<int[]> samples = new ArrayList<>();
            int threeCount = 0, fiveCount = 0;
            int index = 0;

            while ((threeCount < n || fiveCount < n) && index < numImages) {
                int label = labelStream.readUnsignedByte();
                if ((label == 3 && threeCount < n) || (label == 5 && fiveCount < n)) {
                    int[] sample = new int[PIXEL_COUNT + 1];
                    for (int i = 0; i < PIXEL_COUNT; i++) {
                        sample[i] = imageStream.readUnsignedByte();
                    }
                    sample[PIXEL_COUNT] = label;
                    samples.add(sample);
                    if (label == 3) threeCount++;
                    else fiveCount++;
                } else {
                    // Ignorer cette image (sauter les pixels)
                    imageStream.skipBytes(PIXEL_COUNT);
                }
                index++;
            }
            return samples;
        }
    }

    // ---------- R3 : createTextField ----------
    public static void createTextField(int n) throws IOException {
        List<int[]> samples = readMnistSamples(n);
        if (samples.size() != 2 * n) {
            throw new IOException("Nombre d'échantillons insuffisant : " + samples.size());
        }

        // Créer le dossier output s'il n'existe pas
        File outDir = new File("output");
        if (!outDir.exists()) outDir.mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter("output/chiffres.txt"))) {
            for (int[] sample : samples) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < PIXEL_COUNT; i++) {
                    sb.append(sample[i]);
                    if (i < PIXEL_COUNT - 1) sb.append(",");
                }
                sb.append(",");
                sb.append(sample[PIXEL_COUNT] == 3 ? "trois" : "cinq");
                writer.println(sb.toString());
            }
        }
        System.out.println("Fichier output/chiffres.txt créé avec " + (2 * n) + " lignes.");
    }

    // ---------- R4 : imageToFile ----------
    public static void imageToFile(String nomImage) throws IOException {
        File imgFile = new File(nomImage);
        if (!imgFile.exists()) throw new FileNotFoundException("Image introuvable : " + nomImage);

        BufferedImage img = ImageIO.read(imgFile);
        if (img == null) throw new IOException("Format d'image non supporté ou fichier corrompu");
        if (img.getWidth() != IMAGE_SIZE || img.getHeight() != IMAGE_SIZE) {
            throw new IllegalArgumentException("L'image doit être " + IMAGE_SIZE + "x" + IMAGE_SIZE + " pixels");
        }

        int[] intensities = new int[PIXEL_COUNT];
        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (int)(0.299 * ((rgb >> 16) & 0xFF) + 0.587 * ((rgb >> 8) & 0xFF) + 0.114 * (rgb & 0xFF));
                intensities[y * IMAGE_SIZE + x] = gray;
            }
        }

        String txtName = nomImage.replaceFirst("[.][pP][nN][gG]$", "") + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(txtName))) {
            for (int i = 0; i < PIXEL_COUNT; i++) {
                writer.print(intensities[i]);
                if (i < PIXEL_COUNT - 1) writer.print(",");
            }
            writer.println();
        }
        System.out.println("Fichier texte créé : " + txtName);
    }

    // ---------- R5 : fileToImage ----------
    public static void fileToImage(String nomFichier) throws IOException {
        File txtFile = new File(nomFichier);
        if (!txtFile.exists()) throw new FileNotFoundException("Fichier texte introuvable : " + nomFichier);

        String[] parts;
        try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
            String line = reader.readLine();
            if (line == null) throw new IOException("Fichier vide");
            parts = line.split(",");
        }
        if (parts.length != PIXEL_COUNT) {
            throw new IllegalArgumentException("Nombre de valeurs incorrect : " + parts.length + " (attendu " + PIXEL_COUNT + ")");
        }

        int[] intensities = new int[PIXEL_COUNT];
        for (int i = 0; i < PIXEL_COUNT; i++) {
            intensities[i] = Integer.parseInt(parts[i].trim());
            if (intensities[i] < 0 || intensities[i] > 255) {
                throw new IllegalArgumentException("Intensité hors limites : " + intensities[i]);
            }
        }

        BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int gray = intensities[y * IMAGE_SIZE + x];
                int rgb = (gray << 16) | (gray << 8) | gray;
                img.setRGB(x, y, rgb);
            }
        }

        String pngName = nomFichier.replaceFirst("[.][tT][xX][tT]$", "") + ".png";
        ImageIO.write(img, "png", new File(pngName));
        System.out.println("Image PNG créée : " + pngName);
    }

    // ---------- R6 : createExcelFile ----------
    public static void createExcelFile(String nomFichier) throws IOException {
        File txtFile = new File(nomFichier);
        if (!txtFile.exists()) throw new FileNotFoundException("Fichier texte introuvable : " + nomFichier);

        List<String[]> lignes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == PIXEL_COUNT + 1) {
                    lignes.add(parts);
                } else {
                    System.err.println("Ligne ignorée (mauvais format) : " + line);
                }
            }
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Chiffres");

        // En-têtes
        Row header = sheet.createRow(0);
        for (int i = 0; i < PIXEL_COUNT; i++) {
            header.createCell(i).setCellValue("pixel" + i);
        }
        header.createCell(PIXEL_COUNT).setCellValue("label");

        // Données
        int rowNum = 1;
        for (String[] parts : lignes) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < parts.length; i++) {
                if (i < PIXEL_COUNT) {
                    try {
                        double val = Double.parseDouble(parts[i].trim());
                        row.createCell(i).setCellValue(val);
                    } catch (NumberFormatException e) {
                        row.createCell(i).setCellValue(0);
                    }
                } else {
                    row.createCell(i).setCellValue(parts[i].trim());
                }
            }
        }

        // Ajuster les colonnes
        for (int i = 0; i <= PIXEL_COUNT; i++) {
            sheet.autoSizeColumn(i);
        }

        String excelName = nomFichier.replaceFirst("[.][tT][xX][tT]$", "") + ".xlsx";
        try (FileOutputStream fos = new FileOutputStream(excelName)) {
            workbook.write(fos);
        }
        workbook.close();
        System.out.println("Fichier Excel créé : " + excelName);
    }
}