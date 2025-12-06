package it.unibo.oop.workers02;

/**
 * A multithreaded implementation of SumMatrix.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int n;

    public MultiThreadedSumMatrix(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of threads must be positive.");
        }
        this.n = n;
    }

    @Override
    public double sum(double[][] matrix) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sum'");
    }
}
