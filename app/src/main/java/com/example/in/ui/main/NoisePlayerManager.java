package com.example.in.ui.main;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.in.R;

public class NoisePlayerManager {
    private final Context context;
    private final RadioGroup rgNoise;
    private final TransitionDrawable tdRain, tdForest, tdCamp;
    private final RadioButton rbRain, rbForest, rbCamp;

    private MediaPlayer mediaPlayer;
    private final AudioManager audioManager;
    private final AudioFocusRequest focusRequest;

    private int currentPlayingId = -1;

    public NoisePlayerManager(View rootView, Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.rgNoise = rootView.findViewById(R.id.RGSong);
        this.tdRain = (TransitionDrawable) ((ImageView) rootView.findViewById(R.id.IVRain)).getDrawable();
        this.tdForest = (TransitionDrawable) ((ImageView) rootView.findViewById(R.id.IVForest)).getDrawable();
        this.tdCamp = (TransitionDrawable) ((ImageView) rootView.findViewById(R.id.IVCamp)).getDrawable();
        this.rbRain = rootView.findViewById(R.id.RBRain);
        this.rbForest = rootView.findViewById(R.id.RBForest);
        this.rbCamp = rootView.findViewById(R.id.RBCamp);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        AudioManager.OnAudioFocusChangeListener focusChangeListener = focusChange -> {
            if (mediaPlayer == null) return;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (!mediaPlayer.isPlaying() && currentPlayingId != -1) {
                        mediaPlayer.start();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    shutdownEverything();
                    break;
            }
        };

        focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build();

        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {
        View.OnClickListener clickListener = v -> {
            int clickedId = v.getId();
            if (clickedId == currentPlayingId) {
                shutdownEverything();
            } else {
                switchToSound(clickedId);
            }
        };

        rbRain.setOnClickListener(clickListener);
        rbForest.setOnClickListener(clickListener);
        rbCamp.setOnClickListener(clickListener);
    }

    private void switchToSound(int checkedId) {
        animateUnGlow(currentPlayingId);
        animateGlow(checkedId);
        currentPlayingId = checkedId;
        rgNoise.check(checkedId);
        int soundRawId = getSoundResource(checkedId);
        startPlayer(soundRawId);
    }

    private void shutdownEverything() {
        animateUnGlow(currentPlayingId);
        currentPlayingId = -1;
        rgNoise.clearCheck();
        stopPlayer();
    }

    private int getSoundResource(int checkedId) {
        if (checkedId == R.id.RBRain) return R.raw.rain;
        if (checkedId == R.id.RBForest) return R.raw.forest;
        return R.raw.camp;
    }

    private void startPlayer(int resId) {
        stopPlayer();
        int result = audioManager.requestAudioFocus(focusRequest);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        audioManager.abandonAudioFocusRequest(focusRequest);
    }

    private void animateGlow(int id) {
        TransitionDrawable td = getTransitionDrawableById(id);
        if (td != null) {
            td.startTransition(500);
        }
    }

    private void animateUnGlow(int id) {
        TransitionDrawable td = getTransitionDrawableById(id);
        if (td != null) {
            td.reverseTransition(500);
        }
    }

    private TransitionDrawable getTransitionDrawableById(int id) {
        if (id == R.id.RBRain) return tdRain;
        if (id == R.id.RBForest) return tdForest;
        if (id == R.id.RBCamp) return tdCamp;
        return null;
    }

    public void release() {
        shutdownEverything();
    }
}
