
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Hanif
 */
public class GooglePageRank {

    private static int iteration;
    private static int initialValue;
    private static String fileName;
    
    private static int vertices, edges;
    private static int[][] graph;
    private static int[] outgoingLink;
    
    private static double[] pagerank;
    private static final double DAMPING = 0.85;
    private static final double ERRORRATE = 0.0001;
    
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Type this: java GooglePageRank iterations initialValue fileName");
            return;
        }
        
        // get arguments
        iteration = Integer.parseInt(args[0]);
        initialValue = Integer.parseInt(args[1]);
        fileName = args[2];
        
        if (iteration < 0) {
            System.out.println("Please enter valid iteration number");
            return;
        }
        
        // check if initial value typed correctly
        if (initialValue < -2 && initialValue > 1) {
            System.out.println("Please enter initial value (-2, -1, 0, or 1)");
            return;
        }
        
        proceed();        
    }
    
    private static void proceed() {
        boolean getInputSuccessful = scanInputFromFile();
        if (!getInputSuccessful) {
            System.out.println("File not found!");
        }
             
        if (vertices>10) {
            verticesMoreThan10();
            return;
        }
        
        initRanks(); 
        
        printInitialPageRank();
        if (iteration == 0) {
            convergence(false);
        }
        else {
            runIteration();
        }
    }
    
    private static void runIteration() {
        double temp[] = new double[vertices];
        for (int i=0; i<iteration; i++) {
            
            // initial value for temporary pagerank
            for (int j=0; j<vertices; j++) {
                temp[j] = 0;
            }
            
            // loop graph
            for (int j=0; j<vertices; j++) {
                for (int k=0; k<vertices; k++) {
                    if (graph[k][j] == 1) {
                        temp[j] += pagerank[k]/outgoingLink[k];
                    }
                }
            }    
            
            System.out.print("Iter    : " + (i+1));
            for(int j=0; j<vertices; j++) {
              pagerank[j] = DAMPING*temp[j] + (1-DAMPING)/vertices;
              System.out.printf(" :P[" + j + "]=%.6f",Math.round(pagerank[j]*1000000.0)/1000000.0);
            }
            System.out.println();
        }
    }
    
    private static boolean isMoreThanErrorRate(double previous[]) {
        for (int i=0; i<vertices; i++) {
            
            if (Math.abs(pagerank[i]-previous[i]) > ERRORRATE) {
                return false;
            }
        }
        return true;
    }
    
    private static void convergence(boolean isVerticesMoreThan10) {
        double temp[] = new double[vertices];
                
        int i=0;
        while(true) {
            // initial value for temporary pagerank
            for (int j=0; j<vertices; j++) {
                temp[j] = 0;
            }
            
            // loop graph
            for (int j=0; j<vertices; j++) {
                for (int k=0; k<vertices; k++) {
                    if (graph[k][j] == 1) {
                        temp[j] += pagerank[k]/outgoingLink[k];
                    }
                }
            }  
            
            double[] previouspagerank = new double[vertices];
            System.arraycopy(pagerank, 0, previouspagerank, 0, vertices);
            

            if (!isVerticesMoreThan10) System.out.print("Iter    : " + (i+1));
                

            for(int j=0; j<vertices; j++) {
              pagerank[j] = DAMPING*temp[j] + (1-DAMPING)/vertices;
              if (!isVerticesMoreThan10)
                  System.out.printf(" :P[" + j + "]=%.6f",Math.round(pagerank[j]*1000000.0)/1000000.0);
            }
            if (!isVerticesMoreThan10) System.out.println();
            
            
            if (isMoreThanErrorRate(previouspagerank)){
                if (isVerticesMoreThan10) {
                    System.out.println("Iter    : " + (i+1));
                    for(int j=0; j<vertices; j++) {
                      pagerank[j] = DAMPING*temp[j] + (1-DAMPING)/vertices;
                      System.out.printf("P[ " + j + "] = %.6f",Math.round(pagerank[j]*1000000.0)/1000000.0);
                      System.out.println();
                    }
                }
                break;
            }
            
            i++;
        }        
    }
        
    private static void printInitialPageRank() {
        System.out.print("Base    : 0");
        for(int i=0; i<vertices; i++) {
            System.out.printf(" :P[" + i + "]=%.6f",Math.round(pagerank[i]*1000000.0)/1000000.0);
        }
        System.out.println();
    }
    
    private static void verticesMoreThan10() {
        iteration = 0;
        initialValue = -1;
        initRanks();
        convergence(true);
    }
    
    private static void initRanks() {
        pagerank = new double[vertices];
        switch(initialValue) {
            case -2 :
                for (int i=0; i<vertices; i++) {
                    pagerank[i] = 1.0/Math.sqrt(vertices);
                }
                break;
            case -1 :
                for (int i=0; i<vertices; i++) {
                    pagerank[i] = 1.0/vertices;
                }
                break;
            case 0:
                for (int i=0; i<vertices; i++) {
                    pagerank[i] = 0.0;
                }
                break;
            case 1:
                for (int i=0; i<vertices; i++) {
                    pagerank[i] = 1.0;
                }
                break;
        }
    }
    
    private static boolean scanInputFromFile() {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            vertices = scanner.nextInt();
            edges = scanner.nextInt();
                        
            initGraph();
            while(scanner.hasNextInt()) {
                graph[scanner.nextInt()][scanner.nextInt()] = 1;
            }
            calculateOutgoingLink();
            
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GooglePageRank.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return false;
    }
    
    private static void calculateOutgoingLink() {
        outgoingLink = new int[vertices];
        for (int i=0; i<vertices; i++) {
            outgoingLink[i] = 0;
            for (int j=0; j<vertices; j++) {
                outgoingLink[i] += graph[i][j];
            }
        }
    }
    
    private static void initGraph () {
        graph = new int[vertices][vertices];
        for (int i=0; i<vertices; i++) {
            for (int j=0; j<vertices; j++) {
                graph[i][j] = 0;
            }
        }
    }
}
