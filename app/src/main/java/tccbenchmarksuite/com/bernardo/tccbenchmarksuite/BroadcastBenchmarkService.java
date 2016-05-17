package tccbenchmarksuite.com.bernardo.tccbenchmarksuite;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class BroadcastBenchmarkService extends Service {

    private final String TAG = "BroadcastBenchmarkSvc";

    public static final String BROADCAST_ACTION = "com.bernardo.tccbenchmarksuite.calculatesage";

    private final Handler handler = new Handler();
    Intent intent;

    int counter = 0;

    int pid;
    boolean memoryCheck, cpuCheck, diskCheck;

    public BroadcastBenchmarkService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent i, int startId) {
        this.pid = i.getIntExtra("pid", -1);
        this.memoryCheck = i.getBooleanExtra("isMemoryChecked", false);
        this.cpuCheck = i.getBooleanExtra("isCpuChecked", false);
        this.diskCheck = i.getBooleanExtra("isDiskChecked", false);
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 5000);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 5000);
        }
    };

    private void DisplayLoggingInfo() {
        Log.d(TAG, ".DisplayLoggingInfo() called.");

        if(memoryCheck) {
            intent.putExtra("memoryUsage", getMemoryInfo());
        }
        if(cpuCheck) {
            intent.putExtra("cpuUsage", getCpuInfo());
        }
        if(diskCheck) {
            intent.putExtra("diskUsage", getDiskInfo());
        }
        intent.putExtra("time", new Date().toLocaleString());
        intent.putExtra("counter", ++counter);
        sendBroadcast(intent);
    }

    private int getMemoryInfo() {
        ActivityManager aManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo rAPI : aManager.getRunningAppProcesses()) {
            if(rAPI.pid == this.pid){
                android.os.Debug.MemoryInfo[] mi = aManager.getProcessMemoryInfo(
                        new int[] {this.pid});
                for (android.os.Debug.MemoryInfo info : mi) {
                    return info.getTotalPss();
                }
            }
        }
        return -1;
    }

    private float getDiskInfo() {
        return -1;
    }

    private float getCpuInfo() {
        try {
            //Tentar pegar a frequencia do processador
            ///sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq
            //RandomAccessFile freqReader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
            //String freqLoad = freqReader.readLine();
            //int freqLoad = Integer.parseInt(freqReader.readLine());
            float USER_HZ = 100F;

            // /proc/{pid}/smaps

            RandomAccessFile totalSystemTime = new RandomAccessFile("proc/uptime", "r");
            float upTime = Float.parseFloat(totalSystemTime.readLine().split(" ")[0]);

            RandomAccessFile reader = new RandomAccessFile("/proc/" + this.pid + "/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

//            #14 utime - CPU time spent in user code, measured in clock ticks
//            #15 stime - CPU time spent in kernel code, measured in clock ticks
//            #16 cutime - Waited-for children's CPU time spent in user code (in clock ticks)
//            #17 cstime - Waited-for children's CPU time spent in kernel code (in clock ticks)
//            #22 starttime - Time when the process started, measured in clock ticks


            // TODO: Medir somente os ticks

            long uTime = Long.parseLong(toks[13]);
            long sTime = Long.parseLong(toks[14]);
            long cuTime = Long.parseLong(toks[15]);
            long csTime = Long.parseLong(toks[16]);
            long startTime = Long.parseLong(toks[21]);

            long total_time = uTime + sTime;
            total_time = total_time + cuTime + csTime;
            float seconds = upTime - (startTime / USER_HZ);
            //float seconds2 = upTime - (startTime / freqLoad);

            float cpu_usage = 100 * ((total_time / USER_HZ) / seconds);
            //float cpu_usage2 = 100 * ((total_time / freqLoad) / seconds2);


            return cpu_usage;
/*

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
*/
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }



}
