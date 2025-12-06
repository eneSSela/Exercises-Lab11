package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * A multithreaded implementation of SumMatrix.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int n;

    /**
     * @param n number of worker threads to use. Must be positive.
     */
    public MultiThreadedSumMatrix(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of threads must be positive.");
        }
        this.n = n;
    }

    @Override
    public double sum(double[][] matrix) {

        final int rows = matrix.length;
        final int base = rows/this.n;
        final int extra = rows % this.n;

        final List<Worker> workers = new ArrayList<>();

        int start = 0;
        for (int i = 0; i < this.n; i++) {
            final int size;
            if (i < extra) {
                size = base + 1;
            } else {
                size = base;
            }
            final int end = start + size;
            workers.add(new Worker(matrix, start, end));
            start = end;
        }

        for (final Worker w : workers) {
            w.start();
        }

        double total = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                total += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return total;
    }

    /**
     * Worker thread that sums a subset of the matrix rows.
     */
    private static final class Worker extends Thread {

        private final double[][] data;
        private final int startRow;
        private final int endRow;
        private volatile double result;
        
        public Worker(double[][] matrix, int start, int end) {
            super();
            this.startRow = start;
            this.endRow = end;

            final int size = end - start;
            this.data = new double[size][];

            for (int i = 0; i < size; i++) {
                final double[] src = matrix[start+i];
                final double[] copy = new double[src.length];
                System.arraycopy(src, 0, copy, 0, src.length);
                this.data[i] = copy;
            }
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            System.out.println("Worker handling rows " + startRow + "to" + (endRow - 1));

            double sum = 0;
            for (final double[] row : this.data) {
                for (final double v : row) {
                    sum += v;
                }
            }
            this.result = sum;
        }

        public synchronized double getResult() {
            return this.result;
        }
    }
}
