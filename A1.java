import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class A1
{

    private static void addtoMap(Map<String, ArrayList<String>> map, String actor, String movie)
    {
        if (!map.containsKey(actor))
        {
            ArrayList<String> movies = new ArrayList<>();
            movies.add(movie);
            map.put(actor, movies);
        }
        else
        {
            map.get(actor).add(movie);
        }
    }

    private static void printMap(Map<String, ArrayList<String>> map)
    {
        for (String actor: map.keySet())
        {
            String key = actor.toString();
            ArrayList<String> value = map.get(actor);
            System.out.println(actor+" ");
            System.out.println("");
            for (String movie : value)
            {
                System.out.println(movie);
            }
            System.out.println("");
        }
    }
    private static int getDistance(String A, String B) //distance between two string
    {
        int m = A.length();
        int n = B.length();

        int[][] T = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++)
        {
            T[i][0] = i;
        }
        for (int j = 1; j <= n; j++)
        {
            T[0][j] = j;
        }

        int cost;
        for (int i = 1; i <= m; i++)
        {
            for (int j = 1; j <= n; j++)
            {
                cost = A.charAt(i - 1) == B.charAt(j - 1) ? 0: 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }
        return T[m][n];
    }

    private static double findSimilarity(String a, String b) { //compute the similarity
        double maxLength = Double.max(a.length(), b.length());
        if (maxLength > 0) //save time
        {
            return (maxLength - getDistance(a, b)) / maxLength;
        }
        return 1.0;
    }
    public static void main(String[] arg) throws IOException
    {
        Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

        String line = "";
        String splitBy = ",";

        BufferedReader br = new BufferedReader(new FileReader("tmdb_5000_credits.csv")); //read the file

        while ((line = br.readLine()) != null)   //returns a Boolean value
        {
            String[] actor = line.split(splitBy);    // use comma as separator

            int count=0;

            while(true)
            { // infinity loop
                if (actor[count].contains("department") || count== actor.length-1)
                { //base case
                    break;
                }
                if (actor[count].contains("\"\"name\"\""))
                { //when find the actor name position
                    StringBuilder character = new StringBuilder(actor[count-4]);
                    if(!character.toString().contains("character"))
                    { //if character has one "," in it
                        character = new StringBuilder(actor[count-5]);
                        character.append(actor[count-4]);
                        if(!character.toString().contains("character"))
                        { // if character has two "," in it
                            character = new StringBuilder(actor[count-5]);
                            character.append(actor[count-4]);
                            character.append(actor[count-3]);

                        }
                    }
                    character.delete(0,18); //delete ( ""character"": "")
                    character.delete(character.length()-2,character.length()); //delete ("")

                    StringBuilder name = new StringBuilder(actor[count]);
                    name.delete(0,13); // delete ( ""name"": "")

                    if (name.charAt(name.length()-1)==('}'))
                    { //when the string end with "}"
                        name.delete(name.length()-3,name.length());//delete (""}")
                    }
                    else
                    { //when the string end with ""
                        name.delete(name.length()-2,name.length()); //delete("")
                    }
                    StringBuilder overall = new StringBuilder(); //will be the movie name as character
                    overall.append(actor[1]);
                    overall.append(" as ");
                    overall.append(character.toString());
                    addtoMap(map, name.toString(), overall.toString()); //add to the hash map
                }
                count++;
            }
        }
        //printMap(map);
        //menu starts

        Scanner sc = new Scanner(System.in);

        String user_input="";
        ArrayList<String> results = new ArrayList<>();

        System.out.println("Welcome to the Movie Wall!");
        while(true)
        {

            System.out.println("Enter the name of an actor (or \"EXIT\" to quit):");

            user_input = sc.nextLine(); //user input
            if(user_input.equals("EXIT")||user_input.equals("exit")) //if user enter exit in any kind
            {
                break;
            }

            if(map.containsKey(user_input))
            {
                results=map.get(user_input);
                System.out.println("Actor: "+user_input);
                for (String result : results) //print result
                {
                    System.out.print("* Movie: ");
                    System.out.println(result);
                }
            }
            else
            {
                double max=0;
                String storage="";
                for (String actor: map.keySet())
                { //find the most similar string to user_input -> storage
                    double similarity = findSimilarity(actor,user_input);
                    if(similarity>max)
                    {
                        storage=actor;
                        max=similarity;
                    }
                }
                System.out.println("No such actor. Did you mean \"" + storage + "\" (Y/N):");
                String new_input= sc.nextLine();
                if(new_input.equals("Y")||new_input.equals("y"))
                {
                    results=map.get(storage);
                    System.out.println("Actor: "+storage);
                    for (String result : results) //print result
                    {
                        System.out.print("* Movie: ");
                        System.out.println(result);
                    }
                }
                else
                {
                    System.out.println("Try again please");
                }
            }
        }
        System.out.println("Thanks for using the Movie Wall!");
    }
}
