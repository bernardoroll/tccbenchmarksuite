package tccbenchmarksuite.com.bernardo.tccbenchmarksuite;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private CheckBox mCbMemory, mCbCpu, mCbDisk;
    private EditText mEtPid;
    private LinearLayout mLlResults;

    private final String ANDROID_STUDIO_PACKAGENAME = "com.bernardo.tccapp";
    private final String XAMARIN_PACKAGENAME = "com.bernardo.tccappxamarin";
    private final String APPERY_PACKAGENAME = "com.bernardo.tccappappery";

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
        tvResult.setText("Hora: " + iResult.getStringExtra("time"));
        mLlResults.addView(tvResult);

        if(mCbMemory.isChecked()) {
            tvResult = new TextView(this);
            tvResult.setText("Memória: " + iResult.getIntExtra("memoryUsage", -1));
            mLlResults.addView(tvResult);
        }

        if(mCbCpu.isChecked()) {
            tvResult = new TextView(this);
            float cpuUsage = iResult.getFloatExtra("cpuUsage", -1);
            if(cpuUsage >= 0) {
                tvResult.setText("CPU: " + String.format("%.2f", cpuUsage) + "%");
            } else {
                tvResult.setText("CPU: ---");
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

        mLlResults.removeAllViews();

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
