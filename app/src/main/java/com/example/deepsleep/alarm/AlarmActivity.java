package com.example.deepsleep.alarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deepsleep.R;
import com.example.deepsleep.data.AlarmRepository;
import com.example.deepsleep.data.MyDatabase;
import com.example.deepsleep.databinding.ActivityAlarmBinding;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AlarmActivity extends AppCompatActivity {


    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private int previousStreamVolume;
    private ActivityAlarmBinding binding;
    private boolean alreadyStarted = false;

    private int correctAnswersInRow = 0;
    private final int GOAL = 3;

    private int correctPosition = -1;
    private int num1;
    private int num2;

    boolean morningTest;

    private AlarmViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlarmBinding.inflate(getLayoutInflater());

        MyDatabase myDatabase = MyDatabase.getInstance(this);
        AlarmRepository alarmRepository = new AlarmRepository(myDatabase.alarmDao(), MyDatabase.getInstanceExecutor());
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AlarmViewModel(alarmRepository);
            }
        };
        myViewModel = new ViewModelProvider(this, factory).get(AlarmViewModel.class);

        // delete notification if entered from locked screen
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(123);

        //mogla si i samo iz baze da citas da li je valid, ako nije valid znaci da si vec pustila muziku
        restorePreviousState(savedInstanceState);


        boolean vibration = getIntent().getBooleanExtra("com.example.deepsleep.VIBRATION", false);
        morningTest = getIntent().getBooleanExtra(getString(R.string.morning_test_extra), false);
        float volume = getIntent().getFloatExtra(getString(R.string.volume_name_extra), 50);
        long id = getIntent().getLongExtra(getString(R.string.id_alarm_extra), -1);

        Log.d("MIKA", "VIBRATION IN ALARM ACTIVITY IS: " + vibration);
        Log.d("MIKA", "MORNING TEST IN ALARM ACTIVITY IS: " + morningTest);
        Log.d("MIKA", "VOLUME IN ALARM ACTIVITY IS: " + volume);



        turnScreenOnAndKeyguardOff();
        setContentView(binding.getRoot());

        if (volume > 0 && !alreadyStarted) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            previousStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            int newVolume = (int) (streamMaxVolume * volume / 100);



            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);


            try {
                mediaPlayer = new MediaPlayer();
                myViewModel.setMediaPlayer(mediaPlayer);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                String song = "zvuk.mp3";
                String path = getFilesDir().getAbsolutePath() + File.separator + song;
                mediaPlayer.setDataSource(path);
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        if (vibration && !alreadyStarted){
            // mozda vibration attributes sa AudioAttributes.USAGE_ALARM treba da se doda
            vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            myViewModel.setVibrator(vibrator);
            long[] pattern = {1500, 800, 800, 800};
            VibrationEffect vibe = VibrationEffect.createWaveform(pattern, 0);
            vibrator.vibrate(vibe);
            // vibrator.vibrate(pattern, 0); ako je manje od nekog API
        }

        if (morningTest){
            if (!alreadyStarted) setNewTask();
            else setPreviousTask();

            binding.firstAnswer.setOnClickListener(view -> checkAnswer(0));
            binding.secondAnswer.setOnClickListener(view -> checkAnswer(1));
            binding.thirdAnswer.setOnClickListener(view -> checkAnswer(2));
        }
        else{
            binding.stop.setVisibility(View.VISIBLE);
            binding.stop.setOnClickListener(view -> stopAlarm());
        }

        myViewModel.updateValidityOfAlarm(false, id);

    }

    private void turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null);
        }
        else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }*/

    }



    public void setNewTask() {
        chooseNewValues();
        updateGUI();
    }

    public void setPreviousTask(){
        updateGUI();
    }

    public void chooseNewValues(){
        Random random = new Random();
        // treba li random set seed?
        num1 = 10 + random.nextInt(80); // 10 - 89
        num2 = 10 + random.nextInt(90 - num1); // 10 - 89

        correctPosition = random.nextInt(3);
    }


    public void updateGUI(){

        int sum = num1 + num2;
        binding.question.setText(num1 + " + " + num2 + " = ?");

        setAnswers(sum);

        binding.question.setVisibility(View.VISIBLE);
        binding.firstAnswer.setVisibility(View.VISIBLE);
        binding.secondAnswer.setVisibility(View.VISIBLE);
        binding.thirdAnswer.setVisibility(View.VISIBLE);
    }



    public void checkAnswer(int myPosition){

        if (myPosition != correctPosition){
            correctAnswersInRow = 0;
            setNewTask();
            showToast(getString(R.string.msg_wrong_answer));
        }
        else{
            correctAnswersInRow ++ ;
            if (correctAnswersInRow == GOAL){
                showToast(getString(R.string.msg_finished));
                stopAlarm();
            }
            else {
                setNewTask();
                if (correctAnswersInRow == 1) showToast(getString(R.string.msg_two_more));
                else showToast(getString(R.string.msg_one_more));

            }


        }
    }

    public void stopAlarm(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (vibrator != null){
            vibrator.cancel();
        }

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousStreamVolume, AudioManager.FLAG_PLAY_SOUND);


        Handler handler = new Handler();
        final Runnable r = this::finishAndRemoveTask;

        if (morningTest){
            handler.postDelayed(r, 1000);
        }
        else {
            handler.postDelayed(r, 300);
        }

    }

    @Override
    public void onBackPressed() {
        // nothing
    }

    public void showToast(String msg){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.simple_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void setAnswers(int sum){
        switch (correctPosition){
            case 0:
                binding.firstAnswer.setText("" + sum);
                binding.secondAnswer.setText("" + (sum + 10));
                binding.thirdAnswer.setText("" + (sum - 1));
                break;
            case 1:
                binding.secondAnswer.setText("" + sum);
                binding.firstAnswer.setText("" + (sum - 10));
                binding.thirdAnswer.setText("" + (sum + 1));
                break;
            case 2:
                binding.thirdAnswer.setText("" + sum);
                binding.firstAnswer.setText("" + (sum + 10));
                binding.secondAnswer.setText("" + (sum + 1));
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("NUM1", num1);
        outState.putInt("NUM2", num2);
        outState.putInt("CORRECT_POSITION", correctPosition);
        outState.putInt("CORRECT_IN_ROW", correctAnswersInRow);
        outState.putInt("PREV_ALARM_VOLUME", previousStreamVolume);

    }


    public void restorePreviousState(Bundle savedInstanceState){
        if (savedInstanceState != null){
            alreadyStarted = true;

            num1 = savedInstanceState.getInt("NUM1", 0);
            num2 = savedInstanceState.getInt("NUM2", 0);
            correctPosition = savedInstanceState.getInt("CORRECT_POSITION", -1);
            correctAnswersInRow = savedInstanceState.getInt("CORRECT_IN_ROW", 0);
            previousStreamVolume = savedInstanceState.getInt("PREV_ALARM_VOLUME", 7);
            mediaPlayer = myViewModel.getMediaPlayer();
            vibrator = myViewModel.getVibrator();
        }

    }
}