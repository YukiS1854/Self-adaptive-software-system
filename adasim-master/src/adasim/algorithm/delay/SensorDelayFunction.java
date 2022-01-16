package adasim.algorithm.delay;

import java.util.Random;

public class SensorDelayFunction implements TrafficDelayFunction {

    public int getDelay(int weight, int capacity, int number) {
        double prob = Math.random() * 100;
        Random generator = new Random();
        if (prob > 95) {
            return generator.nextInt(15);
        } else
            return Math.max(weight, number - capacity + weight);
    }

}
