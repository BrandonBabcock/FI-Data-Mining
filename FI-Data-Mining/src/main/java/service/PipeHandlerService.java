package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by shawn on 4/2/2017.
 */
public class PipeHandlerService {

    private File file;
    private boolean[] hasPipes;

    public PipeHandlerService(File file){
        this.file = file;
    }

    public static void main(String[] args){
        try {
            File testFile = new File("Data/PreprocessedFile.csv");
            PipeHandlerService service = new PipeHandlerService(testFile);
            service.detectPipe();
        } catch (FileNotFoundException e){

        }
    }

    /**
     * Looks at each row to detect the pipe
     * @throws FileNotFoundException
     */
    public void detectPipe() throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        int numAttributes = scan.nextLine().split(",").length;
        hasPipes = new boolean[numAttributes];
        String[] values;
        while(scan.hasNext()){
            values = scan.nextLine().split(",");
            for(int i = 0; i < values.length; i++){
                if(values[i].contains("|")){
                    hasPipes[i] = true;
                }
            }
        }
    }

    
}
