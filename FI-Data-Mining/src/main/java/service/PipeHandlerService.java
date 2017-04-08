package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by shawn on 4/2/2017.
 */
public class PipeHandlerService {

    private File file;
    private String[] originalFileAttribute;
    private LinkedHashSet<Integer> hasPipes;
    private HashSet<String> pipedAttributes;
    private ArrayList<Object> newAttributes;

    public PipeHandlerService(File file){
        this.file = file;
        pipedAttributes = new HashSet<>();
    }

    public static void main(String[] args){
        try {
            File testFile = new File("Data/PreprocessedFile.csv");
            PipeHandlerService service = new PipeHandlerService(testFile);
            service.detectPipe();
            service.updateFile();
        } catch (FileNotFoundException e){

        }
    }

    /**
     * Used to find which attribute has been piped
     * @throws FileNotFoundException
     */
    public void detectPipe() throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        originalFileAttribute = scan.nextLine().split(",");
        hasPipes = new LinkedHashSet<>();
        String[] values;
        while(scan.hasNext()){
            values = scan.nextLine().split(",");
            for(int i = 0; i < values.length; i++){
                if(values[i].contains("|")){
                    hasPipes.add(i);
                }
            }
        }
        scan.close();

        buildAttributes();
    }

    public void buildAttributes() throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        scan.nextLine();
        String[] values;
        while(scan.hasNext()){
            values = scan.nextLine().split(",");
            for(Integer i : hasPipes){
                String[] temp = values[i].split("\\|");
                for(String s : temp) {
                    pipedAttributes.add(originalFileAttribute[i] + "." + s.split("-")[0].replace(" ","_"));
                }
            }
        }
        scan.close();
    }

    public void updateFile() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        // remove the piped attributes
        for(int i = 0; i < originalFileAttribute.length; i++){
            if(!hasPipes.contains(i)){
                sb.append(originalFileAttribute[i]);
                sb.append(",");
            }
        }

        // add the new attributes to the file
        for(String s : pipedAttributes){
            sb.append(s);
            sb.append(",");
        }

        sb.replace(sb.length() - 1, sb.length(),""); // remove the last comma

        // write the new file attributes
        PrintWriter writer = new PrintWriter(new File("Data/updatedPreprocessedFile"));
        writer.write(sb.toString());

        newAttributes = new ArrayList<>();

        for(String s : sb.toString().split(",")){
            newAttributes.add(s);
        }

        Scanner scan = new Scanner(file);
        scan.nextLine();
        String[] line;
        while(scan.hasNext()){
            line = scan.nextLine().split(",");
            for(int i = 0; i < line.length; i++) {
                String value = 
            }
        }
        writer.close();
    }

}
