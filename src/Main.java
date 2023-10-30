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

        return result;
    }

    /**
     *
     * @param constraintsMatrix A matrix representing the coefficients of the
     *                          constraints.
     * @param rightNumbers A vector of right-hand side numbers from constraints.
     *                     (Matrix with 1 column and n rows).
     * @return Matrix with 1 column and n rows, representing the coordinates
     * of the point that lies in the interior of the feasible region.
     * The output vector must satisfy these conditions:
     * constraintsMatrix * output vector == rightNumbers.
     * NO element of output vector must be zero (0).
     *
     */
    public static Matrix chooseInteriorPoint(Matrix constraintsMatrix, Matrix rightNumbers){
        return multiply( multiply (transpose(constraintsMatrix), (multiply(transpose(constraintsMatrix),constraintsMatrix)).inverse() ) , rightNumbers);
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
        System.out.println("(accuracy = " + accuracy + ")");
        for (int i; i < x.getRows(); i++) {
            System.out.println("X" + i + " = " + x.getElement(i, 0));
        }
        System.out.println("z = " + answer);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Is it max or min problem? (0 for min, 1 for max)");
        int typeOfProblem = in.nextInt();
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
        for(int j = 0; j < 2; j++){
            Matrix x = chooseInteriorPoint(constraintsMatrix, rightNumbers);
            double alpha;
            if(j == 0){
                alpha = 0.5;
            }
            else{
                alpha = 0.9;
            }
            double answer = 0;
            while (true) {
                for (int i = 0; i < D.getColumns(); i++) {
                    D.setElement(i, i, x.getElement(i, 0));
                }
                Matrix A_ = Matrix.multiply(constraintsMatrix, D);
                Matrix C_ = Matrix.multiply(D, coefficients);
                Matrix inv = Matrix.multiply(A_, A_.transpose()).inverse();
                Matrix m = Matrix.multiply(A_.transpose(), inv);
                for (int i = 0; i < I.getColumns(); i++) {
                    I.setElement(i, i, 1);
                }
                Matrix P = I.subtract(Matrix.multiply(m, A_));
                Matrix Cp = Matrix.multiply(P, C_);
                nu = findNu(Cp);
                for (int i = 0; i < I.getColumns(); i++) {
                    I.setElement(i, i, alpha / nu);
                }
                Matrix x_ = ones.add(Matrix.multiply(I, Cp));
                x = Matrix.multiply(D, x_);
                if(Matrix.multiply(coefficients.transpose(), x).getElement(0, 0) - answer < Math.pow(10, -1*accuracy)){
                    answer = Matrix.multiply(coefficients.transpose(), x).getElement(0, 0);
                    break;
                }
                answer = Matrix.multiply(coefficients.transpose(), x).getElement(0, 0);
            }
            printAnswer(x, answer, accuracy);
        }



    }
}
