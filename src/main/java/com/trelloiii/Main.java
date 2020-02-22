package com.trelloiii;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello, please type path to txt file with urls. Note: urls need to be separated by ;");
        Scanner in=new Scanner(System.in);
        InputReader inputReader=new InputReader("/home/trelloiii/Рабочий стол/JavaProjects/http_downloader/src/main/resources/a.txt");
        System.out.println("OK, now type output dir for files");
        String downloadPath=in.next();
        System.out.println("If you want to limit download speed type the speed number in KB, if no type 0");
        Downloader downloader=new Downloader(inputReader.readFile(),downloadPath,in.nextInt()*1024);
        System.out.println("Type threads count for downloading");
        downloader.download(in.nextInt());
        System.out.println("Download is over!");

    }
}
