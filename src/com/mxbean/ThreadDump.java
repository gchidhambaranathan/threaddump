package com.mxbean;


import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


public class ThreadDump  implements IDump{

    private static final String fileName = "/home/chida/myrepo/threaddump/threaddump.txt";
    FileWriter fileWriter;
    private static final String newLine = System.getProperty("line.separator");


    public void dumpThreadInfoWithLocks() {

        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadMXBean.setThreadCpuTimeEnabled(true);

        try {
            try {
                this.fileWriter = new FileWriter(fileName,true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeFileToString("*************************************"+ new Date().toString());


            ThreadInfo[] tinfos = threadMXBean.dumpAllThreads(true, true);
            Arrays.sort(tinfos, new Comparator<ThreadInfo>() {
                @Override
                public int compare(ThreadInfo o1, ThreadInfo o2) {
                    Long cpuTime1 = threadMXBean.getThreadCpuTime(o1.getThreadId());
                    Long cpuTime2 = threadMXBean.getThreadCpuTime(o2.getThreadId());
                    return cpuTime1.compareTo(cpuTime2);
                }
            });
            for (ThreadInfo ti : tinfos) {
                printThreadInfo(ti);
                LockInfo[] syncs = ti.getLockedSynchronizers();
                printLockInfo(syncs);
            }

            long[] tids = threadMXBean.findDeadlockedThreads();
            writeFileToString("DeadLock. start ....................");
            if(tids != null){

                tinfos = threadMXBean.getThreadInfo(tids, Integer.MAX_VALUE);
                for(ThreadInfo threadInfo : tinfos){
                    printThreadInfo(threadInfo);
                }
            }
            writeFileToString("DeadLock. end ....................");

            writeFileToString("++++++++++++++++++++++++++++++++++++++++"+ new Date().toString());
            this.fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String INDENT = "    ";

    private void printThreadInfo(ThreadInfo ti) throws IOException {
        // print thread information
        printThread(ti);

        // print stack trace with locks
        StackTraceElement[] stacktrace = ti.getStackTrace();
        MonitorInfo[] monitors = ti.getLockedMonitors();
        for (int i = 0; i < stacktrace.length; i++) {
            StackTraceElement ste = stacktrace[i];
            writeFileToString(INDENT + "at " + ste.toString());
            for (MonitorInfo mi : monitors) {
                if (mi.getLockedStackDepth() == i) {

                    writeFileToString(INDENT + "  - locked " + mi);
                }
            }
        }
       writeFileToString("");
    }

    private void printThread(ThreadInfo ti) throws IOException {
        StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"" + " Id="
                + ti.getThreadId() + " in " + ti.getThreadState());
        if (ti.getLockName() != null) {
            sb.append(" on lock=" + ti.getLockName());
        }
        if (ti.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (ti.isInNative()) {
            sb.append(" (running in native)");
        }

        writeFileToString(sb.toString());
        if (ti.getLockOwnerName() != null) {
          writeFileToString(INDENT + " owned by " + ti.getLockOwnerName() + " Id="
                  + ti.getLockOwnerId());
        }
    }

    private void printLockInfo(LockInfo[] locks) throws IOException {
       writeFileToString(INDENT + "Locked synchronizers: count = " + locks.length);
        for (LockInfo li : locks) {
            writeFileToString(INDENT + "  - " + li);
        }
      writeFileToString("");
    }

    public void writeFileToString(String line) throws IOException {
        line += line + newLine;
       // FileUtils.writeStringToFile(dumpFile,line,"utf8");
        this.fileWriter.write(line);
    }


}
