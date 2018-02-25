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
        calculateNeighbourDistance();
      
//        dg.drawImage(pixels);

//        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        ArrayList<ArrayList<Pixel>> MSTs = new ArrayList<ArrayList<Pixel>>();
        threadGenerateMST(popSize, MSTs);

        ArrayList<Chromosome> population = new ArrayList<>();
        threadGeneratePopulation(MSTs, population);

//        System.out.println("2");
        System.out.println("populationSize: " + MSTs.size());
    }

    private void threadGeneratePopulation(ArrayList<ArrayList<Pixel>> MSTs, ArrayList<Chromosome> population) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

//        Callable<Chromosome> callableTask = () -> {
//            return new Chromosome();
//        };

        List<Callable<Chromosome>> callableTasks = new ArrayList<>();
        for (int t = 0; t < MSTs.size(); t++) {
            int finalT = t;
            callableTasks.add(() -> {
                return new Chromosome(MSTs.get(finalT), pixels.length * pixels[0].length, 1);
            });
            System.out.println("creating segmenting task :" + t);
        }

        try {
            System.out.println("Gonna try executin these badboys");

            long startTime = System.currentTimeMillis();
            List<Future<Chromosome>> futures = pool.invokeAll(callableTasks);
            long endTime = System.currentTimeMillis();

            System.out.println("Fåkk this badboys are invoked and done, all in just: " + (endTime - startTime)/1000);

            for(int i = 0; i < futures.size(); i++){
                try {
                    population.add(futures.get(i).get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }


            System.out.println("Time used in total: ");
        } catch (InterruptedException e) {
            System.out.println("Futures invokeAll fucked up\n" + e);
        }

        pool.shutdown();
        while(! pool.isTerminated()){
//            System.out.println("population size: " + population.size());
//            System.out.println("Waiting for termination.");
        }


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

//        System.out.println("1");

        pool.shutdown();
        while(! pool.isTerminated()){
//            System.out.println("population size: " + population.size());
//            System.out.println("Waiting for termination.");
        }
    }


    public void calculateNeighbourDistance() {
        int numberOfPRows = this.pixels.length;
        int numberOfPixelsPerRow = this.pixels[0].length;
        for (int row = 0; row < numberOfPRows; row++) {
            for (int pixel = 0; pixel < numberOfPixelsPerRow; pixel++) {
                Pixel p1 = this.pixels[row][pixel];
                for(int n = 0; n < 4; n++) {
                    if (p1.getNeighbours()[n][0] == -1) {
                        p1.setNeighboursDistance(n, -1);
                        continue;
                    }
                    int xv = p1.getNeighbours()[n][0];
                    int swish = p1.getNeighbours()[n][1];
                    Pixel p2 = pixels[p1.getNeighbours()[n][0]][p1.getNeighbours()[n][1]];
                    p1.setNeighboursDistance(n, this.RGBdistance(p1, p2));
                }
            }
        }
    }

    private double RGBdistance(Pixel p1, Pixel p2){
        return Math.sqrt(Math.pow(p1.getRGB()[0] - p2.getRGB()[0], 2) + Math.pow(p1.getRGB()[1] - p2.getRGB()[1], 2) + Math.pow(p1.getRGB()[2] - p2.getRGB()[2], 2));
    }

    public static void main(String[] args) {
        GA g = new GA();
        g.run(1, 1);
    }

}
