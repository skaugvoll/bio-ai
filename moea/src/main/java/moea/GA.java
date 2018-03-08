package moea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
        threadGenerateIndividuals(MSTs, population, 3, 10);

        System.out.println("populationSize: " + MSTs.size());
        System.out.println("Score bitch: " + population.get(0).getFitness());

        System.out.println("Fåkk åff");

        // we have found population, but not sorted!
        population = fastNonDominatedSort(population);

        DataGenerator dg = new DataGenerator();
        Chromosome toBePrinted = population.get(0);
        toBePrinted.findEdgePixels();
        dg.drawSegments(toBePrinted.segments);
        dg.drawTrace(toBePrinted, true);
        dg.drawTrace(toBePrinted, false);

        ArrayList<Chromosome> offsprings = new ArrayList<>();
        int i = 0;
        while(i < population.size()-3){
            Chromosome pi = population.get(i);
            Chromosome pj = population.get(i+1);
            Chromosome parent1 = nichedParetoBinaryTournamentSelection(pi,pj,population,offsprings);

            i = i + 2;
            pi = population.get(i);
            pj = population.get(i+1);
            Chromosome parent2 = nichedParetoBinaryTournamentSelection(pi,pj,population,offsprings);

            // perform crossover with parent 1 and parent 2 and create offspring c1 and c2.
            // perform mutation on c1 and c2.
            // offsprings.add(c1);
            // offsprings.add(c2);

            i++;
            if(i < population.size()-3){
                // just continue
            }

            else if(offsprings.size() == population.size() / 2){
                Collections.shuffle(population);
                i = 1;
                // then continue
            }
            else{
                break;
            }
        }

        // Then Combine Parents and offsprings --> R
        // Sort R based on fast-non-dominating-sort
        System.out.println("Let ME in, or out");





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


    private void threadGenerateIndividuals(ArrayList<MST> MSTs, ArrayList<Chromosome> population, int minSegments, int maxSegments) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Callable<Chromosome>> callableTasks = new ArrayList<>();
        for (int t = 0; t < MSTs.size(); t++) {
            int finalT = t;
            callableTasks.add(() -> {
                MST mst = MSTs.get(finalT);
                return new Chromosome(mst, mst.fuckersVisited.size(), minSegments, maxSegments, new double[]{0.5,0.5});
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

    private Chromosome nichedParetoBinaryTournamentSelection(Chromosome pi, Chromosome pj, ArrayList<Chromosome> population, ArrayList<Chromosome> offsprings){
        Random r = new Random();
        // Step 3: Pick randomly a compariosn set of individuals fro mthe population.
        // Pi and Pj are compared by first picking a sub-pop T of size Tdom (<<N) solutions from the population
        int TdomSize = 3; // remember to make this dynamic.
        Chromosome[] comparisonSet = new Chromosome[TdomSize];
        for (int c = 0; c < TdomSize; c++) {
            comparisonSet[c] = population.get(r.nextInt(population.size()));
        }

        // Step 4: Compare each candidate  against each individual in the comparison set for domination using the conditions for domination.
        int piDominates = 0;
        int piIsdominated = 0;
        int pjDominates = 0;
        int pjIsdominated = 0;
        for (Chromosome q : comparisonSet) {
            if (dominates(pi, q)) {
                piDominates++;
            } else if (dominates(q, pi)) {
                piIsdominated++;
            }

            if (dominates(pj, q)) {
                pjDominates++;
            } else if (dominates(q, pj)) {
                pjIsdominated++;
            }
        }

        // step 5: if one candidate is dominated by the comparison set while the other is not, then select the later for reproduction and go to Step 7, else proceed to Step 6.
        Chromosome selected = null;
        if (piIsdominated == 0 && pjIsdominated > 0) {
            selected = pi;
        } else if (pjIsdominated == 0 && piIsdominated > 0) {
            selected = pj;
        }


        // Step 6: If neither or both candidates are dominated by the comparison set, then use sharing to choose the winner.
        if (selected == null) {
            // TODO: step 6 : implement sharing
            selected = sharing(pi,pj, offsprings);
        }
        return selected;
    }

    private Chromosome sharing(Chromosome i, Chromosome j, ArrayList<Chromosome> q){
        // page 218-220 : https://tinyurl.com/yazoes7z
        if(q.size() < 2){
            double prob = new Random().nextDouble();
            if(prob<0.5){
                return i;
            }
            else{
                return j;
            }
        }

        double nci = 0;
        double ncj = 0;

        double shareDistance = 10; // TODO: sjekke om denne er fryktelig lav eller høy nå! sjekk om res1 og res2
        double maxEdge = Double.MIN_VALUE;
        double maxOD = Double.MIN_VALUE;
        double minEdge = Double.MAX_VALUE;
        double minOD = Double.MAX_VALUE;

        for(Chromosome k : q){
            if(k.edgeValue > maxEdge){
                maxEdge = k.edgeValue;
            }
            if(k.edgeValue < minEdge){
                minEdge = k.edgeValue;
            }
            if(k.overallDeviation > maxOD){
                maxOD = k.overallDeviation;
            }
            if(k.overallDeviation < minOD){
                minOD = k.overallDeviation;
            }
        }


        double obji1 = 0;
        double obji2 = 0;

        double objj1 = 0;
        double objj2 = 0;
        for(Chromosome k : q){
            obji1 += Math.pow( (i.edgeValue - k.edgeValue) / (maxEdge - minEdge), 2);
            obji2 += Math.pow( (i.overallDeviation - k.overallDeviation) / (maxOD - minOD), 2);
            double res1 = Math.sqrt((obji1 + obji2));

            objj1 += Math.pow( (j.edgeValue - k.edgeValue) / (maxEdge - minEdge), 2);
            objj2 += Math.pow( (j.overallDeviation - k.overallDeviation) / (maxOD - minOD), 2);
            double res2 = Math.sqrt((objj1 + objj2));

            if(res1 < shareDistance){
                nci++;
            }
            else if(res2 < shareDistance){
                ncj++;
            }
        }

        if(nci <= ncj){
            return i;
        }
        else {
            return j;
        }
    }



    public static void main(String[] args) {
        GA g = new GA();
        g.run(1, 4);
    }

}
