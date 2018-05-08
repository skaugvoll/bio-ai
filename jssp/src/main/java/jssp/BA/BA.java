package jssp.BA;

import jssp.Job;
import jssp.Node;
import jssp.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;


public class BA {
    private Bee[] bees;
    private final int total;
    private final Node root;
    private final ArrayList<Node> nodes = new ArrayList<>();
    private final Comparator<BeeSolution> makespanComparator;
    private BeeSolution bestGlobalBeeSolution = null;
    private final Random random;

    public BA(Job[] jobs, int num_machines, int num_jobs, int num_bees){
        bees = new Bee[num_bees];
        this.total = num_machines * num_jobs;
        this.makespanComparator = new MakespanComparator();
        this.random = new Random();

        root = new Node(-1, -1, -1);
        nodes.add(root);
        root.edges = new Node[num_jobs];

        for (int i = 0; i < num_jobs; i ++) {
            final int machineNumber = jobs[i].operations[0][0];
            final int timeRequired = jobs[i].operations[0][1];
            final int jobNumber = jobs[i].job_number;
            final Node neighbour = new Node(machineNumber, jobNumber, timeRequired);
            nodes.add(neighbour);
            root.edges[i] = neighbour;
        }

        for(int i = 0; i < num_bees; i++){
            bees[i] = new Bee(jobs, num_jobs, num_machines, total);
        }
    }

    public Solution solve(int num_iterations) {

        //Initial population
        ArrayList<BeeSolution> solutionRegion = new ArrayList<BeeSolution>();
        for(int i=0; i < bees.length;i++){
            solutionRegion.add(bees[i].findSolution(null,0, nodes));
        }
        solutionRegion.sort(new MakespanComparator());

        bestGlobalBeeSolution = solutionRegion.get(0);

        //iterations
        for(int i=0; i < num_iterations;i++){

            double bestSiteCount =  0.2 * solutionRegion.size();  //number of best sites are 40% of the population
            double eliteSiteCount =  0.1 * solutionRegion.size();    //number of best sites are 10% of the population
            double bestSiteBees = 0.8 * bees.length;
            double eliteSiteBees = 0.6 * bestSiteBees;
            /*Let bees do bee stuff*/
            for (int j=0; j<solutionRegion.size();j++){
                if(j <= eliteSiteCount){
                    solutionRegion.get(j).neighbourhood = (int)(total * 0.1);
                }
                else if(j > eliteSiteCount  && j<= eliteSiteCount + bestSiteCount){
                    solutionRegion.get(j).neighbourhood = (int)(total * 0.2);
                }else{
                    solutionRegion.get(j).neighbourhood = (int)(total * 0.5);
                }
            }

            ArrayList<BeeSolution> solutions = new ArrayList<>();

            for (int j=0;j < bees.length;j++){
                BeeSolution region = solutionRegion.get(j);
                if (j > bestSiteBees){
                    double p = Math.random();
                    if(p > 0.5){
                        solutions.add(bees[j].findSolution(null,0, nodes));
                        //System.out.println("sadfasf");
                    }else{
                        BeeSolution randomBeeSolution = solutionRegion.get(random.nextInt(solutionRegion.size()));
                        solutions.add(bees[j].findSolution(randomBeeSolution,randomBeeSolution.neighbourhood, nodes));
                    }
                }
                else{
                    solutions.add(bees[j].findSolution(region,random.nextInt(region.neighbourhood)+1, nodes));
                }
            }

            solutionRegion = solutions;
            //sort and add to graph
            solutionRegion.sort(makespanComparator);
            if (bestGlobalBeeSolution.makespan >= solutionRegion.get(0).makespan) {
                bestGlobalBeeSolution = solutionRegion.get(0);
            }
        }

        System.out.println("#nodes : " + this.nodes.size());
        return bestGlobalBeeSolution.solution;
    }

    }