package com.wave.workmanagerexample;

import android.arch.lifecycle.Observer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MESSAGE_STATUS = "message_status";
    TextView tvStatus;
    Button btnSend, btnStorageNotLow, btnBatteryNotLow, btnRequiresCharging, btnDeviceIdle, btnNetworkType;
    OneTimeWorkRequest mRequest;
    WorkManager mWorkManager;
    PeriodicWorkRequest periodicWorkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        tvStatus = findViewById(R.id.tvStatus);
        btnSend = findViewById(R.id.btnSend);
        mWorkManager = WorkManager.getInstance();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        btnSend = findViewById(R.id.btnSend);
        btnStorageNotLow = findViewById(R.id.buttonStorageNotLow);
        btnBatteryNotLow = findViewById(R.id.buttonBatteryNotLow);
        btnRequiresCharging = findViewById(R.id.buttonRequiresCharging);
        btnDeviceIdle = findViewById(R.id.buttonDeviceIdle);
        btnNetworkType = findViewById(R.id.buttonNetworkType);
        btnSend.setOnClickListener(this);
        btnStorageNotLow.setOnClickListener(this);
        btnBatteryNotLow.setOnClickListener(this);
        btnRequiresCharging.setOnClickListener(this);
        btnDeviceIdle.setOnClickListener(this);
        btnNetworkType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        tvStatus.setText("");
        Constraints mConstraints;

        periodicWorkRequest = null;
        mRequest = null;
        switch (v.getId()) {
            case R.id.btnSend:
                mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
                break;
            case R.id.buttonStorageNotLow:
                /**
                 * Constraints
                 * If TRUE task execute only when storage's is not low
                 */
                mConstraints = new Constraints.Builder().setRequiresStorageNotLow(true).build();
                /**
                 * OneTimeWorkRequest with requiresStorageNotLow Constraints
                 */
                mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setConstraints(mConstraints).build();

                break;
            case R.id.buttonBatteryNotLow:
                /**
                 * Constraints
                 * If TRUE task execute only when battery isn't low
                 */
                mConstraints = new Constraints.Builder().setRequiresBatteryNotLow(true).build();
                /**
                 * OneTimeWorkRequest with requiresBatteryNotLow Constraints
                 */
                mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setConstraints(mConstraints).build();
                break;
            case R.id.buttonRequiresCharging:
                /**
                 * Constraints
                 * If TRUE while the device is charging
                 */
                mConstraints = new Constraints.Builder().setRequiresCharging(true).build();
                /**
                 * OneTimeWorkRequest with requiresCharging Constraints
                 */
                mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setConstraints(mConstraints).build();
                break;
            case R.id.buttonDeviceIdle:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /**
                     * Constraints
                     * If TRUE while the  device is idle
                     */
                    mConstraints = new Constraints.Builder().setRequiresDeviceIdle(true).build();
                    /**
                     * OneTimeWorkRequest with requiresDeviceIdle Constraints
                     */
                    mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setConstraints(mConstraints).build();
                }
                break;
            case R.id.buttonNetworkType:
                /**
                 * Constraints
                 * Network type is conneted
                 */
                mConstraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                /**
                 * OneTimeWorkRequest with requiredNetworkType Connected Constraints
                 */
                //  mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setConstraints(mConstraints).build();

                periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .build();

                break;
            default:
                break;

        }
        /**
         * Fetch the particular task status using request ID
         */


        /**
         * Enqueue the WorkRequest
         */
        if (mRequest != null) {
            mWorkManager.enqueue(mRequest);


            mWorkManager.getWorkInfoByIdLiveData(mRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(@Nullable WorkInfo workInfo) {
                    if (workInfo != null) {
                        WorkInfo.State state = workInfo.getState();
                        tvStatus.append(state.toString() + "\n");

                    }
                }
            });


        } else {
            mWorkManager.enqueue(periodicWorkRequest);


            mWorkManager.getWorkInfoByIdLiveData(periodicWorkRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(@Nullable WorkInfo workInfo) {
                    if (workInfo != null) {
                        WorkInfo.State state = workInfo.getState();
                        tvStatus.append(state.toString() + "\n");

                    }
                }
            });
        }


    }
}
