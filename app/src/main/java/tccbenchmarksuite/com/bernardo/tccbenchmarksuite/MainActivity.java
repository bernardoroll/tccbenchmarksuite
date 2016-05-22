package tccbenchmarksuite.com.bernardo.tccbenchmarksuite;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
//import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private CheckBox mCbMemory, mCbCpu, mCbDisk;
    private EditText mEtPid;
    private LinearLayout mLlResults;

    private final String ANDROID_STUDIO_PACKAGENAME = "com.bernardo.tccapp";
    private final String XAMARIN_PACKAGENAME = "com.bernardo.tccappxamarin";
    private final String APPERY_PACKAGENAME = "com.bernardo.tccappappery";

    private String mWhichAppIsUnderTest = "NONE";

    Intent broadcastIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialLoad();
    }

    private void initialLoad() {
        mCbCpu = (CheckBox) findViewById(R.id.activity_main_cb_cpu);
        mCbDisk = (CheckBox) findViewById(R.id.activity_main_cb_disk);
        mCbMemory = (CheckBox) findViewById(R.id.activity_main_cb_memory);
        mEtPid = (EditText) findViewById(R.id.activity_main_et_pid);
        mLlResults = (LinearLayout) findViewById(R.id.activity_main_ll_results);
    }

    public void btnGetAndroidStudioPidClicked(View v) {
        try {
//            ActivityManager aManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//            int id = -1;
//            for (ActivityManager.RunningAppProcessInfo rAPI : aManager.getRunningAppProcesses()) {
//                if (rAPI.processName.equals(ANDROID_STUDIO_PACKAGENAME)) {
//                    id = rAPI.pid;
//                    //mEtPid.setText(String.valueOf(rAPI.pid));
//                }
//            }
//            if(id >= 0) {
//                mEtPid.setText(String.valueOf(id));
//            } else {
//                mEtPid.setText("");
//                Toast.makeText(this, "A aplicação do Android Studio não está em execução.",
//                        Toast.LENGTH_LONG).show();
//            }
            int pId = getPidByPackagename(ANDROID_STUDIO_PACKAGENAME);
            if(pId >= 0) {
                mEtPid.setText(String.valueOf(pId));
                mWhichAppIsUnderTest = "AndroidStudio";
            } else {
                mEtPid.setText("");
                Toast.makeText(this, "A aplicação do Android Studio não está em execução.",
                        Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {
            Toast.makeText(this, "Não foi possível coletar o PID da aplicação do Android Studio.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void btnGetXamarinPidClicked(View v) {
        try {
            int pId = getPidByPackagename(XAMARIN_PACKAGENAME);
            if(pId >= 0) {
                mEtPid.setText(String.valueOf(pId));
                mWhichAppIsUnderTest = "Xamarin";
            } else {
                mEtPid.setText("");
                Toast.makeText(this, "A aplicação do Xamarin não está em execução.",
                        Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {
            Toast.makeText(this, "Não foi possível coletar o PID da aplicação do Xamarin",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void btnGetApperyPidClicked(View v) {
        try {
            int pId = getPidByPackagename(APPERY_PACKAGENAME);
            if(pId >= 0) {
                mEtPid.setText(String.valueOf(pId));
                mWhichAppIsUnderTest = "Appery";
            } else {
                mEtPid.setText("");
                Toast.makeText(this, "A aplicação do Appery não está em execução.",
                        Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {
            Toast.makeText(this, "Não foi possível coletar o PID da aplicação do Appery",
                    Toast.LENGTH_LONG).show();
        }
    }

    private int getPidByPackagename(String packagename) {
        ActivityManager aManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo rAPI : aManager.getRunningAppProcesses()) {
            if(rAPI.processName.equals(packagename)) {
                return rAPI.pid;
            }
        }
        return -1;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showResults(intent);
        }
    };

    private void showResults(Intent iResult) {


        TextView tvResult;

        tvResult = new TextView(this);
        tvResult.setText("------------------------------------");
        mLlResults.addView(tvResult);

        tvResult = new TextView(this);
        tvResult.setText("Iteração: " + iResult.getIntExtra("counter", -1));
        mLlResults.addView(tvResult);

        tvResult = new TextView(this);
        tvResult.setText("Timestamp: " + iResult.getLongExtra("time", -1));
        mLlResults.addView(tvResult);

        if(mCbMemory.isChecked()) {
            tvResult = new TextView(this);
            tvResult.setText("Memória: " + iResult.getIntExtra("memoryUsage", -1));
            mLlResults.addView(tvResult);
        }

        if(mCbCpu.isChecked()) {
            tvResult = new TextView(this);
            long cpuUsage = iResult.getLongExtra("cpuUsage", -1);
            if(cpuUsage >= 0) {
                //tvResult.setText("CPU: " + String.format("%.2f", cpuUsage) + "%");
                tvResult.setText("Clock ticks: " + String.valueOf(cpuUsage));
            } else {
                tvResult.setText("Clock ticks: ---");
            }

            mLlResults.addView(tvResult);
        }

        if(mCbDisk.isChecked()) {
            tvResult = new TextView(this);
            tvResult.setText("Disco: " + iResult.getFloatExtra("diskUsage", -1));
            mLlResults.addView(tvResult);
        }

        tvResult = new TextView(this);
        tvResult.setText("------------------------------------");
        mLlResults.addView(tvResult);
    }

    public void btnStopMonitorClicked(View v) {
        unregisterReceiver(broadcastReceiver);
        stopService(broadcastIntent);

        //Calendar endTime = new GregorianCalendar();
        long endTime = System.currentTimeMillis();
        TextView tvEndTime = new TextView(this);
        tvEndTime.setText("------------------------------------");
        mLlResults.addView(tvEndTime);

        tvEndTime = new TextView(this);
        tvEndTime.setText("Timestamp final: " + String.valueOf(endTime));
        mLlResults.addView(tvEndTime);

        generateDataFile();

        //mLlResults.removeAllViews();

    }

    private void generateDataFile() {

        List<ResultsModel> listResults = new ArrayList<ResultsModel>();
        ResultsModel result;

        int childrenCount = mLlResults.getChildCount();
        View v = null;
        TextView tv = null;
        for(int i = 0; i < childrenCount; i++) {
            v = mLlResults.getChildAt(i);
            tv = (TextView) v;
            if(tv.getText().toString().startsWith("-") ||
                    tv.getText().toString().contains(" inicial") ||
                    tv.getText().toString().contains(" final")) {

                continue;
            } else {

                int resultId = -1;
                int resultMemory = -1;
                long resultTimestamp = -1;
                long resultClockTicks = -1;

                String id = tv.getText().toString().split(": ")[1];
                try {
                    resultId = Integer.parseInt(id);
                } catch (Exception ex) {
                    resultId = -1;
                }

                v = mLlResults.getChildAt(++i);
                String timestamp = ((TextView)v).getText().toString().split(": ")[1];
                try {
                    resultTimestamp = Long.parseLong(timestamp);
                } catch (Exception ex) {
                    resultTimestamp = -1;
                }

                if(mCbMemory.isChecked()) {
                    v = mLlResults.getChildAt(++i);
                    String memoryUsage = ((TextView)v).getText().toString().split(": ")[1];
                    try {
                        resultMemory = Integer.parseInt(memoryUsage);
                    } catch (Exception ex) {
                        resultMemory = -1;
                    }
                }
                if(mCbCpu.isChecked()) {
                    v = mLlResults.getChildAt(++i);
                    String cpuUsage = ((TextView)v).getText().toString().split(": ")[1];
                    try {
                        resultClockTicks = Long.parseLong(cpuUsage);
                    } catch (Exception ex) {
                        resultClockTicks = -1;
                    }
                }
                if(mCbDisk.isChecked()) {
                    // TODO: implementar análise do uso de disco
                    v = mLlResults.getChildAt(++i);
                    String diskUsage = ((TextView)v).getText().toString().split(": " )[1];
                    try {

                    } catch (Exception ex) {

                    }
                }
                result = new ResultsModel(resultId, resultTimestamp, resultMemory,
                        resultClockTicks);
                listResults.add(result);
            }
        }
        writeResultsToFile(listResults);
    }

    private void writeResultsToFile(List<ResultsModel> listResults) {
        if(isExternalStorageWritable()) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/TCC/Benchmarking");
            dir.mkdirs();
            File file = new File(dir, mWhichAppIsUnderTest + "_" + new GregorianCalendar().getTime()
                    + ".csv");
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(fOut);
                for(ResultsModel r : listResults) {
                    pw.println(r.getId() + ";" + r.getTimestamp() + ";" + r.getMemory() + ";" +
                            r.getClockTicks());
                }
                pw.flush();
                pw.close();
                fOut.close();
            } catch(FileNotFoundException fileNotFound) {
                Log.d(TAG, "writeResultsToFile() error: " + fileNotFound.getMessage());
            } catch (IOException ioException) {
                Log.d(TAG, "writeResultsToFile() error: " + ioException.getMessage());
            } catch (Exception ex) {
                Log.d(TAG, "writeResultsToFile() error: " + ex.getMessage());
            }
        }
    }

    /**
     * Checks if external storage is available for read and write
     * @return true if the external storage is mounted, false otherwise.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void btnMonitorClicked(View v) {
//        this.pid = i.getIntExtra("pid", -1);
//        this.memoryCheck = i.getBooleanExtra("isMemoryChecked", false);
//        this.cpuCheck = i.getBooleanExtra("isCpuChecked", false);
//        this.diskCheck = i.getBooleanExtra("isDiskChecked", false);
        broadcastIntent = new Intent(this, BroadcastBenchmarkService.class);
        broadcastIntent.putExtra("pid", Integer.parseInt(mEtPid.getText().toString()));
        broadcastIntent.putExtra("isMemoryChecked", mCbMemory.isChecked());
        broadcastIntent.putExtra("isCpuChecked", mCbCpu.isChecked());
        broadcastIntent.putExtra("isDiskChecked", mCbDisk.isChecked());

        //Calendar startTime = new GregorianCalendar();
        long startTime = System.currentTimeMillis();
        TextView tvStartTime = new TextView(this);
        tvStartTime.setText("Timestamp inicial: " + String.valueOf(startTime));
        mLlResults.addView(tvStartTime);

        tvStartTime = new TextView(this);
        tvStartTime.setText("------------------------------------");
        mLlResults.addView(tvStartTime);

        startService(broadcastIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(
                BroadcastBenchmarkService.BROADCAST_ACTION));




//        ActivityManager aManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//
//        int pid = Integer.parseInt(mEtPid.getText().toString());
//
//        boolean isMemoryChecked = mCbMemory.isChecked();
//        boolean isCpuChecked = mCbCpu.isChecked();
//        boolean isDiskChecked = mCbDisk.isChecked();
//
//        for(ActivityManager.RunningAppProcessInfo rAPI : aManager.getRunningAppProcesses()) {
//            if(rAPI.pid == pid) {
//                if(isMemoryChecked) {
//                    android.os.Debug.MemoryInfo[] mi = aManager.getProcessMemoryInfo(
//                            new int[] {pid});
//                    for (android.os.Debug.MemoryInfo info : mi) {
//
//                        int totalKB = info.getTotalPss();
//
//                        Toast.makeText(this, "Total: " + totalKB, Toast.LENGTH_LONG).show();
//
//
//                    }
//                }
//
//                if(isCpuChecked) {}
//
//                if(isDiskChecked) {}
//            }
//        }

    }



}
