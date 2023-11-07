import java.text.DecimalFormat;
import java.util.Scanner;
import java.lang.Math;


public class Main {
    /**
     *
     * @param cp Matrix with 1 column and n rows
     * @return the negative component of cp having the largest absolute value
     */
    public static double findNu(Matrix cp) {
        double result = 0.0;
        for (int i = 0; i < cp.getRows(); i++) {
            result = Math.min(result, cp.getElement(i, 0));
        }

        return Math.abs(result);
    }


    /**
     * Prints the answer in the following form:
     * for all elements in x:
     *      'Xi' = 'value inside Xi rounded by n decimal places, where n == accuracy'
     * 'z' = answer rounded by n decimal places, where n == accuracy
     * Example:
     * (accuracy = 2)
     * X1 = 1.23
     * X2 = 2.53
     * ...
     * Xn = 5.19
     * z = 34.17
     * @param x decision variables. Matrix with 1 column and n rows
     * @param answer the final answer of a problem
     * @param accuracy integer number representing the number of decimal places
     */
    public static void printAnswer(Matrix x, double answer, int accuracy) {
        StringBuilder patternBuilder = new StringBuilder("#."); // Start building the pattern

        for (int i = 0; i < accuracy; i++) {
            patternBuilder.append("#"); // Add a placeholder for each decimal place
        }

        DecimalFormat decimalFormat = new DecimalFormat(patternBuilder.toString());

        for (int i = 0; i < x.getRows(); i++) {
            System.out.println("X" + i + " = " + decimalFormat.format(x.getElement(i, 0)));
        }

        System.out.println("z = " + decimalFormat.format(answer));
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Important: Inputs are considered for canonical form of the Linear Programming Problem\nIs it max or min problem? (0 for min, 1 for max)");
        int typeOfProblem = in.nextInt();
        System.out.println("\nExample: min Z = -2X1 + X2\nSubject: X1 - X2 <= 15\n         X2 <= 15\n         X1, X2 >= 0");
        System.out.println("\nIn Canonical Form:\nmin Z = -2X1 + X2 + 0S1 + 0S2\nSubject: X1 - X2 + S1 + 0S2 = 15\n         0X1 + X2 + 0S1 + S2 = 15\n");
        System.out.println("Number of variables in the objective function is: 4\nNumber of constraints is: 2\nVector of coefficients of objective function - C is: -2 1 0 0");
        System.out.println("Matrix of coefficients of constraint function - A is: 1 -1 1 0\n                                                      0 1 0 1");
        System.out.println("Vector of right-hand side numbers - b is: 15 15\nThe approximation accuracy e.g: 20\nCoordinates of the interior point X e.g: 10 2 7 13\nYour turn :)\n");
        System.out.println("Enter the number of variables in the objective function:");
        int numOfVariables = in.nextInt();
        System.out.println("Enter the number of constraints:");
        int numOfConstraints = in.nextInt();
        System.out.println("Enter the vector of coefficients of objective function - C:");
        Matrix coefficients = new Matrix(numOfVariables, 1); // c

        for (int i = 0; i < coefficients.getRows(); i++) {
            coefficients.setElement(i, 0, in.nextFloat());
        }

        System.out.println("Enter the matrix of coefficients of constraint function - A:");
        Matrix constraintsMatrix = new Matrix(numOfConstraints, numOfVariables); // A

        for (int i = 0; i < constraintsMatrix.getRows(); i++) {
            for (int j = 0; j < constraintsMatrix.getColumns(); j++) {
                constraintsMatrix.setElement(i, j, in.nextFloat());
            }
        }

        System.out.println("Enter the vector of right-hand side numbers - b:");
        Matrix rightNumbers = new Matrix(numOfConstraints, 1); // b

        for (int i = 0; i < rightNumbers.getRows(); i++) {
            rightNumbers.setElement(i, 0, in.nextFloat());
        }

        System.out.println("The approximation accuracy (integer number representing the number of decimal places):");
        int accuracy = in.nextInt();

        if(typeOfProblem == 0){
            for (int i = 0; i < coefficients.getRows(); i++){
                if(coefficients.getElement(i, 0) != 0.0){
                    coefficients.setElement(i, 0, -1*coefficients.getElement(i, 0));
                }
            }
        }

        double nu;


        Matrix I = new Matrix(numOfVariables, numOfVariables);

        Matrix ones = new Matrix(numOfVariables, 1);
        for (int i = 0; i < ones.getRows(); i++){
            ones.setElement(i, 0, 1);
        }

        Matrix D = new Matrix(numOfVariables, numOfVariables);
        Matrix interiorPoint = new Matrix(constraintsMatrix.getColumns(), 1);
        System.out.println("Enter the coordinates of the interior point X:");

        for (int i = 0; i < interiorPoint.getRows(); i++) {
            interiorPoint.setElement(i, 0, in.nextFloat());
        }
        for(int j = 0; j < 2; j++){
            Matrix x = new Matrix(interiorPoint);
            double alpha;
            if(j == 0){
                alpha = 0.5;
            }
            else{
                alpha = 0.9;
            }
            System.out.println();
            System.out.println("for alpha = " + alpha + ":");
            double answer = 0;
            while (true) {
                for (int i = 0; i < D.getColumns(); i++) {
                    D.setElement(i, i, x.getElement(i, 0));
                }
                Matrix A_ = Matrix.multiply(constraintsMatrix, D);
                Matrix C_ = Matrix.multiply(D, coefficients);
                Matrix inv = Matrix.multiply(A_, A_.transpose()).inverse();
                for (int i = 0; i < inv.getRows(); i++){
                    for(int k = 0; k < inv.getColumns(); k++){
                        if(inv.getElement(i, k) == Double.POSITIVE_INFINITY ||
                                Double.isNaN(inv.getElement(i, k)) || inv.getElement(i, k) == Double.NEGATIVE_INFINITY){
                            System.out.println("The method is not applicable!");
                            return;
                        }
                    }
                }
                Matrix m = Matrix.multiply(A_.transpose(), inv);
                for (int i = 0; i < I.getColumns(); i++) {
                    I.setElement(i, i, 1);
                }
                Matrix P = I.subtract(Matrix.multiply(m, A_));
                Matrix Cp = Matrix.multiply(P, C_);

                nu = findNu(Cp);
                if(nu == 0){
                    System.out.println("The problem does not have solution!");
                    return;
                }
                for (int i = 0; i < I.getColumns(); i++) {
                    I.setElement(i, i, alpha / nu);
                }
                Matrix x_ = ones.add(Matrix.multiply(I, Cp));
                x = Matrix.multiply(D, x_);
                if(Math.abs(Matrix.multiply(coefficients.transpose(), x).getElement(0, 0) - answer) < Math.pow(10, -1*accuracy)){
                    answer = Matrix.multiply(coefficients.transpose(), x).getElement(0, 0);
                    break;
                }
                answer = Matrix.multiply(coefficients.transpose(), x).getElement(0, 0);
            }
            if(typeOfProblem == 0){
                answer = -answer;
            }
            printAnswer(x, answer, accuracy);
        }



    }
}
