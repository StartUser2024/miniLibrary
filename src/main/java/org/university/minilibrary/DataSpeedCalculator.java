package org.university.minilibrary;

public class DataSpeedCalculator implements Runnable{
    DataReader dataReader;
    long lastCount = 0;
    long readInHalfSecond;

    @Override
    public void run() {
        // Логика расчета скорости
        readInHalfSecond = dataReader.count;
        readInHalfSecond -= lastCount;
        lastCount = dataReader.count;
        if ((readInHalfSecond / (512*1024*1024)) > 1) {
            double factor = (double) readInHalfSecond;
            factor = factor / (512*1024*1024);
            String resultOfSpeed = String.format("%.2f", factor);
            System.out.print("\r");
            System.out.print("Data acquisition rate: " + resultOfSpeed + " Gb/sec");
            System.out.flush();

        } else if ((readInHalfSecond / (512*1024)) > 1) {
            double factor = (double) readInHalfSecond;
            factor = factor / (512*1024);
            String resultOfSpeed = String.format("%.2f", factor);
            System.out.print("\r");
            System.out.print("Data acquisition rate: " + resultOfSpeed + " Mb/sec");
            System.out.flush();

        } else if ((readInHalfSecond / 512) > 1) {
            double factor = (double) readInHalfSecond;
            factor = factor / (512);
            String resultOfSpeed = String.format("%.2f", factor);
            System.out.print("\r");
            System.out.print("Data acquisition rate: " + resultOfSpeed + " Kb/sec");
            System.out.flush();
        }else {
            System.out.print("\r");
            System.out.print("Data acquisition rate: " + readInHalfSecond * 2 + " b/sec");
            System.out.flush();
        }
        readInHalfSecond = 0;
    }
}
