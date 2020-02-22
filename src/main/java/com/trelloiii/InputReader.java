package com.trelloiii;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputReader {
    private String textInputPath;

    public InputReader(String textInputPath) {
        this.textInputPath = textInputPath;
    }

    public String[] readFile(){
        File inputFile=new File(this.textInputPath);
        StringBuilder sb=new StringBuilder();
        try(FileReader fileReader=new FileReader(inputFile)){
            int c;
            while((c=fileReader.read())!=-1){
                sb.append((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString().split(";");
    }
}
