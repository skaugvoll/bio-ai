package moea;

import com.rits.cloning.Cloner;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;

public class GA {

    DataGenerator dg = new DataGenerator();
    Prims prim = new Prims();
    private ExecutorService pool;
    Random r = new Random();
    private Pixel[][] pixels = {};

    int minSegments;
    int maxSegments;
    double[] weights;
    int colorTeta;

    ArrayList<ArrayList<Chromosome>> populationFronts = new ArrayList<ArrayList<Chromosome>>();

    public void run(boolean testing, int imgNbr, int popSize, int maxGeneration, int minSegments, int maxSegments, double[] weights, int colorTeta, boolean nsga) {
        this.minSegments = minSegments;
        this.maxSegments = maxSegments;
        this.weights = weights;
        this.colorTeta = colorTeta;


        pixels = dg.readImage(String.valueOf(imgNbr));


        ArrayList<MST> MSTs = new ArrayList<>();
        threadGenerateMST(popSize, MSTs);

        ArrayList<Chromosome> population = new ArrayList<>();
        threadGenerateIndividuals(MSTs, population, this.minSegments, this.maxSegments, this.weights, this.colorTeta);

        if(testing){
            DataGenerator dg = new DataGenerator();
            dg.drawSegments(population.get(0).segments);
            dg.drawTrace(population.get(0), true, 1);
        }
        else{
            // Running weighted sum
            if(nsga) {
                populationFronts = fastNonDominatedSort(population);

                ArrayList<Chromosome> parentsAndChidren = new ArrayList<>();
                int generationCount = 0;
                while (generationCount < maxGeneration) {
                    parentsAndChidren.addAll(population);
                    while (parentsAndChidren.size() < 2 * popSize) {
                        ArrayList<Chromosome> parents = nsgaTournamentSelection(population);
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
                ArrayList<Chromosome> chromosomesToDraw = new ArrayList<>();
                for(Chromosome c: population){
                    int i = 0;
                    if(i == 5 || c.rank > 1){
                        break;
                    }
                    else {
                        chromosomesToDraw.add(c);
                    }
                    i++;
                }
                drawPictures(chromosomesToDraw);
            }
            // Running weighted sum
            else{
                ArrayList<Chromosome> parentsAndChidren = new ArrayList<>();
                int generationCount = 0;
                while (generationCount < maxGeneration) {
                    parentsAndChidren.addAll(population);
                    while (parentsAndChidren.size() < 2 * popSize) {
                        ArrayList<Chromosome> parents = wsTournamentSelection(population);
                        parentsAndChidren.add(crossOver(parents.get(0), parents.get(1)));
                        parentsAndChidren.add(crossOver(parents.get(1), parents.get(0)));
                    }

                    ArrayList<Chromosome> newPopulation = new ArrayList<>();
                    ArrayList<Chromosome> temp = new ArrayList<>(parentsAndChidren);
                    temp.sort(new FitnessComparator());

                    int index = 0;
                    while(index < popSize){
                        int rank = temp.size();
                        int totalScore = 0;
                        for(int i= temp.size(); i>0;i--){
                            totalScore += i;
                        }
                        Double cumulativeProbability = 0.0;
                        Double p = Math.random();
                        int listIndex = 0;
                        while (!temp.isEmpty()){

                            Chromosome c = temp.get(listIndex);
                            cumulativeProbability += (double) rank/totalScore;
                            if(p <= cumulativeProbability){
                                newPopulation.add(temp.remove(listIndex));
                                index ++;
                                break;
                            }
                            listIndex ++;
                            rank--;

                        }
                    }
                    population = newPopulation;
                    generationCount++;
                }
                population.sort(new FitnessComparator());
                ArrayList<Chromosome> chromosomesToDraw = new ArrayList<>();
                for(int i = 0; i < 5; i++){
                    chromosomesToDraw.add(population.get(i));
                }
                drawPictures(chromosomesToDraw);
            }
        }

//        mutateChangePixelSegment(population.get(0));

        System.out.println("populationSize: " + MSTs.size());
        System.out.println("Score bitch: " + population.get(0).getFitness());
    }

    public void drawPictures(ArrayList<Chromosome> chromosomes){
        for(int i = 0; i < chromosomes.size(); i++){
            dg.drawTrace(chromosomes.get(i), false, i);
        }
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


    private void threadGenerateIndividuals(ArrayList<MST> MSTs, ArrayList<Chromosome> population, int minSegments, int maxSegments, double[] weights, int colorTeta) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Callable<Chromosome>> callableTasks = new ArrayList<>();
        for (int t = 0; t < MSTs.size(); t++) {
            int finalT = t;
            callableTasks.add(() -> {
                MST mst = MSTs.get(finalT);
                return new Chromosome(mst, mst.fuckersVisited.size(), minSegments, maxSegments, weights, colorTeta);
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

    private ArrayList<Chromosome> nsgaTournamentSelection(ArrayList<Chromosome> population) {
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

    private ArrayList<Chromosome> wsTournamentSelection(ArrayList<Chromosome> population) {
        ArrayList<Chromosome> parents = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            Chromosome p1 = population.get(r.nextInt(population.size()));
            Chromosome p2 = population.get(r.nextInt(population.size()));
            if(p1.fitness < p2.fitness){
                parents.add(p1);
            }
            else if(p2.fitness < p1.fitness) {
                parents.add(p2);
            }else {
                if (r.nextDouble() < 0.5){
                    parents.add(p1);
                }
                else{
                    parents.add(p2);
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

        this.mutateNijaturtles(temp);
        this.mergeClosestSegments(temp);

        temp.findEdgePixels();
        temp.calculateOverallDeviation();
        temp.calculateEdgeValue();
        temp.fitness = temp.calculateFitness();

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

    public void mergeClosestSegments(Chromosome c){
        if(c.segments.size() > minSegments){
            Segment s1 = null;
            Segment s2 = null;
            int bestDiff = 1000000000;
            int j = 1;
            for(Segment s: c.segments){
                for(int i = j; i < c.segments.size(); i++){
                    int diff = Math.abs((s.avgSegColors[0] - c.segments.get(i).avgSegColors[0]) + (s.avgSegColors[1] - c.segments.get(i).avgSegColors[1]) + (s.avgSegColors[2] - c.segments.get(i).avgSegColors[2]));
                    if(diff < bestDiff){
                        bestDiff = diff;
                        s1 = s;
                        s2 = c.segments.get(j);
                    }
                }
                j++;
            }
            s1.addAllPixels(s2.pixels);
            c.segments.remove(s2);
            c.findEdgePixels();
        }
    }

    public void mutateNijaturtles(Chromosome c){
//        for hver edge, sjekke hver nabo, sjekk hvilket segment naboene tilhører. sett denne pixelen til å tilhø®e segmentet flest naboer tilhører
        for(Pixel p : c.edges){
            HashMap<Segment, Integer> counter = new HashMap<>();
            Segment currentPixelSegment = p.segment;

            for(Edge edge : p.getNeighbours()){
                int value = counter.containsKey(edge.getNeighbourPixel().segment) ? counter.get(edge.getNeighbourPixel().segment) : 1;
                counter.put(edge.getNeighbourPixel().segment, value + 1);
            }

            Segment nearestSegment = Collections.max(counter.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();

            if(nearestSegment.equals(currentPixelSegment)){
                continue;
            }

            p.segment.pixels.remove(p);
            p.segment = nearestSegment;
            nearestSegment.addPixel(p);

        }

        c.findEdgePixels();

    }

    public static void main(String[] args) {
        GA g = new GA();
        g.run(false,3, 8, 2, 5, 8, new double[] {0.5,0.5}, 200, true);
    }

}
