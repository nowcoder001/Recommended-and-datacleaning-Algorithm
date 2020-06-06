package util;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Csv {
    public static  List<String> readCsvFile(String filePath) {
        File csv = new File(filePath);  // CSV文件路径
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        try {
            List<String> allString = new ArrayList<String>();
            while ((line = br.readLine()) != null)  //读取到的内容给line变量
            {
                everyLine = line;
                allString.add(everyLine);
            }
            return allString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static void writeCsvFile(String filePath,List<String> list) {
        try {
            File csv = new File(filePath);//CSV文件
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
            //新增一行数据
            for(String i :list)
            {

                bw.write(i);
                bw.newLine();
            }

            bw.close();
        } catch (FileNotFoundException e) {
            //捕获File对象生成时的异常
            e.printStackTrace();
        } catch (IOException e) {
            //捕获BufferedWriter对象关闭时的异常
            e.printStackTrace();
        }



    }
    public static void main(String[] args) {
//        String filePath = "src/main/resources/ml-25m/ratings.csv";
//        readCsvFile(filePath);
        String filePath = "src/main/resources/ml-25m/2.csv";
//        List<String> allString = new ArrayList<String>();
//        allString.add("1,2,3,4");
//        allString.add("1,2,3,4");
//        allString.add("1,2,3,4");

        List<String>  allString =  readCsvFile(filePath);
        System.out.println(allString);
    }
}
