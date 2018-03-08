package moea;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class GA {

    DataGenerator dg = new DataGenerator();
    Prims prim = new Prims();
    private ExecutorService pool;
    Random r = new Random();
    private Pixel[][] pixels = {};

    ArrayList<ArrayList<Chromosome>> populationFronts = new ArrayList<ArrayList<Chromosome>>();

    public void run(int imgNbr, int popSize) {
        pixels = dg.readImage(String.valueOf(imgNbr));


        ArrayList<MST> MSTs = new ArrayList<>();
        threadGenerateMST(popSize, MSTs);

        ArrayList<Chromosome> population = new ArrayList<>();
        threadGenerateIndividuals(MSTs, population);

        populationFronts = fastNonDominatedSort(population);
        ArrayList<Chromosome> parents = tournamentSelection(population);

        System.out.println("populationSize: " + MSTs.size());
        System.out.println("Score bitch: " + population.get(0).getFitness());
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

    private ArrayList<ArrayList<Chromosome>> fastNonDominatedSort(ArrayList<Chromosome> population){
        ArrayList<Chromosome> F1 = new ArrayList<>(); // this is the first pareto front.


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
                F1.add(ch);
            }
        }
        // now we are done with finding the first front line
        ArrayList<ArrayList<Chromosome>> Fi = new ArrayList<>(); // this should hold all pareto fronts.
        Fi.add(F1);

        int i = 1;
        while(!Fi.get(i-1).isEmpty()){ // vi vil ha size hær kansje, siden flere kan få sp = ø ??
            ArrayList<Chromosome> currentFront = Fi.get(i-1);
            ArrayList<Chromosome>  newFront = new ArrayList<>();

            for(Chromosome ch : currentFront){
                for(Chromosome q : ch.sp){
                    q.np -= 1;
                    if(q.np == 0){
                        q.rank = i + 1;
                        newFront.add(q);
                    }
                }
            }
            i++;
            Fi.add(newFront);

        }
        System.out.println("smook smook smook");
        calculateCrowdingDistance(Fi);
        return Fi;
    }

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

    private void calculateCrowdingDistance(ArrayList<ArrayList<Chromosome>> Fi) {
        for(ArrayList<Chromosome> front: Fi){
            ArrayList<Chromosome> overallDeviation = front;
            ArrayList<Chromosome> edgeValue = front;
            overallDeviation.sort(new OverallDeviationComparator());
            edgeValue.sort(new EdgeValueComparator());
            front.get(0).crowdingDistance = Double.MAX_VALUE;
            front.get(front.size()-1).crowdingDistance = Double.MAX_VALUE;
            for(int i = 1; i <front.size(); i++){
                
            }
        }
    }

    private ArrayList<Chromosome> tournamentSelection(ArrayList<Chromosome> population) {
        ArrayList<Chromosome> parents = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            Chromosome p1 = population.get(r.nextInt(population.size()));
            Chromosome p2 = population.get(r.nextInt(population.size()));
            if(p1.rank < p2.rank){
                parents.add(p1);
            }
            else if(p2.rank < p1.rank){
                parents.add(p2);
            }
            else{

            }
        }
        return parents;
    }




    public static void main(String[] args) {
        GA g = new GA();
        g.run(1, 5);
    }

}
