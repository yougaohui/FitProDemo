package xfkj.fitpro.utils.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import xfkj.fitpro.R;

/**
 * Created by gaohui.you on 2/28/18
 * Email:839939978@qq.com
 */
public class GlideUitls {

    public static void loadlocal(Context context, ImageView view, Integer resourceId) {
        Glide.with(context)
                .load(resourceId)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(view);

    }


    public static void loadLocal(Context context, String url, ImageView view) {
        loadLocal(context, url, R.mipmap.default_load_img, view, null);
    }

    public static void loadLocal(Context context, String url, ImageView view, BitmapTransformation transformation) {
        loadLocal(context, url, R.mipmap.default_load_img, view, transformation);
    }

    public static void loadLocal(Context context, String url, int defAvator, ImageView view) {
        loadLocal(context, url, defAvator, view, null);
    }

    public static void loadLocal(Context context, String url, ImageView view, boolean isCircleShape) {
        BitmapTransformation transformation;

        int borderSize = 2;
        int radius = 13;
        int borderColor = ColorUtils.getColor(R.color.watch_theme2_border_color);
        if (!isCircleShape) {
            RoundedCornersTransform.Border border = new RoundedCornersTransform.Border(TypedValue.COMPLEX_UNIT_DIP, borderSize, borderColor);
            transformation = new RoundedCornersTransform(TypedValue.COMPLEX_UNIT_DIP, radius, border);
        } else {
            transformation = new CircleCropTransform(TypedValue.COMPLEX_UNIT_DIP, borderSize, borderColor);
        }

        loadLocal(context, url, -1, view, transformation);
    }

    public static void loadLocal(Context context, String url, int defAvator, ImageView view, BitmapTransformation transformation) {
        try {
            RequestManager glide = Glide.with(context);
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options.centerCrop();
            if (defAvator == -1) {
                options.error(R.drawable.transparent_bg);
                options.placeholder(R.drawable.transparent_bg);
            } else {
                options.error(defAvator);
                options.placeholder(defAvator);
            }
            if (transformation != null) {
                options.transform(transformation);
            }
            glide.load(url).apply(options).into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void load(Context context, int resourceId, ImageView view) {
        Glide.with(context)
                .load(resourceId)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(view);
    }


    public static void loadImgFromSever(String url, ImageView view) {
        try {
            int defImg = R.mipmap.default_load_img;
            Glide.with(Utils.getApp()).load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .autoClone()
                            .error(defImg)
                            .placeholder(defImg))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("aa", "error:" + e.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (view != null) {
                                int screenW = (int) (ScreenUtils.getScreenWidth() / (3.2f));
                                int iWidth = resource.getIntrinsicWidth();
                                float scale = (screenW * 1.0f) / (iWidth * 1.0f);
                                Bitmap bitmap = ImageUtils.drawable2Bitmap(resource);
                                bitmap = ImageUtils.scale(bitmap, scale, scale);

                                ViewGroup.LayoutParams params = view.getLayoutParams();
                                params.width = bitmap.getWidth();
                                params.height = bitmap.getHeight();
                                Bitmap finalBitmap = bitmap;
                                ((Activity) (view.getContext())).runOnUiThread(() -> view.setImageBitmap(finalBitmap));
                            }
                            return false;
                        }
                    }).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
