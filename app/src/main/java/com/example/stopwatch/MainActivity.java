package com.example.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTime;
    private Button buttonPlayPause, buttonLap, buttonRestart;
    private ListView listViewLaps;

    private Handler handler;
    private Runnable runnable;

    private long startTime, timeInMillis, timeSwapBuff, updateTime;
    private boolean isRunning;

    private ArrayList<String> lapTimes;
    private ArrayAdapter<String> lapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTime = findViewById(R.id.textViewTime);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonLap = findViewById(R.id.buttonLap);
        buttonRestart = findViewById(R.id.buttonRestart);
        listViewLaps = findViewById(R.id.listViewLaps);

        handler = new Handler();
        isRunning = false;

        lapTimes = new ArrayList<>();
        lapAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapTimes);
        listViewLaps.setAdapter(lapAdapter);

        buttonPlayPause.setOnClickListener(v -> {
            if (isRunning) {
                pauseStopwatch();
            } else {
                startStopwatch();
            }
        });
        buttonLap.setOnClickListener(v -> recordLap());
        buttonRestart.setOnClickListener(v -> restartStopwatch());
    }

    private void startStopwatch() {
        startTime = System.currentTimeMillis();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                timeInMillis = System.currentTimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMillis;
                int secs = (int) (updateTime / 1000);
                int mins = secs / 60;
                int hrs = mins / 60;
                secs = secs % 60;
                mins = mins % 60;
                textViewTime.setText(String.format("%02d:%02d:%02d", hrs, mins, secs));
                handler.postDelayed(this, 0);
            }
        }, 0);
        buttonPlayPause.setBackgroundResource(R.drawable.ic_pause);
        buttonLap.setVisibility(View.VISIBLE);
        buttonRestart.setVisibility(View.VISIBLE);
        isRunning = true;
    }

    private void pauseStopwatch() {
        timeSwapBuff += timeInMillis;
        handler.removeCallbacks(runnable);
        buttonPlayPause.setBackgroundResource(R.drawable.ic_play);
        isRunning = false;
    }

    private void restartStopwatch() {
        if (isRunning) {
            pauseStopwatch();
        }
        timeSwapBuff = 0L;
        startTime = 0L;
        updateTime = 0L;
        textViewTime.setText("00:00:00");
        lapTimes.clear();
        lapAdapter.notifyDataSetChanged();
        buttonRestart.setVisibility(View.GONE);
        buttonLap.setVisibility(View.GONE);
    }

    private void recordLap() {
        if (isRunning) {
            lapTimes.add(textViewTime.getText().toString());
            lapAdapter.notifyDataSetChanged();
        }
    }
}
