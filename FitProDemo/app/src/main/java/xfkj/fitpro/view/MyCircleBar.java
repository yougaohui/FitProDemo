package xfkj.fitpro.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.DecimalFormat;

import xfkj.fitpro.R;


public class MyCircleBar extends View
{
    /**
     * 属性常量
     */
    private static final int INIT_270_ANGLE = 270;//270度
    private static final int INIT_360_ANGLE = 360;//360度
    private static final int START_POINT_270 = 135;//圆环起始点角度
    private static final int START_POINT_360 = 270;//圆环起始点角度
    private static final int DEFAULT_CIRCLE_SIDE = 200;//控件而的默认边长
    private static final int DEFAULT_ANGLE_TYPE = 2;//默认类型-->360
    private static final int CHANGE_ANGLE_TYPE = 1;//默认类型-->270
    private static final int DEFAULT_RING_WIDTH = 20;//默认宽度
    private static final int DEFAULT_RING_UN_REACHED_COLOR = 0xffffffff;//默认颜色
    private static final int DEFAULT_RING_REACHED_COLOR = 0xffabed4d;//默认进度颜色
    private static final int DEFAULT_PROGRESS = 0;//默认进度是0
    private static final int DEFAULT_MAX_PROGRESS = 6000;//默认最大进度值
    private static final boolean DEFAULT_GRADIENT_ON = false;//默认是关闭的
    private static final int DEFAULT_CIRCLE_START_COLOR = 0xff00ff00;//绿色
    private static final int DEFAULT_CIRCLE_CENTER_COLOR = 0xffffff00;//黄色
    private static final int DEFAULT_CIRCLE_END_COLOR = 0xffff0000;//红色
    private static final int DEFAULT_HINT_TEXT_COLOR = 0xffffffff;//黑色
    private static final int DEFAULT_SHOW_TEXT_COLOR = 0xffffffff;//黑色
    private static final int DEFAULT_DRAW_SCALE_COLOR = 0xffff00ff;//紫红色
    private static final boolean DEFAULT_DRAW_SCALE_ON = false;//默认不绘制刻度
    /**
     * 绘制变量
     */
    private int circle_diameter;//圆环的直径
    private int circle_type;//圆环类型
    private int circle_width;//宽度
    private int unReachedColor;//颜色
    private int reachedColor;//颜色
    private int drawArcStartAngle;//绘制圆弧的起始点
    private int drawMaxValues;//绘制的最大值
    private Paint unReachedPaint, reachedPaint,smallCircleSolidePaint,smallCirclePaint;//画笔
    private RectF drawArcRect;//绘制弧形的区域
    private int drawCircleRadius;//实际绘制半径
    private int drawOffset;//绘制的偏移量
    private int circlePointX;//中心X
    private int circlePointY;//中心Y
    private int nowProgress;//当前进度
    private int maxProgress;//最大进度值
    private boolean drawSingleColor;//是否绘制单一的颜色
    private int startColor;//起始颜色
    private int centerColor;//中间颜色
    private int endColor;//结束颜色
    private SweepGradient gradientColors;//渐变颜色
    private PaintFlagsDrawFilter mDrawFilter;//图形抗锯齿
    private BarAnimation circleAnimation;//进度动画
    private float unit;//角度值
    private Paint drawHintText;//绘制提示
    private Paint drawShowText;//绘制文字
    private RectF drawTextRect;//绘制文字的区域
    private int hintTextColor;//提示文字颜色
    private int showTextColor;//显示文字颜色
    private int values;//显示的值
    private String showText;//显示提示的文字
    private int drawScaleHeightStartPoint;//绘制刻度的起点
    private Paint drawScalePaint;//绘制刻度
    private int drawScaleColor;//绘制刻度的颜色
    private boolean isShowScale;//是否显示刻度
    private Bitmap progressBitmap = null;

    /**
     * 构造
     *
     * @param context
     */
    public MyCircleBar(Context context)
    {
        this(context, null);
    }

    public MyCircleBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MyCircleBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        obtainAttributes(attrs);//获取自定义属性
        init();//初始化
        initPaint();//初始化画笔
    }

    /**
     * 初始化
     */
    private void init()
    {
        drawArcStartAngle = circle_type == DEFAULT_ANGLE_TYPE ? START_POINT_360 : START_POINT_270;//绘制圆弧的开始角度
        drawMaxValues = circle_type == DEFAULT_ANGLE_TYPE ? INIT_360_ANGLE : INIT_270_ANGLE;//绘制出的图形
        drawArcRect = new RectF();//创建这个对象
        drawOffset = (circle_width >> 1) + dip2px(2);//为了美观
        drawScaleHeightStartPoint = circle_width + dip2px(2);
        circleAnimation = new BarAnimation();
        drawTextRect = new RectF();
    //    progressBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.step_progress);
    //    progressBitmap = setImgSize(progressBitmap,30,30);
    }

    public Bitmap setImgSize(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 初始化画笔
     */
    private void initPaint()
    {
        //绘制默认圆环
        unReachedPaint = new Paint();
        unReachedPaint.setAntiAlias(true);
        unReachedPaint.setDither(true);
        unReachedPaint.setStrokeWidth(circle_width);
        unReachedPaint.setColor(unReachedColor);
        unReachedPaint.setStyle(Paint.Style.STROKE);
        unReachedPaint.setStrokeCap(Paint.Cap.ROUND);
        //绘制进度
        reachedPaint = new Paint();
        reachedPaint.setAntiAlias(true);
        reachedPaint.setDither(true);
        reachedPaint.setStrokeWidth(circle_width);
        reachedPaint.setStyle(Paint.Style.STROKE);
        reachedPaint.setStrokeCap(Paint.Cap.ROUND);
        //绘制提示文字
        drawHintText = new Paint();
        drawHintText.setAntiAlias(true);
        drawHintText.setColor(hintTextColor);
        drawHintText.setTextAlign(Paint.Align.CENTER);
        drawHintText.setStyle(Paint.Style.FILL);
        //绘制显示文字
        drawShowText = new Paint();
        drawShowText.setAntiAlias(true);
        drawShowText.setColor(showTextColor);
        drawShowText.setTextAlign(Paint.Align.CENTER);
        drawShowText.setStyle(Paint.Style.FILL);
        //绘制刻度
        drawScalePaint = new Paint();
        drawScalePaint.setAntiAlias(true);
        drawScalePaint.setDither(true);
        drawScalePaint.setColor(drawScaleColor);
        //设置抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //绘制进度图标圆圈
        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setDither(true);
        smallCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint.setStrokeWidth(1);
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //绘制圆弧的区域
        drawArcRect.left = drawOffset;
        drawArcRect.top = drawOffset;
        drawArcRect.right = circle_diameter - drawOffset;
        drawArcRect.bottom = circle_diameter - drawOffset;
        //实际绘制的半径长度
        drawCircleRadius = (circle_diameter >> 1) - drawOffset;
        //绘制文字的区域
        int chord_length = (int) Math.sqrt(Math.pow(circle_diameter, 2)
                + Math.pow(circle_diameter, 2));//获取矩形的对角线长度
        drawTextRect.left = (int) ((chord_length / 2 - drawCircleRadius) * Math.sin(45));
        drawTextRect.top = drawTextRect.left;
        drawTextRect.right = circle_diameter - drawTextRect.left;
        drawTextRect.bottom = circle_diameter - drawTextRect.top;
        int placeValues = (int) (drawTextRect.bottom - drawTextRect.top) / 5;
        //绘制文本
        drawHintText.setTextSize(placeValues / 2 + 5);
        drawHintText.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("目标:" + showText, circle_diameter / 2, placeValues * 5 + drawTextRect.top, drawHintText);

        drawShowText.setTextSize(placeValues * 2);
        drawShowText.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(values + "", circle_diameter / 2, (float) (placeValues / 2) * 5 + 20 + drawTextRect.top, drawShowText);
        //中心点
        circlePointX = circle_diameter >> 1;
        circlePointY = circlePointX;
        //绘制默认圆环
        switch (circle_type)
        {
            case DEFAULT_ANGLE_TYPE://绘制圆形
                canvas.drawCircle(circlePointX, circlePointY, drawCircleRadius, unReachedPaint);
                break;
            case CHANGE_ANGLE_TYPE://绘制弧形
                canvas.drawArc(drawArcRect, drawArcStartAngle, drawMaxValues, false, unReachedPaint);
                break;
        }



        //绘制进度
        if (drawSingleColor)
        {
            reachedPaint.setShader(gradientColors());
        } else
        {
            reachedPaint.setColor(reachedColor);
        }
        if (values <= maxProgress)
        {
            //当设置进度小于最大进度就进行绘制
            canvas.drawArc(drawArcRect, drawArcStartAngle,
                    unit == 0 ? (float) nowProgress / (float) maxProgress * drawMaxValues : unit,
                    false, reachedPaint);
        } else
        {
            canvas.drawArc(drawArcRect, drawArcStartAngle, drawMaxValues, false, reachedPaint);
        }

        if(progressBitmap != null && this.nowProgress>0){
            float unit2 = drawMaxValues;
            if (values<=this.maxProgress){
                unit2 = unit == 0 ?(float) nowProgress / (float) maxProgress * drawMaxValues: unit;
            }
            float hudu = (float) Math.abs(Math.PI * unit2 / 180);//Math.abs：绝对值 ，Math.PI：表示π ， 弧度 = 度*π / 180
            float smallCircleX = (float) Math.abs(Math.sin(hudu) * drawCircleRadius + drawCircleRadius+24);
            float smallCircleY = (float) Math.abs(drawCircleRadius+24 -Math.cos(hudu) * drawCircleRadius);
            canvas.drawCircle(smallCircleX, smallCircleY, 20, smallCirclePaint);
            float bitmapWidth = progressBitmap.getWidth()/2;
            canvas.drawBitmap(progressBitmap,smallCircleX-bitmapWidth,smallCircleY-bitmapWidth,smallCirclePaint);
        }

        //绘制刻度
        if (isShowScale)
        {
            int scaleLength = (int) (drawTextRect.top - drawScaleHeightStartPoint) / 2;//刻度的长度
            float scaleValues = drawMaxValues * 1.0f / 100;
            canvas.save();
            int drawCounts;
            if (circle_type == DEFAULT_ANGLE_TYPE)
            {
                canvas.rotate(-180, circlePointX, circlePointY);
                drawCounts = 100;
            } else
            {
                canvas.rotate(-135, circlePointX, circlePointY);
                drawCounts = 101;
            }
            canvas.translate(circlePointX, drawScaleHeightStartPoint);
            for (int i = 0; i < drawCounts; i++)
            {
                if (i % 10 == 0)
                {
                    canvas.drawLine(0, 0, 0, scaleLength, drawScalePaint);
                } else
                {
                    canvas.drawLine(0, 0, 0, scaleLength / 2, drawScalePaint);
                }
                canvas.rotate(scaleValues, 0, circlePointY - drawScaleHeightStartPoint);
            }
            canvas.restore();
        }
        canvas.setDrawFilter(mDrawFilter);
    }

    /**
     * 格式化
     *
     * @param values
     * @return
     */
    private String getStringValues(double values)
    {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(values);
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
            switch (circle_type)
            {
                case DEFAULT_ANGLE_TYPE:
                    gradientColors = new SweepGradient(circlePointX, circlePointY,
                            new int[]{startColor, centerColor, endColor, centerColor, startColor}, null);
                    break;
                case CHANGE_ANGLE_TYPE:
                    gradientColors = new SweepGradient(circlePointX, circlePointY,
                            new int[]{startColor, centerColor, endColor, startColor}, null);
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.setRotate(drawArcStartAngle, circlePointX, circlePointY);
            gradientColors.setLocalMatrix(matrix);
        }
        return gradientColors;
    }

    /**
     * 设置进度值
     *
     * @param progress
     */
    public void updateProgress(int progress)
    {
        unit = (float) nowProgress / (float) maxProgress * drawMaxValues;
        this.nowProgress = progress;
        values = nowProgress;
        invalidate();
    }

    public int getProgress()
    {
        return this.nowProgress;
    }

    /**
     * 显示进度
     *
     * @param progress
     * @param time
     */
    public void showProgress(int progress, long time)
    {
        this.nowProgress = progress;
        circleAnimation.setDuration(time);
        circleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        startAnimation(circleAnimation);
    }

    /**
     * 设置最大进度值
     *
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress)
    {
        this.maxProgress = maxProgress;
        this.showText = maxProgress + "";
        showText = maxProgress + "";
    }

    /**
     * 获取自定义属性
     *
     * @param attrs
     */
    private void obtainAttributes(AttributeSet attrs)
    {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyCircleBar);
        circle_type = typedArray.getInt(R.styleable.MyCircleBar_circle_ring_angle_type, DEFAULT_ANGLE_TYPE);
        circle_width = dip2px((int) typedArray.getDimension(R.styleable.MyCircleBar_circle_ring_width, DEFAULT_RING_WIDTH));
        unReachedColor = typedArray.getColor(R.styleable.MyCircleBar_circle_ring_un_reached, DEFAULT_RING_UN_REACHED_COLOR);
        reachedColor = typedArray.getColor(R.styleable.MyCircleBar_circle_ring_reached, DEFAULT_RING_REACHED_COLOR);
        nowProgress = typedArray.getInt(R.styleable.MyCircleBar_circle_show_progress, DEFAULT_PROGRESS);
        maxProgress = typedArray.getInt(R.styleable.MyCircleBar_circle_max_progress, DEFAULT_MAX_PROGRESS);
        drawSingleColor = typedArray.getBoolean(R.styleable.MyCircleBar_circle_gradient_on, DEFAULT_GRADIENT_ON);
        startColor = typedArray.getColor(R.styleable.MyCircleBar_circle_start_color, DEFAULT_CIRCLE_START_COLOR);
        centerColor = typedArray.getColor(R.styleable.MyCircleBar_circle_center_color, DEFAULT_CIRCLE_CENTER_COLOR);
        endColor = typedArray.getColor(R.styleable.MyCircleBar_circle_end_color, DEFAULT_CIRCLE_END_COLOR);
        hintTextColor = typedArray.getColor(R.styleable.MyCircleBar_circle_hint_text_color, DEFAULT_HINT_TEXT_COLOR);
        showTextColor = typedArray.getColor(R.styleable.MyCircleBar_circle_show_text_color, DEFAULT_SHOW_TEXT_COLOR);
        drawScaleColor = typedArray.getColor(R.styleable.MyCircleBar_circle_show_scale_color, DEFAULT_DRAW_SCALE_COLOR);
        isShowScale = typedArray.getBoolean(R.styleable.MyCircleBar_circle_show_scale_on, DEFAULT_DRAW_SCALE_ON);
        typedArray.recycle();
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //获取宽度和高度的测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//宽度的测量模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);//高度的测量模式
        //获取宽度和高度的测量值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //获取最小值
        circle_diameter = Math.min(opinionSide(widthMode, widthSize),
                opinionSide(heightMode, heightSize));
        //获取宽和高的最小值最为边长(直径),设置控件的宽和高
        setMeasuredDimension(circle_diameter, circle_diameter);
    }


    /**
     * 测量
     *
     * @param mode
     * @param size
     * @return
     */

    private int opinionSide(int mode, int size)
    {
        int result = 0;
        if (mode == MeasureSpec.EXACTLY)
        {
            result = size;
        } else
        {
            // 设置默认边长
            int defaultSize = DEFAULT_CIRCLE_SIDE;
            if (mode == MeasureSpec.AT_MOST)
            {
                result = Math.min(size, defaultSize);
            }
        }
        return result;
    }

    /**
     * dp转px
     *
     * @param dipValues
     * @return
     */
    private int dip2px(int dipValues)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValues, getResources().getDisplayMetrics());
    }


    /**
     * 进度条动画
     *
     * @author Administrator
     */
    public class BarAnimation extends Animation
    {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);	
			if (interpolatedTime < 1.0f) {
				unit = nowProgress / maxProgress * drawMaxValues * interpolatedTime;
				values = (int) (nowProgress * interpolatedTime);
			} else {
				unit = nowProgress / maxProgress * drawMaxValues;
                values = nowProgress;
			}
            postInvalidate();
        }
    }	
	
}