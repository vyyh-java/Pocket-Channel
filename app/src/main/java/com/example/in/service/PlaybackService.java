package com.example.in.service;


import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.session.SessionError;
import androidx.media3.session.SessionResult;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class PlaybackService extends MediaSessionService {

    private CountDownTimer timer = null;

    private MediaSession mediaSession = null;
    private ExoPlayer player = null;

    @Override
    public void onCreate() {
        super.onCreate();
        player = new ExoPlayer.Builder(this).build();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();
        player.setAudioAttributes(audioAttributes, true);
        MediaSession.Callback sessionCallback = new MediaSession.Callback() {
            @OptIn(markerClass = UnstableApi.class)
            @NonNull
            @Override
            public MediaSession.ConnectionResult onConnect(@NonNull MediaSession session, @NonNull MediaSession.ControllerInfo controllerInfo) {
                MediaSession.ConnectionResult.AcceptedResultBuilder connectionResultBuilder = new MediaSession.ConnectionResult.AcceptedResultBuilder(session);
                SessionCommands sessionCommands = new SessionCommands.Builder()
                        .add(new SessionCommand("COMMAND_START_TIMER", Bundle.EMPTY))
                        .add(new SessionCommand("COMMAND_STOP_TIMER", Bundle.EMPTY))
                        .build();
                return connectionResultBuilder
                        .setAvailableSessionCommands(sessionCommands)
                        .build();
            }

            @OptIn(markerClass = UnstableApi.class)
            @NonNull
            @Override
            public ListenableFuture<SessionResult> onCustomCommand(
                    @NonNull MediaSession session,
                    @NonNull MediaSession.ControllerInfo controllerInfo,
                    @NonNull SessionCommand command,
                    @NonNull Bundle args) {

                if ("COMMAND_START_TIMER".equals(command.customAction)) {
                    long totalMillis = args.getLong("KEY_TOTAL_TIME", 0L);
                    startBackendTimer(totalMillis);
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
                } else if ("COMMAND_STOP_TIMER".equals(command.customAction)) {
                    stopBackendTimer();
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
                }
                return Futures.immediateFuture(new SessionResult(SessionError.ERROR_UNKNOWN));
            }
        };
        mediaSession = new MediaSession.Builder(this, player)
                .setCallback(sessionCallback)
                .build();
    }
    private void stopBackendTimer(){
        if(timer != null){
            timer.cancel();
        }
        if(player != null){
            player.pause();
        }
    }

    private void startBackendTimer(long totalMillis){
        totalMillis = Math.max(totalMillis, 0);
        if(timer != null){
            timer.cancel();
        }
        timer = new CountDownTimer(totalMillis, 1000) {
            @Override
            public void onFinish() {
                if(player != null){
                    player.stop();
                }
                if (mediaSession != null) {
                    SessionCommand updateCmd = new SessionCommand("COMMAND_UPDATE_TIMER_UI", Bundle.EMPTY);
                    Bundle args = new Bundle();
                    args.putLong("KEY_REMAINING_TIME", 0L);
                    mediaSession.broadcastCustomCommand(updateCmd, args);
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (mediaSession != null) {
                    SessionCommand updateCmd = new SessionCommand("COMMAND_UPDATE_TIMER_UI", Bundle.EMPTY);
                    Bundle args = new Bundle();
                    args.putLong("KEY_REMAINING_TIME", millisUntilFinished);
                    mediaSession.broadcastCustomCommand(updateCmd, args);
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaSession != null) {
            Player player = mediaSession.getPlayer();
            mediaSession.release();
            mediaSession = null;
            player.release();
        }
    }
}

