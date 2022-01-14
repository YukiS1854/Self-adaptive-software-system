package adasim.algorithm.delay;

public class SensorDelayFunction implements TrafficDelayFunction {

    @Override
    public int getDelay(int weight, int cutoff, int number) {
        double num = Math.random() * 100;
        if (num > 99.9) {
            return Integer.MAX_VALUE;
        } else
            return 0;
    }

}
