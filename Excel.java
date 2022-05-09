import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Excel {
    public Excel() {

    }

    public ArrayList<String[]> ReadExcel(String path) {

        ArrayList<String[]> rawData = new ArrayList<String[]>();
        File file = new File(path);
        try {

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            List<String[]> allData = csvReader.readAll();

            rawData.addAll(allData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String[]> outFinale= new ArrayList<>();


        //System.out.println(out.get(1)[0]);
        for(int i=0;i<rawData.size();i++) {
            String[] temp = rawData.get(i);
            String temp1="";
            for(String cell : temp) {
                temp1+=cell;
            }
            outFinale.add(temp1.split(";"));
        }

        /*
        for(int i=0;i<outFinale.size();i++){
            for ( String cell: outFinale.get(i) ){
                System.out.println(cell);
            }
            System.out.println("___________________________");
        }

         */
        return outFinale;
    }
}
