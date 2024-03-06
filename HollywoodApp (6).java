import java.util.Vector;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;
import java.util.LinkedList;
import javafoundations.AdjListsGraph; 
import javafoundations.Graph;
import javafoundations.LinkedQueue;
import javafoundations.exceptions.*;

/**
 * HollywoodApp class parses through a file of movie information. 
 * It first creates a Hashtable with the key being each movie and actor
 * in the file, and the value being Vectors including all actors in 
 * the movie and all movies that the actor starred in, respectively. 
 * It also runs a Bechdel Test, which will calculate if each movie has at least 48% 
 * female cast. 
 *
 * @author Sophie Hwang, Nyslai Bolanos, Ayesha Tariq, help from Angel, Becky, Moji, Smaranda, Hana, Jiarui 
 * @version (4/20/2023)
 */
public class HollywoodApp
{
    private AdjListsGraph<String> g;

    private Hashtable<String, Vector<String>> castGender;
    //keys are movie names, corresponding vector contains genders of each actor
    //that appears in the movie. Used for Bechdel Test.
    private Vector<String> movies;
    //vector of all movies for later parsing
    private Vector<String> actors;
    //vector for all actos for later parsing 

    private String fName; 
    final int num;  

    /**
     * Constructor for objects of class HollywoodApp
     * Creates the movieActor Hashtable, which is used for creating the TGF file
     * 
     * @param fName the file name of data
     */
    public HollywoodApp(String fName)
    {
        actors = new Vector<String>();
        movies = new Vector<String>();
        g = AdjListsGraphFromFile(fName); //creates graph object
        num = 48;  // final variable 
        this.fName = fName; 
        this.castGender = new Hashtable<String,Vector<String>>();
        readCastandMovies(); //fills castGender, actors and movies 
    }

    /**
     * Scans the file and creates the AdjListsGraph object from the parsed 
     * information. 
     * 
     * @param fName filename to read from
     */
    public AdjListsGraph<String> AdjListsGraphFromFile(String fName) { // creates the graph
        g = new AdjListsGraph<String>();

        try{
            Scanner scan = new Scanner(new File(fName)).useDelimiter(","); 
            scan.nextLine();//skipping header

            while (scan.hasNextLine()){ 
                String movieName = scan.next().replaceAll("\"", ""); // reads the movieName
                //System.out.println(movieName);
                String actorName = scan.next().replaceAll("\"", ""); // reads the actorName 
                //System.out.println(actorName);

                g.addVertex(movieName); // adds movieName as a vertex
                g.addVertex(actorName); // adds actorName as a vertex

                g.addArc(movieName, actorName);
                g.addArc(actorName, movieName);

                scan.nextLine();
            }
            scan.close();
        }
        catch(IOException ex){
            System.out.println(ex);
        }
        return g; 
    }

    /**
     * this reads the file and fills the hashtable castGender, Vector actors and
     * Vector movies for later parsing. 
     */
    public void readCastandMovies(){
        try{
            Scanner scan = new Scanner(new File(fName)).useDelimiter(",|\\n"); 
            scan.nextLine();//skipping header
            while (scan.hasNextLine()){
                String movieName = scan.next().replaceAll("\"", "");
                String actorName = scan.next().replaceAll("\"", "");
                scan.next(); scan.next(); scan.next();//skipping to gender
                String gender = scan.next().replaceAll("\"", "");

                if (castGender.containsKey(movieName)){ //if moviename already in castGender
                    castGender.get(movieName).add(gender);
                }
                else{//if moviename not in castGender, add new key and add movieName to movies
                    castGender.put(movieName, new Vector<String>());
                    castGender.get(movieName).add(gender);
                    movies.add(movieName);
                }
                if (!actors.contains(actorName))//if actorName not in actors, add
                    actors.add(actorName); // adds actor
                scan.nextLine();
            }
            scan.close();
        }catch(IOException ex){
            System.out.println(ex);
        }
    }

    /**
     * Runs the Bechdel Test using castGender and movies. This method will parse
     * through the file, add all actors' genders for each movie into the
     * Hashtable, and also add each movie into the movies Vector.
     * 
     * Parsing through castGender, we will get the number of females in each movie
     * and divide it by the total number of actors. We will not include "unknown"
     * genders in the femaleCount, since they are ambiguous. They will be included
     * in the total number. 
     * 
     */
    public void bechdelTest(){

        //File file = new File("bechdelProject_testing.txt");
        //PrintWriter writer = new PrintWriter(file);
        for (int i = 0; i < movies.size(); i++){//loops through each movie
            String movie = movies.get(i);
            Vector<String> genders = castGender.get(movie);
            //gets vector of genders in the movie

            double total = genders.size();
            double femaleCount = 0;
            double genderPercentage = 0;

            for (int j = 0; j < genders.size(); j++){//gets each gender
                if (genders.get(j).equals("Female")){
                    femaleCount += 1;
                }

            }
            genderPercentage = femaleCount/total*100;

            System.out.println("\n" + movie + ": " + genderPercentage + "%");
            if (genderPercentage > num)
                System.out.println(movie + " passes the Bechdel Test.");
            else
                System.out.println(movie + " does not pass the Bechdel Test.");
            //writer.println(movie + ": " + genderPercentage);

        }
    }

    /**
     * given a String input actorName, returns a LinkedList of the movies the 
     * actor has starred in
     * 
     * @param takes in a actorName 
     */
    public LinkedList<String> givenActor(String actorName){ // parameter is an actorName
        LinkedList<String> movieLists = new LinkedList<String>(); // creates a movieList to store the list of movies given the actor name

        for(int i = 0; i < movies.size(); i++){ // iterates through the movie size
            if(g.isEdge(actorName, movies.get(i))){ // if it there is an edge btwn an actor and a movie then 
                if(!movieLists.contains(movies.get(i)))
                    movieLists.add(movies.get(i)); // adds the movieName into the list
            }
        }
        return movieLists; 
    }

    /**
     * given a String input movieName, returns a LinkedList of the actors 
     * that appear in the movie
     * 
     * @param takes in a movieName 
     */
    public LinkedList<String> givenMovie(String movieName){
        LinkedList <String> actorLists = new LinkedList<String>(); // creates an actorList to store the list of actors that are in the movie
     
        for(int i= 0; i < actors.size(); i++){
            if(g.isEdge(movieName,actors.get(i))){ // if it there is an edge btwn a movie and an actor 
                if(!actorLists.contains(actors.get(i)))
                    actorLists.add(actors.get(i)); // adds the actorName into the list
            }
        }
        return actorLists;  // return the LinkedLists
    }

    /**
     * This calculates the degree of separation between actor 1 and actor 2. 
     * For example, if actor 1 and actor 2 played in a movie together, 
     * the doS is 0. If there is 1 more actor in between, doS is 1, and so on. 
     * Utilizes the Breadth-First-Search algorithm
     * 
     * @param actor1, actor2 Strings
     */
    public int doS(String actor1, String actor2){
        LinkedList<String> first = new LinkedList<String>();//initial LinkedList
        first.add(actor1);//actor 1 is added to start the search

        LinkedQueue<LinkedList<String>> traversalQueue = new LinkedQueue<LinkedList<String>>();
        //traversalQueue keeps track of the paths in BFS
        traversalQueue.enqueue(first);
        Vector<String> visited = new Vector<String>(); //marks visited locations
        visited.add(actor1);//marks actor1 as visited
        LinkedList<String> finalPath = new LinkedList<String>();//the final desired path

        try{
            while (!visited.contains(actor2)){

                LinkedList<String> adjacent;//list of adjacent nodes
                LinkedList<String> nextPath = new LinkedList<String>();//new path being enqueued

                LinkedList<String> path = traversalQueue.dequeue(); // path dequeued

                String location = path.get(path.size()-1); // gets location to continue BFS

                if (movies.contains(location)){
                    adjacent = givenMovie(location);
                }
                else {
                    adjacent = givenActor(location);
                }

                for (int i = 0; i < adjacent.size(); i++){
                    String nextLocation = adjacent.get(i);

                    nextPath = (LinkedList) path.clone();//creates a clone of path
                    if (!visited.contains(nextLocation)){
                        nextPath.add(nextLocation);

                        traversalQueue.enqueue(nextPath);
                        visited.add(nextLocation);
                        if (nextLocation.equals(actor2)){
                            finalPath = (LinkedList) nextPath.clone();
                        }
                    }
                }
            }
        }catch(EmptyCollectionException ex){
            System.out.println();
        }

        return finalPath.size()/2 -1;
    }

    /**
     * Main method for the HollywoodApp Class
     */
    public static void main(String[] args){
        HollywoodApp firstG = new HollywoodApp("data/nextBechdel_castGender.txt");
        AdjListsGraph<String> graph = firstG.AdjListsGraphFromFile("data/nextBechdel_castGender.txt");
        
        firstG.g.saveTGF("testing1.tgf");

        firstG.bechdelTest();

        LinkedList<String> movieList = firstG.givenActor("Jennifer Lawrence");
        System.out.println("\nMovies acted by Jennifer Lawrence: " + movieList);

        LinkedList<String> actorList = firstG.givenMovie("The Jungle Book");
        System.out.println("\nActors in The Jungle Book: "  + actorList);

        int degree1 = firstG.doS("Megan Fox", "Tyler Perry");
        System.out.println("\nCalculating degree of separation between Megan Fox and Tyler Perry:");
        System.out.println("Degree of separation: " + degree1);

        int degree2 = firstG.doS("Nick Arapoglou", "Tyler Perry");
        System.out.println("\nCalculating degree of separation between Nick Arapoglou and Tyler Perry:");
        System.out.println("Degree of separation: " + degree2);

    }
}
