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
    private ArrayList<String> newAttributes;
    private ArrayList<String[]> data;
    private ArrayList<ArrayList<String>> newData;

    public PipeHandlerService(File file){
        this.file = file;
        newAttributes = new ArrayList<>();
        pipedAttributes = new HashSet<>();
        data = new ArrayList<>();
        newData = new ArrayList<>();
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
            data.add(values);
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
        for(String[] str : data){
            for(Integer i : hasPipes){
                String[] temp = str[i].split("\\|");
                for(String s : temp) {
                    pipedAttributes.add(originalFileAttribute[i] + "." + s.split("-")[0].replace(" ","_"));
                }
            }
        }

        updateData();
    }

    public String[] splitValues(String str){
        return str.split("\\|");
    }


    public void updateData(){

        for(int i = 0; i < originalFileAttribute.length; i++){
            if(!hasPipes.contains(i)){
                newAttributes.add(originalFileAttribute[i]);
            }
        }

        for(String s : pipedAttributes){
            newAttributes.add(s);
        }

        for(String[] row : data){
            ArrayList<String> values = new ArrayList();
            for(int i = 0; i < row.length; i++){
                if(!hasPipes.contains(i)){
                    values.add(row[i]);
                }
            }

            for(int i = 0; i < row.length; i++){
                if(hasPipes.contains(i)){
                    if(row[i].contains("|")){
                        String[] splitValues = splitValues(row[i]);
                        values.addAll(Arrays.asList(splitValues));
                    }
                }
            }
            newData.add(values);
        }

    }

    public void updateFile() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        for(String s : newAttributes){
            sb.append(s);
            sb.append(",");
        }

        sb.delete(sb.length() - 1, sb.length());

        // write the new file attributes
        PrintWriter writer = new PrintWriter(new File("Data/updatedPreprocessedFile.csv"));

        writer.write(sb.toString());

        for(ArrayList<String> row : newData){
            sb = new StringBuilder();
            for(int i = 0; i < row.size(); i++){
                if(!hasPipes.contains(i)){
                    sb.append(row.get(i));
                    sb.append(",");
                }
            }

            for(int i = 0; i < row.size(); i++){
                if (hasPipes.contains(i)) {
                    for (String s : newAttributes) {
                        String value = row.get(i);
                        if (s.contains(value)) {
                            sb.append(row.get(i));
                            sb.append(",");
                        } else {
                            sb.append("null");
                            sb.append(",");
                        }
                    }
                }
            }
            writer.write(sb.toString());
            writer.write("\n");
            writer.flush();
        }

        sb.delete(sb.length() - 1, sb.length());

        writer.close();
    }

}
