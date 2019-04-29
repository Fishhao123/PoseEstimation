package com.fishhao.utils;

import com.fishhao.controller.WebSocketController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseData {
    public static void main( String[] args )
    {
        WebSocketController wsc = new WebSocketController();
        try{
            FileReader fileReader = new FileReader("F:/UnityProjects/pos_sample1.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            File file = new File("F:/UnityProjects/example.txt");

            if(!file.exists()){
                file.createNewFile();
            }

//            FileWriter fileWritter = new FileWriter(file.getName(),true);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            String data = null;
            while ((data = bufferedReader.readLine()) != null){
                data = data.replace(",","");
                String[] dataString = data.split(" ");
                ArrayList list = new ArrayList();
                for(int i = 0; i < dataString.length; i++){
                    if(i%4 != 0){
                        bw.write(dataString[i] + ", ");

//                        fileWritter.write(dataString[i] + ", ");
//                        fileWritter.flush();
//                        System.out.println(dataString[i]);
//                        list.add(dataString[i]);
                    }
                }
                bw.write("\r\n");
            }
            bufferedReader.close();
            fileReader.close();
            bw.close();
//            fileWritter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
