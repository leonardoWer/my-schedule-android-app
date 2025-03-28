package com.example.myschedule.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.myschedule.R;

public class CircleProgressBar extends View {

    private static final float START_ANGLE_POINT = 270; // Начинаем сверху

    private Paint paint;
    private RectF rectF;
    private float strokeWidth = 12; // Толщина линии прогресса
    private int progressColor;
    private int emptyColor;
    private float progress = 0;
    private int maxProgress = 100;

    private Context context;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        rectF = new RectF();

        // Получаем цвета
        emptyColor = ContextCompat.getColor(context, R.color.light_gray);
        progressColor = ContextCompat.getColor(context, R.color.blue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = (float) getWidth() / 2;
        float centerY = (float) getHeight() / 2;
        float radius = Math.min(centerX, centerY) - strokeWidth / 2;

        // Draw empty circle
        paint.setColor(emptyColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth - 2);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw progress arc
        paint.setColor(progressColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rectF, START_ANGLE_POINT, (progress / maxProgress) * 360, false, paint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
}