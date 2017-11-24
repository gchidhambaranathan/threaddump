package com.mxbean;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FileWatcher implements Runnable {
    private File file;
    private long modifiedTime;
    private IDump dump;

    FileWatcher(File file, IDump dump){
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
     this.file = file;
     this.dump = dump;
     this.modifiedTime = file.lastModified();
    }

    private void checkFileModifiled(){
        long currentModifiledTime = file.lastModified();

        if(currentModifiledTime > this.modifiedTime){
            System.out.println("File is modified..");
            this.modifiedTime =  currentModifiledTime;
            this.dump.dumpThreadInfoWithLocks();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkFileModifiled();
        }
    }
}
