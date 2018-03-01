package moea;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GA {

    DataGenerator dg = new DataGenerator();
    Prims prim = new Prims();
    private ExecutorService pool;

    private Pixel[][] pixels = {};

    public void run(int imgNbr, int popSize) {
        pixels = dg.readImage(String.valueOf(imgNbr));


        ArrayList<ArrayList<Pixel>> MSTs = new ArrayList<ArrayList<Pixel>>();
        threadGenerateMST(popSize, MSTs);


        System.out.println("populationSize: " + MSTs.size());
    }



    private void threadGenerateMST(int popSize, ArrayList<ArrayList<Pixel>> MSTs) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Callable<ArrayList<Pixel>> callableTask = () -> {
            return this.prim.algorithm(pixels);
        };

        List<Callable<ArrayList<Pixel>>> callableTasks = new ArrayList<>();
        for (int t = 0; t < popSize; t++) {
            callableTasks.add(callableTask);
            System.out.println("creating task :" + t);
        }

        try {
            long startTime = System.currentTimeMillis();
            List<Future<ArrayList<Pixel>>> futures = pool.invokeAll(callableTasks);

            for(int i = 0; i < futures.size(); i++){
                try {
                    MSTs.add(futures.get(i).get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Time used in total: " + (endTime-startTime));
        } catch (InterruptedException e) {
            System.out.println("Futures invokeAll fucked up\n" + e);
        }

        pool.shutdown();
        while(! pool.isTerminated()){

        }
    }


    public static void main(String[] args) {
        GA g = new GA();
        g.run(9, 1);
    }

}
