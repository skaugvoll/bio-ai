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

        ArrayList<MST> MSTs = new ArrayList<>();
        threadGenerateMST(popSize, MSTs);

        ArrayList<Chromosome> population = new ArrayList<>();
        threadGenerateIndividuals(MSTs, population);

        // we have found population, but not sorted!
        population = fastNonDominatedSort(population);


        //Then at first:
        // usual binary tournement selection
        // crossover
        // mutation

        // Q = offsprings = result of these 3.  | Q | = | population |

        // Then Combine Parents and offsprings --> R
        // Sort R based on fast-non-dominating-sort
        





    }


    private void threadGenerateMST(int popSize, ArrayList<MST> MSTs) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Callable<MST> callableTask = () -> {
            return this.prim.algorithm(pixels);
        };

        List<Callable<MST>> callableTasks = new ArrayList<>();
        for (int t = 0; t < popSize; t++) {
            callableTasks.add(callableTask);
            System.out.println("creating task :" + t);
        }

        try {
            long startTime = System.currentTimeMillis();
            List<Future<MST>> futures = pool.invokeAll(callableTasks);

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


    private void threadGenerateIndividuals(ArrayList<MST> MSTs, ArrayList<Chromosome> population) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Callable<Chromosome>> callableTasks = new ArrayList<>();
        for (int t = 0; t < MSTs.size(); t++) {
            int finalT = t;
            callableTasks.add(() -> {
                MST mst = MSTs.get(finalT);
                return new Chromosome(mst, mst.fuckersVisited.size(), 30, new double[]{0.5,0.5});
            });
            System.out.println("creating segmenting task :" + t);
        }

        try {
            long startTime = System.currentTimeMillis();
            List<Future<Chromosome>> futures = pool.invokeAll(callableTasks);

            for(int i = 0; i < futures.size(); i++){
                try {
                    population.add(futures.get(i).get());
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
        while(! pool.isTerminated()){ }
    }


    private ArrayList<Chromosome> fastNonDominatedSort(ArrayList<Chromosome> population){
//        ArrayList<Chromosome> F1 = new ArrayList<>(); // this is the first pareto front.
        ArrayList<Chromosome> Fi = new ArrayList<>(); // this should hold all pareto fronts.


        // find the first front line
        for(Chromosome ch : population){
            ch.np = 0;
            ch.sp.clear();

            for(Chromosome q : population){
                if(q.equals(ch)){
                    continue;
                }

                if(dominates(ch,q)){
                    ch.sp.add(q);
                }
                else if(dominates(q,ch)){
                    ch.np += 1;
                }
            }
            if (ch.np == 0){
               ch.rank = 1;
               Fi.add(ch);
            }
        }
        // now we are done with finding the first front line

        int i = 1;
        while(!Fi.get(i-1).sp.isEmpty()){ // vi vil ha size hær kansje, siden flere kan få sp = ø ??
            Chromosome currentFront = Fi.get(i-1);
            ArrayList<Chromosome>  newFront = new ArrayList<>();

//            for(Chromosome ch : currentFront){
                for(Chromosome q : currentFront.sp){
                    q.np -= 1;
                    if(q.np == 0){
                        q.rank = i + 1;
                        Fi.add(q);
                    }
                }
//            }
            i++;

        }
        System.out.println("smook smook smook");
        return Fi;
    }

    /***
     A solution x(1) is said to dominate the other solution x(2),
     if both the following conditions are true:
     1. The solution x(1) is no worse than x(2) in all objectives.
     2. The solution x(1) is strictly better than x(2) in at least one objective.
     * @param ch: Chromosome :
     * @param q: Chromosme :
     * @return : boolean : True if ch dominates q
     */

    private boolean dominates(Chromosome ch, Chromosome q) {
        boolean dominates = true;

        // condition 1:
        if (! (ch.edgeValue <= q.edgeValue && ch.overallDeviation <= q.overallDeviation)) {
            dominates = false;
        }

        // condition 2:
        if (! (ch.edgeValue < q.edgeValue || ch.overallDeviation < q.overallDeviation)){
            dominates = false;
        }

        return dominates;
    }


    public static void main(String[] args) {
        GA g = new GA();
        g.run(1, 10);
    }

}
