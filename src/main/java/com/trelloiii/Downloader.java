package com.trelloiii;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Downloader {

    private List<String> urls;
    private String downloadDir;
    private int downLoadSpeedKB;

    public Downloader(String[] urls, String downloadDir,int downLoadSpeedKB) {
        this.urls = Arrays.asList(urls);
        this.downloadDir = downloadDir;
        this.downLoadSpeedKB=downLoadSpeedKB;
        File file=new File(downloadDir);
        if(file.exists()){
            if(!file.isDirectory()){
                throw new IllegalArgumentException("This path is not directory");
            }
        }
        else{
            file.mkdirs();
        }
    }
    public void download(int threadCounts) throws InterruptedException {
        if(threadCounts>=this.urls.size()){
            ExecutorService executorService= Executors.newFixedThreadPool(this.urls.size());
            for(int i=0;i<this.urls.size();i++){
                String [] list=new String[]{this.urls.get(i)};
                executorService.submit(new DownloaderThread(Arrays.asList(list),this.downloadDir,downLoadSpeedKB));

            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        }
        else{
//            int res=this.urls.length/threadCounts;
//            int ost=res*threadCounts;
//            int inLastThreadElements=this.urls.length-ost;
            ExecutorService executorService= Executors.newFixedThreadPool(threadCounts);
            //List<String[]> arrays=new ArrayList<>(threadCounts);
            HashMap<Integer,List<String>> arrays=new HashMap<>();
            for(int i=0;i<threadCounts;i++){
                arrays.put(i,new ArrayList<>());
            }
            //List<List<String>> arrays=new ArrayList<>(threadCounts);
            int l=this.urls.size()-1;
            int i=0;
            while (l>=0){
                if(i>threadCounts-1){
                    i=0;
                }
                arrays.get(i).add(this.urls.get(l));
                i++;
                l--;
            }

            for (Map.Entry<Integer,List<String>> entry:arrays.entrySet()){
                System.out.println(entry.getKey()+" : "+ Arrays.toString(entry.getValue().toArray()));
                executorService.submit(new DownloaderThread(entry.getValue(),this.downloadDir, downLoadSpeedKB));
               // new DownloaderThread(entry.getValue(),this.downloadDir).start();

            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        }
    }
}
class DownloaderThread extends Thread{
    private List<String> urls;
    private String downloadDir;
    private int downLoadSpeedKB;
    public DownloaderThread(List<String> urls, String downloadDir,int downLoadSpeedKB){
        this.urls = urls;
        this.downLoadSpeedKB=downLoadSpeedKB;
        this.downloadDir = downloadDir;
    }


    @Override
    public void run() {
        System.out.println("Thread "+Thread.currentThread().getId()+" starts");
        download();
    }

    private void download(){
        int i=0;
        for(String url: urls) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();
                File downoloaded = new File(downloadDir, String.valueOf(i).concat(String.valueOf(Thread.currentThread().getId())));
                BufferedSink sink = Okio.buffer(Okio.sink(downoloaded));
                Source source=response.body().source();
               // System.out.println(source.read(new Buffer(),8192L)+"/");
                Buffer b=new Buffer();
                long readCount;
                long total=0L;
                long bytePerCount=1024L;// 1kb
                long t;
                if(downLoadSpeedKB>0) {
                     t= bytePerCount / downLoadSpeedKB;
                }
                else {
                    t=0;
                }
                while((readCount = source.read(b,bytePerCount)) != -1L) {
                    total+=readCount;
                    Thread.sleep(t*1000);
                }
                sink.write(b,total);
                //sink.writeAll(source);
                sink.close();
                i++;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
