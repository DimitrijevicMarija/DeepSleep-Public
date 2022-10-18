package com.example.deepsleep.statistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.deepsleep.R;
import com.example.deepsleep.data.Sleep;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SleepingLine extends View {

    private Paint linePaint = null;
    private Paint sleepPaint = null;
    private Paint textPaint = null;

    private final int lineWidth = 50;
    private final int halfLineWidth = lineWidth / 2;
    private final int textSize = 30;
    private final int textPosition = lineWidth * 2 + textSize;
    private final int yLine = halfLineWidth + textSize + 5;


    private List<Sleep> sleepList = new ArrayList<>();

    private int startLine;
    private int endLine;

    private Calendar calendar;
    private int durationShown = 24;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private int prevTextPosition = -1000;
    private int currTextY = textPosition;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public SleepingLine(Context context) {
        super(context);
    }

    public SleepingLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCustomView();
    }

    public SleepingLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomView();
    }

    public SleepingLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCustomView();
    }

    public void setSleepList(List<Sleep> sleepList) {
        this.sleepList = sleepList;
        if (sleepList.size() > 0){
            Sleep sleep = sleepList.get(0);
            // istestiraj ovo
            if (sleep.getStart() * 1000 < calendar.getTimeInMillis()){

                calendar.setTimeInMillis(sleep.getStart() * 1000);

                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                durationShown += 21 - calendar.get(Calendar.HOUR_OF_DAY);

            }

        }



        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        startLine = halfLineWidth;
        endLine = getWidth() - halfLineWidth;
        canvas.drawLine(startLine, yLine, endLine, yLine, linePaint);

        drawTimeMarks(canvas);

        prevTextPosition = -1000;
        currTextY = textPosition;


        for (Sleep sleep: sleepList){
            showOneSleep(canvas, sleep);
        }


        super.onDraw(canvas);
    }

    void initCustomView() {

        linePaint = new Paint();
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(ResourcesCompat.getColor(
                getResources(),
                R.color.grey,
                null));


        sleepPaint = new Paint();
        sleepPaint.setStrokeWidth(lineWidth);
        //sleepPaint.setStrokeCap(Paint.Cap.ROUND);
        sleepPaint.setColor(ResourcesCompat.getColor(
                getResources(),
                R.color.light_blue,
                null));

        textPaint = new Paint();
        textPaint.setStrokeWidth(5);
        textPaint.setTextSize(textSize);
        textPaint.setColor(ResourcesCompat.getColor(
                getResources(),
                R.color.white,
                null));

    }

    private void showOneSleep(Canvas canvas, Sleep sleep){

        int lineLength = endLine - startLine;
        double duration = 1.0 * sleep.getDuration() / 60 / 60;

        int sleepLineWidth = (int) (duration * lineLength / durationShown );


        long diff = sleep.getStart() * 1000 - calendar.getTimeInMillis();
        int position = (int) (1.0 * diff / (durationShown * 60 * 60 * 1000)* lineLength) + startLine;


        String timeString = getTimeString(sleep);

        // oko 160 je duzina vremenskog stringa
        canvas.drawLine(position, yLine, position + sleepLineWidth, yLine, sleepPaint);

        int textX = position + sleepLineWidth/2 - 80;
        if (textX > endLine - 160) textX = endLine - 160;
        if (textX < 0) textX = 0;

        if (textX - prevTextPosition < 160) {
            currTextY += 30;
            canvas.drawText(timeString, textX, currTextY, textPaint);
        }
        else {
            canvas.drawText(timeString, textX , textPosition, textPaint);
            prevTextPosition = textX;
            currTextY = textPosition;
        }



    }

    private String getTimeString(Sleep sleep){
        long start = sleep.getStart();
        long end = sleep.getEnd();

        ZonedDateTime startTime = Instant.ofEpochSecond(start).atZone( ZoneId.of("Europe/Belgrade"));
        ZonedDateTime endTime = Instant.ofEpochSecond(end).atZone( ZoneId.of("Europe/Belgrade"));

        return startTime.format(formatter) + " - " + endTime.format(formatter);
    }

    private void drawTimeMarks(Canvas canvas){
        canvas.drawText(calendar.get(Calendar.HOUR_OF_DAY) + ":00", 0, textSize, textPaint);
        canvas.drawText("21:00", endLine - 50, textSize, textPaint);

        int lineLength = endLine - startLine;

        // 00:00
        int start00 = (int) (1.0 * (durationShown - 24 + 3) / durationShown * lineLength + startLine);
        canvas.drawText("00:00", start00 - 25, textSize, textPaint);

        // 08:00
        int start0800 = (int) (1.0 * (durationShown - 24 + 11) / durationShown * lineLength + startLine);
        canvas.drawText("08:00", start0800 - 25, textSize, textPaint);

        // 15:00
        int start1500 = (int) (1.0 * (durationShown - 24 + 18) / durationShown * lineLength + startLine);
        canvas.drawText("15:00", start1500 - 25, textSize, textPaint);



    }
}
