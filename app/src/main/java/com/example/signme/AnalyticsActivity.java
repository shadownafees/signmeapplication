package com.example.signme;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsActivity extends AppCompatActivity {

    private RelativeLayout chartLayout;
    private int[] data = {8, 10, 6, 5, 1, 4, 7, 3, 4, 6,4, 3, 5, 6, 3, 6, 2, 7, 1, 0}; // Sample data (number of times detected)
    private String[] labels = {
            "Left hand curve", "Right hand curve", "Left hairpin bend", "Right hairpin bend", "Steep ascent",
            "Left reverse bend", "Right reverse bend", "Dangerous dip", "Uneven road", "Pedestrian crossing",
            "Children Crossing", "Roundabout", "No entry", "No parking", "No stopping",
            "No overtaking", "Speed limit 60", "Speed limit 70", "Speed limit 80", "Speed limit 100"
    }; // Sample labels (traffic sign names)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        chartLayout = findViewById(R.id.chartLayout);
        drawBarChart();
    }

    private void drawBarChart() {
        chartLayout.addView(new BarChartView(this));
    }

    private class BarChartView extends View {

        private final int barColor = Color.rgb(9, 26, 119);
        private final int axisColor = Color.BLACK;
        private final int textColor = Color.BLACK;
        private final int numberColor = Color.WHITE; // Color for the numbers inside the bars
        private final int barHeight = 40; // Height of each bar
        private final int maxValue = 20; // Maximum value for x-axis
        private final int padding = 50; // Padding around the chart

        private final Paint barPaint;
        private final Paint axisPaint;
        private final Paint textPaint;
        private final Paint numberPaint;

        public BarChartView(AnalyticsActivity context) {
            super(context);

            barPaint = new Paint();
            barPaint.setColor(barColor);

            axisPaint = new Paint();
            axisPaint.setColor(axisColor);
            axisPaint.setStrokeWidth(5);

            textPaint = new Paint();
            textPaint.setColor(textColor);
            textPaint.setTextSize(30);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            numberPaint = new Paint();
            numberPaint.setColor(numberColor);
            numberPaint.setTextSize(30);
            numberPaint.setTextAlign(Paint.Align.CENTER); // Center align the numbers within the bars
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            // Draw x-axis
            canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint);

            // Draw y-axis
            canvas.drawLine(padding, padding, padding, height - padding, axisPaint);

            // Draw x-axis label
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Number of Detections", width / 2, height - padding / 4, textPaint);

            // Draw y-axis label
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.save();
            canvas.rotate(-90, padding / 4, height / 2);
            canvas.drawText("Traffic Signs", padding / 4, height / 2, textPaint);
            canvas.restore();

            // Draw bars and labels
            float barSpacing = (height - 2 * padding - barHeight * data.length) / (data.length + 1);
            for (int i = 0; i < data.length; i++) {
                float barWidth = (float) data[i] / maxValue * (width - 2 * padding);
                float left = padding;
                float top = padding + barSpacing * (i + 1) + barHeight * i;
                float right = padding + barWidth;
                float bottom = top + barHeight;

                // Draw bar
                canvas.drawRect(left, top, right, bottom, barPaint);

                // Draw label
                canvas.drawText(labels[i], right + 20, bottom - barHeight / 2 + 10, textPaint);

                // Draw number inside the bar
                float numberX = (left + right) / 2; // Center of the bar horizontally
                float numberY = top + barHeight / 2 + 10; // Center of the bar vertically, with some adjustment
                canvas.drawText(String.valueOf(data[i]), numberX, numberY, numberPaint);
            }
        }
    }
}
