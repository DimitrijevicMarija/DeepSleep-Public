package com.example.deepsleep.alarm;

import android.media.MediaPlayer;
import android.os.Vibrator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.deepsleep.data.Alarm;
import com.example.deepsleep.data.AlarmRepository;

import java.util.List;

public class AlarmViewModel extends ViewModel {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private final AlarmRepository alarmRepository;

    private int savedMinute = -1;
    private int savedHour = -1;
    private boolean vibrationChecked = false;
    private boolean morningTestChecked = false;
    private float volumeSliderValue = 50;


    public AlarmViewModel(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public LiveData<List<Alarm>> getAlarmList() {
        return alarmRepository.getAllLiveData();
    }

    public long insert(Alarm alarm){
         return alarmRepository.insert(alarm);
    }
    public void update(Alarm alarm){
        alarmRepository.update(alarm);
    }
    public void delete(Alarm alarm){
        alarmRepository.delete(alarm);
    }



    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Vibrator getVibrator() {
        return vibrator;
    }

    public void setVibrator(Vibrator vibrator) {
        this.vibrator = vibrator;
    }

    public void setHourAndMinute(int hour, int minute){
        savedHour = hour;
        savedMinute = minute;
    }

    public int getSavedMinute() {
        return savedMinute;
    }

    public int getSavedHour() {
        return savedHour;
    }

    public void updateValidityOfAlarm(boolean valid, long id){
        alarmRepository.updateValidityOfAlarm(valid, id);
    }

    public List<Alarm> getAlarmsForReboot(long now){
        return alarmRepository.getAlarmsForReboot(now);
    }

    public boolean isVibrationChecked() {
        return vibrationChecked;
    }

    public void setVibrationChecked(boolean vibrationChecked) {
        this.vibrationChecked = vibrationChecked;
    }

    public boolean isMorningTestChecked() {
        return morningTestChecked;
    }

    public void setMorningTestChecked(boolean morningTestChecked) {
        this.morningTestChecked = morningTestChecked;
    }

    public float getVolumeSliderValue() {
        return volumeSliderValue;
    }

    public void setVolumeSliderValue(float volumeSliderValue) {
        this.volumeSliderValue = volumeSliderValue;
    }

}
