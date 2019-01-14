import java.io.*;
import java.util.*;


public class MatrixMultiplication {
    public static void main(String[] args) {
        double timeTaken = 0;
        long startTime;
        int[][] A, B;
        int[][] result = new int[0][];
        Scanner scan = new Scanner(System.in);
        String option1 = "Iterative", option2 = "Recursive (Divide and Conquer)",
                option3 = "Classical Strassen's (base case 2^1)", option4 = "Modified Strassen's (base case 2^X)";
        String inputFile, outputFile = null;
        int size = 0;

        boolean flag = false;
        int choice = 0;
        while (!flag) {
            try {
                System.out.println("Select the method of matrix input:");
                System.out.println("1- Enter filenames\n2- Generate random matrices");
                System.out.print("\nEnter your choice: ");

                choice = scan.nextInt();
                if (choice >= 1 && choice <= 2) {
                    flag = true;
                } else {
                    System.out.println("\nInvalid choice. Please try again");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid choice. Please try again");
                scan.next();
            }
        }

        if (choice == 1) {
            System.out.print("Enter the filename (or path) of the input and output files: ");
            inputFile = scan.next();
            outputFile = scan.next();

            System.out.print("Enter the size of the matrix (in power of 2): ");
            size = scan.nextInt();

            System.out.println("Reading input file...");
            Object[] matrices = readMatrixFromFile(inputFile, size);
            A = (int[][]) matrices[0];
            B = (int[][]) matrices[1];
        } else {
            System.out.print("Enter the size of the matrix (in power of 2): ");
            size = scan.nextInt();
            A = generateRandomMatrix(size);
            B = generateRandomMatrix(size);
        }


        flag = false;
        while (!flag) {
            try {
                System.out.println("\nSelect the algorithm for computing matrix multiplication: ");
                System.out.println("1- " + option1 + "\n2- " + option2 + "\n3- " + option3 +
                        "\n4- " + option4 + "\n0- Exit");

                System.out.print("\nEnter your choice: ");
                choice = scan.nextInt();
                if (choice >= 0 && choice <= 4) {
                    flag = true;
                } else {
                    System.out.println("\nInvalid choice. Please try again");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid choice. Please try again");
                scan.next();
            }
        }

        switch (choice) {
            case 0:
                System.out.println("Terminating program.");
                System.exit(0);
            case 1:
                startTime = System.currentTimeMillis();
                System.out.println("\nRunning Iterative method...");
                result = iterativeMultiplication(A, B);
                timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("Time taken for iterative algorithm: " + timeTaken + "s");
                break;
            case 2:
                startTime = System.currentTimeMillis();
                System.out.println("\nRunning Recursive (Divide and Conquer)...");
                result = divideAndConquer(A, B);
                timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("Time taken for divide and conquer algorithm: " + timeTaken + "s");
                break;
            case 3:
                startTime = System.currentTimeMillis();
                System.out.println("\nRunning classical Strassen's algorithm...");
                result = classicalStrassen(A, B);
                timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("Time taken for classical Strassen's algorithm: " + timeTaken + "s");
                break;
            case 4:
                System.out.print("Enter the base case (in power of 2): ");
                int n = scan.nextInt();
                int baseCase = (int) Math.pow(2, n);
                startTime = System.currentTimeMillis();
                System.out.println("\nRunning modified Strassen's algorithm...");
                result = modifiedStrassen(A, B, baseCase);
                timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("Time taken for modified Strassen's algorithm (base case 2^" + n + "): " + timeTaken + "s");
                break;
        }

        writeToFile("Time taken: " + timeTaken + "s ", result, outputFile);
    }

    private static Object[] readMatrixFromFile(String path, int size) {
        int n = (int) Math.pow(2, size);
        int[][] A = new int[n][n];
        int[][] B = new int[n][n];
        Scanner scan;

        try {
            scan = new Scanner(new FileInputStream(path));
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A.length; j++) {
                    A[i][j] = scan.nextInt();
                }
            }

            for (int i = 0; i < B.length; i++) {
                for (int j = 0; j < B.length; j++) {
                    B[i][j] = scan.nextInt();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error in finding the input file; please check the file path.");
            System.exit(0);
        } catch (NumberFormatException e) {
            System.out.println("Error in reading the file; please reformat the file.");
            System.exit(0);
        } catch (NoSuchElementException e) {
            System.out.println("Error in reading the file; please enter the correct matrix size.");
            System.exit(0);
        }

        return new Object[]{A, B};
    }

    private static int[][] generateRandomMatrix(int size) {
        int x = (int) Math.pow(2, size);
        int[][] A = new int[x][x];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                A[i][j] = new Random().nextInt(201) - 100;  // [-100, 100]
            }
        }

        return A;
    }

    private static int[][] iterativeMultiplication(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    @SuppressWarnings("Duplicates")
    private static int[][] divideAndConquer(int[][] A, int[][] B) {
        int n = A.length;
        int[][] result = new int[n][n];
        // base case is 2x2 matrix
        if (n == 2) {
            result[0][0] = A[0][0] * B[0][0] + A[0][1] * B[1][0];
            result[0][1] = A[0][0] * B[0][1] + A[0][1] * B[1][1];
            result[1][0] = A[1][0] * B[0][0] + A[1][1] * B[1][0];
            result[1][1] = A[1][0] * B[0][1] + A[1][1] * B[1][1];
        } else {
            // create the sub matrices for A and initialize
            int[][] A11 = divideMatrix(A, 0, 0);
            int[][] A12 = divideMatrix(A, 0, n / 2);
            int[][] A21 = divideMatrix(A, n / 2, 0);
            int[][] A22 = divideMatrix(A, n / 2, n / 2);
            // create the sub matrices for B and initialize
            int[][] B11 = divideMatrix(B, 0, 0);
            int[][] B12 = divideMatrix(B, 0, n / 2);
            int[][] B21 = divideMatrix(B, n / 2, 0);
            int[][] B22 = divideMatrix(B, n / 2, n / 2);

            // conquer step; compute multiplication on the 4 sub matrices
            int[][] C11 = addMatrices(
                    divideAndConquer(A11, B11),
                    divideAndConquer(A12, B21)
            );

            int[][] C12 = addMatrices(
                    divideAndConquer(A11, B12),
                    divideAndConquer(A12, B22)
            );

            int[][] C21 = addMatrices(
                    divideAndConquer(A21, B11),
                    divideAndConquer(A22, B21)
            );

            int[][] C22 = addMatrices(
                    divideAndConquer(A21, B12),
                    divideAndConquer(A22, B22)
            );

            // merge the 4 sub matrices into result
            mergeMatrices(C11, result, 0, 0);
            mergeMatrices(C12, result, 0, n / 2);
            mergeMatrices(C21, result, n / 2, 0);
            mergeMatrices(C22, result, n / 2, n / 2);
        }

        return result;
    }

    @SuppressWarnings("Duplicates")
    private static int[][] classicalStrassen(int[][] A, int[][] B) {
        int n = A.length;
        int[][] result = new int[n][n];
        // base case is 2x2 matrix
        if (n == 2) {
            int a = A[0][0], b = A[0][1], c = A[1][0], d = A[1][1];
            int e = B[0][0], f = B[0][1], g = B[1][0], h = B[1][1];

            int p1 = a * (f - h);
            int p2 = (a + b) * h;
            int p3 = (c + d) * e;
            int p4 = d * (g - e);
            int p5 = (a + d) * (e + h);
            int p6 = (b - d) * (g + h);
            int p7 = (a - c) * (e + f);

            result[0][0] = p5 + p4 - p2 + p6;
            result[0][1] = p1 + p2;
            result[1][0] = p3 + p4;
            result[1][1] = p1 + p5 - p3 - p7;

        } else {
            // create the sub matrices for A and initialize
            int[][] A11 = divideMatrix(A, 0, 0);
            int[][] A12 = divideMatrix(A, 0, n / 2);
            int[][] A21 = divideMatrix(A, n / 2, 0);
            int[][] A22 = divideMatrix(A, n / 2, n / 2);
            // create the sub matrices for B and initialize
            int[][] B11 = divideMatrix(B, 0, 0);
            int[][] B12 = divideMatrix(B, 0, n / 2);
            int[][] B21 = divideMatrix(B, n / 2, 0);
            int[][] B22 = divideMatrix(B, n / 2, n / 2);

            // conquer step; compute multiplication on the 7 sub matrices
            int[][] P1 = classicalStrassen(A11, subtractMatrices(B12, B22));
            int[][] P2 = classicalStrassen(addMatrices(A11, A12), B22);
            int[][] P3 = classicalStrassen(addMatrices(A21, A22), B11);
            int[][] P4 = classicalStrassen(A22, subtractMatrices(B21, B11));
            int[][] P5 = classicalStrassen(addMatrices(A11, A22), addMatrices(B11, B22));
            int[][] P6 = classicalStrassen(subtractMatrices(A12, A22), addMatrices(B21, B22));
            int[][] P7 = classicalStrassen(subtractMatrices(A11, A21), addMatrices(B11, B12));

            // compute values of the main 4 sub matrices
            int[][] C11 = addMatrices(subtractMatrices(addMatrices(P5, P4), P2), P6);
            int[][] C12 = addMatrices(P1, P2);
            int[][] C21 = addMatrices(P3, P4);
            int[][] C22 = subtractMatrices(subtractMatrices(addMatrices(P1, P5), P3), P7);

            // merge the 4 sub matrices into result
            mergeMatrices(C11, result, 0, 0);
            mergeMatrices(C12, result, 0, n / 2);
            mergeMatrices(C21, result, n / 2, 0);
            mergeMatrices(C22, result, n / 2, n / 2);
        }

        return result;
    }

    @SuppressWarnings("Duplicates")
    private static int[][] modifiedStrassen(int[][] A, int[][] B, int baseCase) {
        int n = A.length;
        int[][] result = new int[n][n];
        // base case is NxN matrix
        if (n <= baseCase) {
            result = iterativeMultiplication(A, B);
        } else {
            // create the sub matrices for A and initialize
            int[][] A11 = divideMatrix(A, 0, 0);
            int[][] A12 = divideMatrix(A, 0, n / 2);
            int[][] A21 = divideMatrix(A, n / 2, 0);
            int[][] A22 = divideMatrix(A, n / 2, n / 2);
            // create the sub matrices for B and initialize
            int[][] B11 = divideMatrix(B, 0, 0);
            int[][] B12 = divideMatrix(B, 0, n / 2);
            int[][] B21 = divideMatrix(B, n / 2, 0);
            int[][] B22 = divideMatrix(B, n / 2, n / 2);

            // conquer step; compute multiplication on the 7 sub matrices
            int[][] P1 = modifiedStrassen(A11, subtractMatrices(B12, B22), baseCase);
            int[][] P2 = modifiedStrassen(addMatrices(A11, A12), B22, baseCase);
            int[][] P3 = modifiedStrassen(addMatrices(A21, A22), B11, baseCase);
            int[][] P4 = modifiedStrassen(A22, subtractMatrices(B21, B11), baseCase);
            int[][] P5 = modifiedStrassen(addMatrices(A11, A22), addMatrices(B11, B22), baseCase);
            int[][] P6 = modifiedStrassen(subtractMatrices(A12, A22), addMatrices(B21, B22), baseCase);
            int[][] P7 = modifiedStrassen(subtractMatrices(A11, A21), addMatrices(B11, B12), baseCase);

            // compute values of the main 4 sub matrices
            int[][] C11 = addMatrices(subtractMatrices(addMatrices(P5, P4), P2), P6);
            int[][] C12 = addMatrices(P1, P2);
            int[][] C21 = addMatrices(P3, P4);
            int[][] C22 = subtractMatrices(subtractMatrices(addMatrices(P1, P5), P3), P7);

            // merge the 4 sub matrices into result
            mergeMatrices(C11, result, 0, 0);
            mergeMatrices(C12, result, 0, n / 2);
            mergeMatrices(C21, result, n / 2, 0);
            mergeMatrices(C22, result, n / 2, n / 2);
        }

        return result;
    }

    private static int[][] divideMatrix(int[][] A, int row, int col) {
        int n = A.length / 2;
        int[][] B = new int[n][n];
        for (int i1 = 0, i2 = row; i1 < B.length; i1++, i2++) {
            for (int j1 = 0, j2 = col; j1 < B.length; j1++, j2++) {
                B[i1][j1] = A[i2][j2];
            }
        }

        return B;
    }

    private static void mergeMatrices(int[][] A, int[][] B, int row, int col) {
        for (int i1 = 0, i2 = row; i1 < A.length; i1++, i2++) {
            for (int j1 = 0, j2 = col; j1 < A.length; j1++, j2++) {
                B[i2][j2] = A[i1][j1];
            }
        }
    }

    private static int[][] addMatrices(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }

        return C;
    }

    private static int[][] subtractMatrices(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }

        return C;
    }

    // used for testing by comparing iterative matrix with X matrix
    private static void compareMatrices(long[][] A, long[][] B) {
        boolean flag = false;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                if (A[i][j] != B[i][j]) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag) {
            System.out.println("\nThe matrices are not equal.");
        } else {
            System.out.println("\nThe matrices are equal.");
        }
    }

    private static void printMatrix(long[][] A) {
        System.out.println();
        String format = "%-6d";
        for (long[] row : A) {
            for (long digit : row) {
                System.out.printf(format, digit);
            }
            System.out.println();
        }
    }

    @SuppressWarnings("Duplicates")
    private static void writeToFile(String message, int[][] matrix, String filename) {
        String format = "%-10d";
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(filename));
            printWriter.print(message);
            printWriter.print("\n");
            for (int[] row : matrix) {
                for (long digit : row) {
                    printWriter.printf(format, digit);
                }
                printWriter.print("\n");
            }

            printWriter.close();
            System.out.println("Output written to " + filename);
        } catch (IOException e) {
            System.out.println("Error in writing to file.");
            System.exit(0);
        } catch (NullPointerException ignored) {
        }
    }

    @SuppressWarnings("Duplicates")
    private static void writeMatricesToFile(long[][] A, long[][] B, String filename) {
        String format = "%-6d";
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(filename));
            for (long[] row : A) {
                for (long digit : row) {
                    printWriter.printf(format, digit);
                }
                printWriter.print("\n");
            }

            for (long[] row : B) {
                for (long digit : row) {
                    printWriter.printf(format, digit);
                }
                printWriter.print("\n");
            }
            printWriter.close();
        } catch (IOException e) {
            System.out.println("Error in writing to file.");
            System.exit(0);
        }
    }
}
