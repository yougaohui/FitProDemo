package xfkj.fitpro.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class CircleProgressView extends View {

    private int len;
    private RectF oval;
    //起始角度
    private float startAngle=0;
    //经过角度
    private float sweepAngle=360;
    // 刻度经过角度范围
    private float targetAngle = 360;
    private Paint paint;
    private Paint paint2;
    private float radius;
    private float border = 14;

    // 绘制文字
    private Paint textPaint;
    // 画水球的画笔
 //   private Paint waterPaint;
    boolean useCenter = false;

    private String showText = "";


    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    /**
     *1  初始化画笔对象
     */
    private void initPaint() {

        paint =new Paint();
        //设置画笔颜色
        paint.setColor(Color.WHITE);
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        //让画出的图形是空心的(不填充)
        paint.setStyle(Paint.Style.STROKE);

        //文本画笔
        textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setAntiAlias(true);

        paint2 =new Paint();
        //设置画笔颜色
        paint2.setColor(Color.parseColor("#F8F8F8"));
        //设置画笔抗锯齿
        paint2.setAntiAlias(true);
        //让画出的图形是空心的(不填充)
        paint2.setStyle(Paint.Style.FILL);



        //水波纹画笔
    //    waterPaint = new Paint();
        //waterPaint.setAntiAlias(true);
    }

    /**
     *2  来测量限制view为正方形
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //以最小值为刻度区域正方形的长
        len = Math.min(width, height);
        //确定圆弧所在的矩形区域
        oval = new RectF(border/2, border/2, len-(border/2), len-(border/2));
        radius = len/2;
        //设置测量高度和宽度
        setMeasuredDimension(len,len);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 3 绘制圆弧
        // oval是一个RectF对象为一个矩形
        // startAngle为圆弧的起始角度
        // sweepAngle为圆弧的经过角度（扫过角度）
        // useCenter为圆弧是一个boolean值，为true时画的是圆弧，为false时画的是割弧
        // paint为一个画笔对象
        paint.setStrokeWidth(border);
        canvas.drawArc(oval,startAngle,sweepAngle,useCenter,paint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius-border, paint2);

        // 4 画刻度线
        drawViewLine(canvas);
        drawText(canvas);
    }

    /**
     * 实现画刻度线内的内容
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        // 设置文本对齐方式，居中对齐
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(radius/3);
        textPaint.setColor(Color.BLACK);
        // 画分数
        canvas.drawText(showText, radius, radius+8, textPaint);
    }


    /**
     * 绘制刻度线
     * @param canvas
     */
    private void drawViewLine(Canvas canvas) {
        canvas.save();
        //移动canvas
        canvas.translate(radius,radius);
        //旋转canvas
        canvas.rotate(180);
        //普通刻度
        Paint linePatin=new Paint();
        //设置普通刻度画笔颜色
        linePatin.setColor(Color.BLACK);
        //线宽
        linePatin.setStrokeWidth(1);
        //设置画笔抗锯齿
        linePatin.setAntiAlias(true);
       /* //画一条刻度线
        canvas.drawLine(0,radius,0,radius-40,linePatin);*/
        //画101条刻度线
        //确定每次旋转的角度
        float rotateAngle=sweepAngle/99;
        //绘制需要有颜色部分的画笔
     //   Paint targetLinePatin=new Paint();
     //   targetLinePatin.setColor(Color.parseColor("#F76B1C"));
    //    targetLinePatin.setStrokeWidth(1);
    //    targetLinePatin.setAntiAlias(true);
        //记录已经绘制过的有色部分范围(角度float)
        float hasDraw=0;

        for(int i=0;i<100;i++){
          /*  //画一条刻度线
            canvas.drawLine(0,radius,0,radius-40,linePatin);*/
            //有色和无色分别用不同颜色
            if (hasDraw <= targetAngle && targetAngle != 0) {
            //    canvas.drawLine(0, radius, 0, radius-13, targetLinePatin);
                linePatin.setColor(Color.WHITE);
            } else {
                canvas.drawLine(0,radius-2,0,radius-border,linePatin);
            }
            hasDraw += rotateAngle;
            canvas.rotate(rotateAngle);
        }
        //操作完成后恢复状态
        canvas.restore();
    }

    public void setProgress(int progress,String showText){
        this.showText = showText;
        targetAngle = (float) progress/100*360;
        paint.setColor(Color.WHITE);
        if (targetAngle > 0.1) {
            paint.setColor(Color.parseColor("#F76B1C"));
        }
        postInvalidate();
    }

}