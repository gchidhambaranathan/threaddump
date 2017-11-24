package com.mxbean;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DumpMain {
    private static File changeFile = new File("/home/chida/myrepo/threaddump/ChangeMe.txt");
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);
    public static void main(String[] args) {
        IDump iDump = new ThreadDump();
        FileWatcher fileWatcher = new FileWatcher(changeFile,iDump);
        executorService.execute(fileWatcher);

       for(int i = 0; i < 5; i++){
           MyOwnThread ex = new MyOwnThread();
           executorService.execute(ex);
       }


    }

    private static class MyOwnThread  implements Runnable {

        @Override
        public void run() {
            while(true){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread");
            }
        }
    }
}
