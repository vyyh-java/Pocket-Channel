package com.example.in.ui.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.in.R;

import java.util.Locale;

public class ClockTimerManager {
    private final TextView tvTimer;
    private final TextView tvStart;
    private final TextView tvEnd;
    private final ImageButton btnStartPause;
    private final TextClock tcTimer;
    private final View llTimer;
    private final View rvTask;

    private final NumberPicker npHour;
    private final NumberPicker npMinute;
    private final NumberPicker npSecond;

    private final NumberPicker.Formatter zeroDigitFormatter = value -> String.format(Locale.getDefault(), "%02d", value);

    private CountDownTimer timer;

    private static final int MAX_HOUR = 2;
    private static final int MAX_MINUTE = 59;
    private static final int MAX_SECOND = 59;

    public ClockTimerManager(View rootView, Context context) {
        this.tvTimer = (TextView) rootView.findViewById(R.id.TVTimer);
        this.btnStartPause = (ImageButton) rootView.findViewById(R.id.IBtoggler);
        this.tcTimer = (TextClock) rootView.findViewById(R.id.TCTimer);
        this.llTimer = (LinearLayout) rootView.findViewById(R.id.LLTimer);
        this.rvTask = (RecyclerView) rootView.findViewById(R.id.RVTask);
        this.npHour = (NumberPicker) rootView.findViewById(R.id.NPHour);
        this.npMinute = (NumberPicker) rootView.findViewById(R.id.NPMinute);
        this.npSecond = (NumberPicker) rootView.findViewById(R.id.NPSecond);
        this.tvStart = (TextView) rootView.findViewById(R.id.TVStart);
        this.tvEnd = (TextView) rootView.findViewById(R.id.TVEnd);

        // Define range
        setTimerUi(npHour, MAX_HOUR, 0);
        setTimerUi(npMinute, MAX_MINUTE, 0);
        setTimerUi(npSecond, MAX_SECOND, 0);

        tcTimer.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.INVISIBLE);

        // Toggle UI elements -> picker interface(timer, task, text, clock)
        tcTimer.setOnClickListener(v -> {
            setInterface(View.VISIBLE, View.GONE, View.VISIBLE, View.INVISIBLE, false, true, true);
        });

        tvTimer.setOnClickListener(v -> {
            setInterface(View.GONE, View.VISIBLE, View.INVISIBLE, View.VISIBLE, true, false, false);
        });

        npHour.setOnValueChangedListener((picker, oldVal, newVal) -> updateTimerText());
        npMinute.setOnValueChangedListener((picker, oldVal, newVal) -> updateTimerText());
        npSecond.setOnValueChangedListener((picker, oldVal, newVal) -> updateTimerText());

        btnStartPause.setOnClickListener(v -> {

            boolean isToStart = !v.isActivated();

            if (isToStart) {
                setBtnUi(v, true, v.getRotation(), -90f);
                setInterface(View.GONE, View.VISIBLE, View.VISIBLE, View.INVISIBLE, false, false, true);

                int hour = npHour.getValue();
                int minute = npMinute.getValue();
                int second = npSecond.getValue();
                long millis = (hour * 3600000L) + (minute * 60000L) + (second * 1000L);

                if (millis == 0) {
                    Toast.makeText(context, "Please set timer", Toast.LENGTH_SHORT).show();
                    return;
                };

                timer = new CountDownTimer(millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long h = millisUntilFinished / 3600000;
                        long m = (millisUntilFinished % 3600000) / 60000;
                        long s = (millisUntilFinished % 60000) / 1000;

                        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s));
                    }

                    @Override
                    public void onFinish() {
                        setBtnUi(v, false, v.getRotation(), 0f);
                        setInterface(View.GONE, View.VISIBLE, View.INVISIBLE, View.VISIBLE, true, false, false);
                        release();
                    }
                }.start();

            } else {
                release();
                setBtnUi(v, false, v.getRotation(), 0f);
                setInterface(View.GONE, View.VISIBLE, View.INVISIBLE, View.VISIBLE, true, false, false);
                updateTimerText();
            }
        });
    }

    private void updateTimerText() {
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                npHour.getValue(), npMinute.getValue(), npSecond.getValue()));
    }

    public void release() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setTimerUi(NumberPicker np, int max, int value) {
        np.setMinValue(0);
        np.setMaxValue(max);
        np.setFormatter(zeroDigitFormatter);
        np.setValue(value);
        np.setWrapSelectorWheel(true);
    }

    private void setInterface(int lltimer, int rvtask, int tvtimer, int tctimer, boolean tcClickable, boolean tvClickable, boolean txUpdate){
        llTimer.setVisibility(lltimer);
        rvTask.setVisibility(rvtask);
        tvTimer.setVisibility(tvtimer);
        tcTimer.setVisibility(tctimer);
        tcTimer.setClickable(tcClickable);
        tvTimer.setClickable(tvClickable);
        if(txUpdate)
            updateTimerText();
    }
    private void setBtnUi(View btnStartPause, boolean isActivated, float from, float to){
        btnStartPause.setActivated(isActivated);
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnStartPause, "rotation", from, to);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
        if(isActivated)
            tvStart.setShadowLayer(10, 0, 0, 0xFFFFFFFF);
        else
            tvStart.setShadowLayer(0, 0, 0, 0xFF000000);
    }
}
