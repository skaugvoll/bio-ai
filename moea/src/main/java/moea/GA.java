package moea;

import com.rits.cloning.Cloner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void run(int imgNbr, int popSize, int maxGeneration) {
        pixels = dg.readImage(String.valueOf(imgNbr));


        ArrayList<MST> MSTs = new ArrayList<>();
        threadGenerateMST(popSize, MSTs);

        ArrayList<Chromosome> population = new ArrayList<>();
        threadGenerateIndividuals(MSTs, population);


        populationFronts = fastNonDominatedSort(population);

        ArrayList<Chromosome> parentsAndChidren = new ArrayList<>();
        parentsAndChidren.addAll(population);
        int generationCount = 0;
        while(generationCount < maxGeneration) {

            while (parentsAndChidren.size() < 2 * popSize) {
                ArrayList<Chromosome> parents = tournamentSelection(population);
                parentsAndChidren.add(crossOver(parents.get(0), parents.get(1)));
                parentsAndChidren.add(crossOver(parents.get(1), parents.get(0)));
            }
            populationFronts = fastNonDominatedSort(parentsAndChidren);

            ArrayList<Chromosome> newPopulation = new ArrayList<>();
            for (ArrayList<Chromosome> front : populationFronts) {
                if (newPopulation.size() == popSize) {
                    break;
                }
                if (newPopulation.size() + front.size() <= popSize) {
                    newPopulation.addAll(front);
                } else {
                    front.sort(new CrowdingDistanceComparator());
                    for (Chromosome c : front) {
                        if (newPopulation.size() < popSize) {
                            newPopulation.add(c);
                            continue;
                        }
                        break;
                    }
                    break;
                }
            }

            population = newPopulation;
            generationCount++;
        }

        System.out.println(parentsAndChidren.size());
        DataGenerator dg = new DataGenerator();
        dg.drawSegments(population.get(0).segments);
        dg.drawTrace(population.get(0), true);
        dg.drawTrace(population.get(0), false);
//        mutateChangePixelSegment(population.get(0));

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
                return new Chromosome(mst, mst.fuckersVisited.size(), 5, 10, new double[]{0.5,0.5});
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
        Fi.remove(Fi.size()-1);
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
            ArrayList<Chromosome> overallDeviation = new ArrayList<>(front);
            ArrayList<Chromosome> edgeValue = new ArrayList<>(front);
            overallDeviation.sort(new OverallDeviationComparator());
            edgeValue.sort(new EdgeValueComparator());
            overallDeviation.get(0).crowdingDistance = Double.MAX_VALUE;
            overallDeviation.get(front.size()-1).crowdingDistance = Double.MAX_VALUE;
            edgeValue.get(0).crowdingDistance = Double.MAX_VALUE;
            edgeValue.get(front.size()-1).crowdingDistance = Double.MAX_VALUE;
            double minDeviation = overallDeviation.get(0).overallDeviation;
            double maxDeviation = overallDeviation.get(front.size()-1).overallDeviation;
            double minEdgeValue = edgeValue.get(front.size()-1).edgeValue;
            double maxEdgeValue = edgeValue.get(0).edgeValue;

            if(front.size() > 2){
                for(int i = 1; i <front.size()-1; i++){
                    overallDeviation.get(i).crowdingDistance = Math.abs((overallDeviation.get(i-1).overallDeviation - overallDeviation.get(i+1).overallDeviation)/(maxDeviation - minDeviation)) + Math.abs((overallDeviation.get(i-1).edgeValue - overallDeviation.get(i+1).edgeValue)/(maxEdgeValue - minEdgeValue));
                }
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
                if(p1.crowdingDistance > p2.crowdingDistance){
                    parents.add(p1);
                }
                else if(p2.crowdingDistance < p1.crowdingDistance){
                    parents.add(p2);
                }
                else{
                    if (r.nextDouble() > 0.5 ? parents.add(p1) : parents.add(p2));
                }
            }
        }
        return parents;
    }

    private Chromosome crossOver(Chromosome parent1, Chromosome parent2) {
        Chromosome temp = new Chromosome(parent1.numberOfPixels, parent1.minSegments, parent1.maxSegments, parent1.segments);
        ArrayList<Pixel> pixels = new ArrayList<>();
        while(pixels.size() < 500){
            Segment s = parent2.segments.get(r.nextInt(parent2.segments.size()));
            Pixel p = s.pixels.get(r.nextInt(s.pixels.size()));
            if(!pixels.contains(p)){
                pixels.add(p);
            }
        }

        Segment segment = temp.segments.get(r.nextInt(temp.segments.size()));
        for(Pixel pixel: pixels){
            Pixel p = temp.coordinateToPixel.get(Arrays.toString(pixel.coordinates));
            p.segment.pixels.remove(p);
            p.segment = segment;
            segment.addPixel(p);
        }
        temp.findEdgePixels();
        temp.calculateOverallDeviation();
        temp.calculateEdgeValue();
        temp.calculateFitness();


        return temp;
    }


    public void mutateChangePixelSegment(Chromosome c){
        Pixel pixel = c.edges.get(r.nextInt(c.edges.size()));
        Segment pixelSegment = pixel.segment;
        Segment bestSegment = null;
        double bestFitness = Double.MAX_VALUE;
        pixelSegment.pixels.remove(pixel);
        for(Segment s : c.segments){
            if(! s.equals(pixelSegment)){
                s.addAllPixels(new ArrayList<Pixel>(Arrays.asList(pixel)));
                c.calculateEdgeValue();
                c.calculateOverallDeviation();
                double tempFitness = c.calculateFitness();
                if(tempFitness < bestFitness && tempFitness < c.getFitness()){
                    bestFitness = tempFitness;
                    bestSegment = s;
                }
                s.pixels.remove(pixel);
            }
        }
        if(bestSegment != null){
            bestSegment.addPixel(pixel);
        }
        else{
            pixelSegment.addPixel(pixel);
        }
        c.calculateEdgeValue();
        c.calculateOverallDeviation();
        c.fitness = c.calculateFitness();
    }

    public static void main(String[] args) {
        GA g = new GA();
        g.run(3, 2, 1);
    }

}
