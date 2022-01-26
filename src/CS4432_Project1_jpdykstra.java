import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CS4432_Project1_jpdykstra {

    public static void main(String[] args) throws IOException{
        BufferPool pool = new BufferPool(Integer.parseInt(args[0]));
        System.out.println("Please input a command (GET, SET, PIN, UNPIN, HELP, EXIT)");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        boolean active = true;
        while(active){
            // parse input
            String line = reader.readLine();
            String[] input = new String[3];
            int firstSpace = line.indexOf(" ");

            if(firstSpace == -1) input[0] = line;
            else {
                input[0] = line.substring(0, firstSpace);
                input[1] = line.substring(firstSpace + 1);
            }

            // parse commands
            switch(input[0]){
                case "GET":
                    // get recordID
                    pool.get(Integer.parseInt(input[1]));
                    break;
                case "SET":
                    // separate next two inputs into recordID and record
                    int secondSpace = line.indexOf(" ", firstSpace + 1);
                    input[1] = line.substring(firstSpace + 1, secondSpace);
                    input[2] = line.substring(secondSpace + 2, line.length() - 1);
                    pool.set(Integer.parseInt(input[1]), input[2].toCharArray());
                    break;
                case"PIN":
                    // pin blockID
                    pool.pin(Integer.parseInt(input[1]));
                    break;
                case "UNPIN":
                    // unpin blockID
                    pool.unpin(Integer.parseInt(input[1]));
                    break;
                case "HELP":
                    System.out.println("List of commands:");
                    System.out.println("GET <recordID>");
                    System.out.println("SET <recordID> <40 byte string>");
                    System.out.println("PIN <blockID>");
                    System.out.println("UNPIN <blockID>");
                    System.out.println("QUIT");
                    break;
                case "QUIT":
                    active = false;
                    break;
                default:
                    System.out.println("Pretty sure you misspelled something... Command wasn't recognized :)");
            }

        }
    }
}
