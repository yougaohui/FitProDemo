package xfkj.fitpro.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import xfkj.fitpro.R;

public class MyHeartView extends View {
    private String startColor = "#33d24e";//起始颜色//#31D34E
    private String centerColor1 = "#f8e71c";//起始颜色//#31D34E
    private String centerColor2 = "#ff9500";//起始颜色//#31D34E
    private String endColor = "#ff4e65";//结束颜色//#FBCB16
    private SweepGradient gradientColors;//渐变颜色
    private int maxProgress = 200;//最大进度值
    private String tips = "bpm";
    private Bitmap mBitmap;
    // 正方形的宽高
    private int len;

    /**
     * 当前进度
     */
    private int progress = 0;
    /**
     * view的实际宽度
     */
    private int viewWidth;
    /**
     * view的实际高度
     */
    private int viewHeight;

    /**
     * 内环的半径
     */
    private int inCircleRedius = 0;
    /**
     * 内环的宽度
     */
    private int inCircleWidth = 60;

    /**
     * 内容中心的坐标
     */
    private int[] centerPoint = new int[2];


    /**
     * 圆弧开始的角度
     */
    private int startAngle = 0;
    /**
     * 圆弧划过的角度
     */
    private int allAngle = 0;

    private Paint mPaint;
    /**
     * 刻度盘上数字的数量
     */
    private int figureCount = 5;


    public MyHeartView(Context context) {
        this(context, null);
    }

    public MyHeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyHeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        initValues();
    }

    /**
     * 初始化尺寸
     */
    private void initValues() {
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.measure_hr_icon);
        // 取出最小值
        len = Math.min(viewWidth, viewHeight);
        centerPoint[0] = viewWidth / 2;
        centerPoint[1] = (viewHeight / 2)+140;
        inCircleRedius = len/2;
        startAngle = 180;
        allAngle = 180;
        mBitmap = imageScale(mBitmap,68,66);
    }

    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStatic(canvas);
        drawDynamic(canvas);
    }

    /**
     * 绘制静态的部分
     *
     * @param canvas
     */
    private void drawStatic(Canvas canvas) {
    //    drawCircleWithRound(startAngle, allAngle, inCircleWidth, inCircleRedius, color_outcircle, canvas);
        drawFigure(canvas, figureCount);
    }

    private void drawFigure(Canvas canvas, int count) {
        int figure = 0;
        int angle;

        init();
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF rectF = new RectF(centerPoint[0]-inCircleRedius-inCircleWidth, centerPoint[1]-inCircleRedius-inCircleWidth, centerPoint[0]+inCircleRedius+inCircleWidth , centerPoint[1]+inCircleRedius+inCircleWidth);
        canvas.drawArc(rectF, startAngle, allAngle, false, mPaint);

        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < count; i++) {
            figure = (int) (200 / (1f * count-1) * i);
            angle = (int) ((allAngle) / ((count-1) * 1f) * i) + startAngle;
            int[] pointFromAngleAndRadius = getPointFromAngleAndRadius(angle, inCircleRedius+90 );
            mPaint.setTextSize(22);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.save();
            canvas.drawText(figure+"",pointFromAngleAndRadius[0],pointFromAngleAndRadius[1],mPaint);
            canvas.restore();
        }
    }


    /**
     * 绘制动态的部分
     *
     * @param canvas
     */
    private void drawDynamic(Canvas canvas) {
        drawCurrentProgressTv(progress, canvas);
        drawProgress(progress, canvas);
    }

    /**
     * 绘制当前进度是文字
     *
     * @param progress
     * @param canvas
     */
    private void drawCurrentProgressTv(int progress, Canvas canvas) {
        mPaint.setStrokeWidth(8f);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setTextAlign(Paint.Align.CENTER);
    //    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
     //   float baseLine2 = 1 - 3 * (fontMetrics.bottom + fontMetrics.top) + centerPoint[1];
        mPaint.setTextSize(45);
        canvas.drawText(tips+"", centerPoint[0], centerPoint[1]+10, mPaint);
        mPaint.setTextSize(120);
        Typeface mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/IMPACT_0.TTF");
        mPaint.setTypeface(mFace);
        canvas.drawText(progress + "", centerPoint[0], centerPoint[1]-50, mPaint);
        canvas.drawBitmap(mBitmap, centerPoint[0]-(mBitmap.getWidth()/2), centerPoint[1]-240, null);
    }


    /**
     * 根据进度画进度条
     *
     * @param progress 最大进度为100.最小为0
     */
    private void drawProgress(int progress, Canvas canvas) {
        float ratio = progress / 200f;
        int angle = (int) (allAngle * ratio);
        drawCircleWithRound(startAngle, angle, inCircleWidth, inCircleRedius, canvas,"progress");
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress,String text) {
        init();
        if (progress>200){
            progress = 200;
        }
        this.progress = progress;
        this.tips = text;
        invalidate();
    }
    /**
     * 画一个两端为圆弧的圆形曲线
     *
     * @param startAngle 曲线开始的角度
     * @param allAngle   曲线走过的角度
     * @param radius     曲线的半径
     * @param width      曲线的厚度
     */
    @SuppressLint("ResourceAsColor")
    private void drawCircleWithRound(int startAngle, int allAngle, int width, int radius, Canvas canvas, String circleType) {
        mPaint.setStrokeWidth(width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#00f0ff"));
        /*
        gradientColors = null;
        mPaint.setShader(gradientColors);
        if(circleType == "progress" && progress>0){
            int[] colors = new int[]{Color.parseColor(startColor), Color.parseColor(centerColor1),Color.parseColor(centerColor2),Color.parseColor(endColor)};
            LinearGradient shader = new LinearGradient(0, 0, 500 , 0, colors, null,  Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
        }*/
        RectF rectF = new RectF(centerPoint[0] - radius+10, centerPoint[1] - radius+10, centerPoint[0] + radius-10, centerPoint[1] + radius-10);
        canvas.drawArc(rectF, startAngle, allAngle, false, mPaint);
    }
	
	 /**
     * 颜色渐变
     *
     * @return
     */
    private SweepGradient gradientColors()
    {
        if (gradientColors == null)
        {

            int[] colors = new int[]{Color.parseColor(startColor), Color.parseColor(centerColor1),Color.parseColor(centerColor2),Color.parseColor(endColor),Color.parseColor(startColor), Color.parseColor(centerColor1),Color.parseColor(centerColor2),Color.parseColor(endColor)};
            gradientColors = new SweepGradient(centerPoint[0], centerPoint[1],colors , null);
            Matrix matrix = new Matrix();
            matrix.setRotate(startAngle, centerPoint[0], centerPoint[1]);
            gradientColors.setLocalMatrix(matrix);
        }
        return gradientColors;
    }


    /**
     * 根据角度和半径，求一个点的坐标
     *
     * @param angle
     * @param radius
     * @return
     */
    private int[] getPointFromAngleAndRadius(int angle, int radius) {
        double x = radius * Math.cos(angle * Math.PI / 180) + centerPoint[0];
        double y = radius * Math.sin(angle * Math.PI / 180) + centerPoint[1];
        return new int[]{(int) x, (int) y};
    }

}