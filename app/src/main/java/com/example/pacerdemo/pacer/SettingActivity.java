package com.example.pacerdemo.pacer;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pacerdemo.R;
import com.example.pacerdemo.frame.BaseActivity;
import com.example.pacerdemo.frame.LogWriter;
import com.example.pacerdemo.service.IPedometerService;
import com.example.pacerdemo.service.PedometerService;
import com.example.pacerdemo.beans.Settings;
import com.example.pacerdemo.utils.Utiles;

public class SettingActivity extends BaseActivity {

    private String[] titleList = {"Step Length","Weight","Sensor Sensitivity","Sensor Sample Interval"};

    private ListView mListView;
    private ImageView mImg;
    private MyAdapter myAdapter;

    private IPedometerService mService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IPedometerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public class MyAdapter extends BaseAdapter {

        private String[] titleList = {"Step Length","Weight","Sensor Sensitivity","Sensor Sample Interval"};
        private Settings settings;

        public MyAdapter(){
            settings = new Settings(SettingActivity.this);
        }

        @Override
        public int getCount() {
            return titleList.length;
        }

        @Override
        public Object getItem(int position) {
            return titleList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            ViewHolder viewHolder;
            if (convertView==null) {
                convertView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.item_setting,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.titleTxt = convertView.findViewById(R.id.setting_title_txt);
                viewHolder.descText = convertView.findViewById(R.id.setting_desc_txt);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.titleTxt.setText(titleList[position]);

            switch (position) {
                case 0:
                    float stepLen = settings.getStepLength();
                    viewHolder.descText.setText(String.format("%s cm",stepLen));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stepLenClick(stepLen);
                        }
                    });

                    break;
                case 1:
                    float weight = settings.getWeight();
                    viewHolder.descText.setText(String.format("%s kg",weight));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
                case 2:
                    double sensitivity = settings.getSensitivity();
                    viewHolder.descText.setText(String.format("%s",Utiles.getFormatVal(sensitivity,"#.00")));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sensitivityClick();
                        }
                    });
                    break;
                case 3:
                    int interval = settings.getInterval();
                    viewHolder.descText.setText(String.format("%s ms", Utiles.getFormatVal(interval,"#.00")));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
            }

            return convertView;
        }

        private void stepLenClick(float stepLen) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("Set StepLength");
            View view = View.inflate(SettingActivity.this,R.layout.dialog_input,null);
            EditText inputEdt = view.findViewById(R.id.setting_input_edt);
            inputEdt.setText(String.valueOf(stepLen));
            builder.setView(view);
            builder.setNegativeButton("No",null);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String val = inputEdt.getText().toString();
                    if (val!=null&&val.length()>0) {
                        settings.setStepLength(Float.parseFloat(val));
                        if (myAdapter!=null) {
                            myAdapter.notifyDataSetChanged();
                        }

                    }
                    else {
                        Toast.makeText(SettingActivity.this,"Can not be null",Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.create().show();
        }

        private void sensitivityClick(){
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("Set Sensitivity");
            builder.setItems(R.array.sensitivity, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    settings.setSensitivity(Settings.SENSITIVITY_ARRAY[which]);
                    if (myAdapter!=null) {
                        myAdapter.notifyDataSetChanged();
                    }

                    if (mService!=null) {
                        try {
                            mService.setSensitivity(Settings.SENSITIVITY_ARRAY[which]);
                        } catch (RemoteException e) {
                            LogWriter.d(e.toString());
                        }
                    }
                }
            });

            builder.create().show();
        }
    }



    public class ViewHolder {
        public TextView titleTxt;
        public TextView descText;
    }


    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {

        setContentView(R.layout.activity_setting);
        mListView = findViewById(R.id.setting_lv);
        mImg = findViewById(R.id.setting_img);

        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);


        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onRequestData() {
        Intent serviceIntent = new Intent(SettingActivity.this, PedometerService.class);
        if (!Utiles.isServiceRuning(this,PedometerService.class.getName())) {

            startService(serviceIntent);
        } else {
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        boolean bindService = bindService(serviceIntent,connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
