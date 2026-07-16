package com.example.in.ui.main;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Locale;

//timer logic
public class ClockTimerViewModel extends ViewModel {
    private final MutableLiveData<String> timerText = new MutableLiveData<>();
    private final MutableLiveData<Integer> timerValue = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTimerStart = new MutableLiveData<>();
    private CountDownTimer timer;

    public LiveData<String> getTimerText() {
        return timerText;
    }
    public LiveData<Boolean> getIsTimerStart() {
        return isTimerStart;
    }
    public void startTimer(){

        if (timerValue.getValue() == null || timerValue.getValue() <= 0) {
            return;
        }

        isTimerStart.setValue(true);
        long millis = timerValue.getValue();

        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long l) {
                timerValue.setValue((int) l);
                long hour = l / 3600000;
                long minute = (l % 3600000) / 60000;
                long second = (l % 60000) / 1000;
                timerText.setValue(format(hour, minute, second));
            }
            @Override
            public void onFinish() {
                timerText.setValue("00:00:00");
                timerValue.setValue(0);
                isTimerStart.setValue(false);
            }
        }.start();
    }
    public void stopTimer(){
        isTimerStart.setValue(false);
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }
    public void setTimer(int hour, int minute, int second){
        timerValue.setValue((hour * 3600000) + (minute * 60000) + (second * 1000));
        timerText.setValue(format(hour, minute, second));
    }
    private String format(long hour, long minute, long second){
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTimer();
    }
}
