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

    public PipeHandlerService(File file){
        this.file = file;
        newAttributes = new ArrayList<>();
        pipedAttributes = new HashSet<>();
        data = new ArrayList<>();
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

                    pipedAttributes.add(originalFileAttribute[i] + "." + s.replace(" ","_").replace("'",""));
                }
            }
        }

        updateData();
    }



    public void updateData(){
        newAttributes = new ArrayList<>();
        for(int i = 0; i < originalFileAttribute.length; i++){
            if(!hasPipes.contains(i)){
                newAttributes.add(originalFileAttribute[i]);
            }
        }

        for(String s : pipedAttributes){
            newAttributes.add(s);
        }

        System.out.println(newAttributes.size());
    }

    public void updateFile() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        for(String s : newAttributes){
            sb.append(s.replace("-","_"));
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");

        // write the new file attributes
        PrintWriter writer = new PrintWriter(new File("Data/updatedPreprocessedFile.csv"));

        writer.write(sb.toString());


        for(String[] arr : data){
            sb = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                if(!hasPipes.contains(i)){
                    sb.append(arr[i]);
                    sb.append(",");
                }
            }


            for(int i = 0; i < newAttributes.size(); i++){
                boolean contained = false;
                String att = newAttributes.get(i);
                for(int j : hasPipes){
                    String[] values = arr[j].split("\\|");
                   for(String s : values){
                       if(att.contains(s)){
                           contained = true;
                       }
                   }
                }
                if(contained){
                    sb.append("true");
                    sb.append(",");
                } else {
                    sb.append("false");
                    sb.append(",");
                }
            }
            System.out.println(sb.toString().split(",").length);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
            writer.write(sb.toString());
        }

        sb.delete(sb.length() - 1, sb.length());
        writer.close();
    }
}


