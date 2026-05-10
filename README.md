# Handwritten Digit Recognition (3 & 5) — Java Mini Project

**Course:** Object-Oriented Programming (Java) — Level 1TA2  
**Institution:** Advanced Technologies — ENS  
**Academic Year:** 2025/2026  
**Instructor:** Mohamed Mahmoud Moussa  

---

## Project Overview

This project implements a complete pipeline for **handwritten digit recognition** (specifically digits **3** and **5**) using the famous **MNIST dataset**. It is divided into two main parts:

1. **File Processing** — Reading binary MNIST files, converting between text/image/Excel formats.
2. **Machine Learning** — Training and evaluating classifiers (Naive Bayes and Random Forest) using the **Weka** library.

---

## Features

### Part 1: File Processing (`Util.java`)
| Method | Description |
|--------|-------------|
| `createTextField(int n)` | Extracts `n` samples of digit **3** and `n` samples of digit **5** from the MNIST binary files and saves them as `output/chiffres.txt` (784 pixel values + label per line). |
| `imageToFile(String nomImage)` | Converts a **28×28 PNG image** into a text file containing 784 comma-separated pixel intensities (grayscale). |
| `fileToImage(String nomFichier)` | Converts a text file of 784 comma-separated values back into a **28×28 PNG grayscale image**. |
| `createExcelFile(String nomFichier)` | Converts a text data file into an **Excel `.xlsx` file** using Apache POI (784 pixel columns + 1 label column). |

### Part 2: Machine Learning (`WekaApp.java`)
- Generates **800 samples** (400 × "trois", 400 × "cinq").
- Splits data into **training set (600)** and **test set (200)**.
- Converts datasets to **ARFF format** (Weka-compatible).
- Trains and evaluates two classifiers:
  - **Naive Bayes**
  - **Random Forest** (100 trees)
- Displays accuracy, confusion matrix, and detailed statistics.

### Testing (`Test.java`)
- Automated test class that validates all `Util` methods.

---

## Project Structure

```
JavaMiniProject/
├── src/
│   └── JavaMiniProject/
│       ├── Util.java          # File processing utilities
│       ├── WekaApp.java       # Machine learning pipeline
│       └── Test.java          # Unit tests for Util
├── data/
│   ├── train-images.idx3-ubyte   # MNIST images (binary)
│   └── train-labels.idx1-ubyte   # MNIST labels (binary)
├── output/
│   ├── chiffres.txt          # Generated dataset (800 lines)
│   ├── train.txt             # Training subset
│   ├── test.txt              # Test subset
│   ├── train-data.arff       # Weka training file
│   └── test-data.arff        # Weka test file
└── lib/
    ├── weka.jar              # Weka ML library
    └── poi-*.jar             # Apache POI dependencies
```

---

## Prerequisites

- **Java JDK 8+**
- **Weka Library** (`weka.jar`) — [Download from waikato.ac.nz](https://www.cs.waikato.ac.nz/ml/weka/)
- **Apache POI** — for Excel file generation
- **MNIST Dataset** — Download `train-images.idx3-ubyte` and `train-labels.idx1-ubyte` from [Kaggle MNIST](https://www.kaggle.com/datasets/hojjatk/mnist-dataset) and place them in the `data/` folder.

---

## Setup Instructions

### 1. Add Dependencies
Add the following JARs to your project's build path:
- `weka.jar`
- Apache POI JARs (`poi-5.x.x.jar`, `poi-ooxml-5.x.x.jar`, and dependencies)

### 2. Prepare MNIST Data
Download the MNIST training files and place them in:
```
data/train-images.idx3-ubyte
data/train-labels.idx1-ubyte
```

### 3. Compile & Run

**Test file processing:**
```bash
javac -cp ".:lib/*" JavaMiniProject/Test.java
java -cp ".:lib/*" JavaMiniProject.Test
```

**Run Machine Learning pipeline:**
```bash
javac -cp ".:lib/*" JavaMiniProject/WekaApp.java
java -cp ".:lib/*" JavaMiniProject.WekaApp
```

> *On Windows, replace `:` with `;` in the classpath.*

---

## Usage Examples

### Generate Text Dataset
```java
Util.createTextField(400);  // Creates output/chiffres.txt with 800 lines
```

### Convert Image to Text
```java
Util.imageToFile("my_digit.png");  // Creates my_digit.txt
```

### Convert Text to Image
```java
Util.fileToImage("my_digit.txt");  // Creates my_digit.png
```

### Convert to Excel
```java
Util.createExcelFile("output/chiffres.txt");  // Creates chiffres.xlsx
```

---

## Output Example (WekaApp)

```
=== Étape 1 : Génération de 800 échantillons ===
Fichier output/chiffres.txt créé avec 800 lignes.

=== Modèle 1 : Naive Bayes ===
Correctly Classified Instances     185      92.5 %
Incorrectly Classified Instances    15       7.5 %

=== Modèle 2 : Random Forest ===
Correctly Classified Instances     192      96.0 %
Incorrectly Classified Instances     8       4.0 %
```

---

## Notes

- The MNIST binary files use **big-endian** format. The reader handles magic numbers (`2051` for images, `2049` for labels) automatically.
- All images are strictly **28×28 pixels**, grayscale (0 = black, 255 = white).
- Labels in text files are stored as strings: `"trois"` and `"cinq"`.
- The ARFF files use `{trois, cinq}` as the nominal class attribute.

---

## Authors

- Ali Lazzem **1TA2 — Advanced Technologies**
- Supervised by **Mohamed Mahmoud Moussa**
