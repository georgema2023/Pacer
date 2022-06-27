package com.example.pacerdemo.pacer;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pacerdemo.R;
import com.example.pacerdemo.beans.PedometerChartBean;
import com.example.pacerdemo.biz.ChartBiz;
import com.example.pacerdemo.frame.BaseActivity;
import com.example.pacerdemo.frame.LogWriter;
import com.example.pacerdemo.service.IPedometerService;
import com.example.pacerdemo.service.PedometerService;
import com.example.pacerdemo.utils.Utiles;
import com.example.pacerdemo.view.RoundProgressBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    public static final int STATUS_NOT_RUNNING = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int MESSAGE_UPDATE_STEP_COUNT = 1000;
    public static final int MESSAGE_UPDATE_CHART = 2000;
    public static final int UPDATE_STEPCOUNT_INTERVAL = 200;
    public static final long UPDATE_CHART_INTERVAL = 60000L;
    private RoundProgressBar mProgressBar;
    private TextView mCalorieTxt, mTimeTxt,mDistanceTxt,mStepCountTxt;
    private Button mResetBtn,mStartBtn;
    private BarChart mChart;
    private ImageView mSettingImg;

    private IPedometerService mService;
    private int serviceStatus = -1;
    private boolean isRunning = false;
    private boolean isUpdatingChart = false;
    private boolean bindService = false;

    private PedometerChartBean chartData;

    private MyHandler handler = new MyHandler(this);


    public class MyHandler extends Handler {
        public final WeakReference<HomeActivity> weakReference;

        public MyHandler(HomeActivity activity) {
            weakReference = new WeakReference<HomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomeActivity activity = weakReference.get();
            if (msg.what == MESSAGE_UPDATE_STEP_COUNT) {
                if (activity != null) {
                    updateStepCount();
                }
            }
            if (msg.what == MESSAGE_UPDATE_CHART) {
                if (activity!=null) {
                    ChartBiz chartBiz = new ChartBiz();
                    chartBiz.updateChart(chartData,mChart,mTimeTxt);
//                    updateChart(chartData);
                }
            }
        }
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what) {
//                case MESSAGE_UPDATE_STEP_COUNT:
//                    updateStepCount();
//                    break;
//                case MESSAGE_UPDATE_CHART:
//                    if (chartData!=null) {
//                        updateChart(chartData);
//                    }
//                    break;
//            }
//        }
//    };



    private void updateChart(PedometerChartBean chartData) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        if (chartData!=null) {
            for (int i = 1; i <= chartData.getIndex(); i++) {
                xVals.add(String.valueOf(i)+"min");
                int yVal = chartData.getDataArray()[i];
                yVals.add(new BarEntry(i,yVal));
            }
            mTimeTxt.setText(String.valueOf(chartData.getIndex())+"min");
            BarDataSet barDataSet = new BarDataSet(yVals,"Steps");
//            ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
//            barDataSets.add(barDataSet);
            BarData barData = new BarData(barDataSet);
            barData.setValueTextSize(10f);
            mChart.setData(barData);
            mChart.invalidate();

        }

    }

    private void updateStepCount() {
        if(mService!=null) {
            int stepCount = 0;
            double calorie = 0;
            double distance = 0;
            try {
                stepCount = mService.getStepsCount();
                calorie = mService.getCalorie();
                distance = mService.getDistance();
            } catch (RemoteException e) {
                LogWriter.d(e.toString());
            }
            mStepCountTxt.setText(String.valueOf(stepCount)+" Steps");
            mProgressBar.setProgress(stepCount);
            mCalorieTxt.setText(Utiles.getFormatVal(calorie)+" J");
            mDistanceTxt.setText(Utiles.getFormatVal(distance));
//            Toast.makeText(this,""+mProgressBar.getProgress(),Toast.LENGTH_SHORT);

        }
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IPedometerService.Stub.asInterface(service);
            try {
                serviceStatus = mService.getServiceStatus();
                if (serviceStatus==STATUS_RUNNING) {
                    mStartBtn.setText("Stop");
                    isRunning = true;
                    isUpdatingChart = true;
                    chartData = mService.getChartData();
                    updateChart(chartData);

                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                } else {
                    mStartBtn.setText("Start");
                }
            } catch (RemoteException e) {
                LogWriter.d(e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {

        setContentView(R.layout.activity_home);
        mProgressBar = findViewById(R.id.home_progressbar);
        mProgressBar.setProgress(0);
        mCalorieTxt = findViewById(R.id.home_calorie_txt);
        mTimeTxt = findViewById(R.id.home_time_txt);
        mDistanceTxt = findViewById(R.id.home_distance_txt);
        mStepCountTxt= findViewById(R.id.home_stepcount_txt);
        mResetBtn = findViewById(R.id.home_reset_btn);
        mStartBtn = findViewById(R.id.home_start_btn);
        mChart = findViewById(R.id.home_chart);
        mSettingImg = findViewById(R.id.home_setting_img);

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to delete records?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mService!=null) {
                            try {
                                mService.stopCount();
                                mService.resetCount();
                                chartData = mService.getChartData();
                                updateChart(chartData);
                                serviceStatus = mService.getServiceStatus();
                                if (serviceStatus==STATUS_RUNNING) {
                                    mStartBtn.setText("Stop");
                                } else {
                                    mStartBtn.setText("Start");
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No",null);

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    serviceStatus = mService.getServiceStatus();
                } catch (RemoteException e) {
                    LogWriter.d(e.toString());
                }

                if (serviceStatus==STATUS_RUNNING&&mService!=null) {
                    try {
                        mService.stopCount();
                        mStartBtn.setText("Start");
                        isRunning = false;
                        isUpdatingChart = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else if (serviceStatus==STATUS_NOT_RUNNING&&mService!=null) {
                    try {
                        mService.startCount();
                        mStartBtn.setText("Stop");
                        isRunning = true;
                        isUpdatingChart = true;
                        chartData = mService.getChartData();
                        updateChart(chartData);

                        new Thread(new StepRunnable()).start();
                        new Thread(new ChartRunnable()).start();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mSettingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRequestData() {

        Intent serviceIntent = new Intent(this,PedometerService.class);;
        if(!Utiles.isServiceRuning(this, PedometerService.class.getName())) {
            startService(serviceIntent);
        } else {
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        bindService = bindService(serviceIntent,connection,BIND_AUTO_CREATE);

        if (bindService&&mService!=null) {
            try {
                serviceStatus = mService.getServiceStatus();
                if (serviceStatus==STATUS_NOT_RUNNING) {
                    mStartBtn.setText("Start");
                } else {
                    mStartBtn.setText("Stop");
                    isRunning = true;
                    isUpdatingChart = true;
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                }
            } catch (RemoteException e) {
                LogWriter.d(e.toString());
            }
        } else {
            mStartBtn.setText("Start");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bindService) {
            bindService = false;
            isRunning = false;
            isUpdatingChart = false;
            unbindService(connection);
        }

    }

    private class StepRunnable implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    serviceStatus = mService.getServiceStatus();
                    if (serviceStatus==STATUS_RUNNING) {
                        handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                        handler.sendEmptyMessage(MESSAGE_UPDATE_STEP_COUNT);
                        Thread.sleep(UPDATE_STEPCOUNT_INTERVAL);

                    }
                } catch (RemoteException e) {
                    LogWriter.d(e.toString());
                } catch (InterruptedException e) {
                    LogWriter.d(e.toString());
                }
            }
        }
    }

    private class ChartRunnable implements Runnable {

        @Override
        public void run() {
            while (isUpdatingChart) {
                try {
                    chartData = mService.getChartData();
                    handler.removeMessages(MESSAGE_UPDATE_CHART);
                    handler.sendEmptyMessage(MESSAGE_UPDATE_CHART);
                    Thread.sleep(UPDATE_CHART_INTERVAL);
                } catch (RemoteException e) {
                    LogWriter.d(e.toString());
                } catch (InterruptedException e) {
                    LogWriter.d(e.toString());
                }

            }
        }
    }
}
