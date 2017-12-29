package id.co.ionsoft.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import id.co.ionsoft.customview.util.SizeUtil;

/**
 * Items of centering horizontal scroll view
 * created at 11/20/15
 *
 * @author hendrawd
 */
public class DistanceItemView extends View {

    private float drawRadius;
    private Paint paint;
    private boolean currentCenter = false;
    // default position is mid
    private int position = 0;
    private String text;
    private ArrayList<Bitmap> bmAnimations = new ArrayList<>();
    private Bitmap bm;

    public DistanceItemView(Context context) {
        super(context);
        init(context);
    }

    public DistanceItemView(Context context, AttributeSet as) {
        super(context, as);
        init(context);
        // get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(as,
                R.styleable.DistanceItemView, 0, 0);
        try {
            // get the text and colors specified using the names in attrs.xml
            position = a.getInteger(R.styleable.DistanceItemView_viewPosition, position);
            text = a.getString(R.styleable.DistanceItemView_viewText);
            if (!isInEditMode()) {
                TypedArray images;
                switch (position) {
                    case 0:
                        images = getResources().obtainTypedArray(R.array.distance1);
                        break;
                    case 1:
                        images = getResources().obtainTypedArray(R.array.distance2);
                        break;
                    case 2:
                        images = getResources().obtainTypedArray(R.array.distance3);
                        break;
                    case 3:
                        images = getResources().obtainTypedArray(R.array.distance4);
                        break;
                    case 4:
                        images = getResources().obtainTypedArray(R.array.distance5);
                        break;
                    case 5:
                        images = getResources().obtainTypedArray(R.array.distance6);
                        break;
                    default:
                        images = getResources().obtainTypedArray(R.array.distance1);
                        break;
                }
                for (int i = 0; i < images.length(); i++) {
                    bmAnimations.add(BitmapFactory.decodeResource(context.getResources(), images.getResourceId(i, -1)));
                }
                images.recycle();
                bm = bmAnimations.get(bmAnimations.size() - 1);
            }
        } finally {
            a.recycle();
        }
    }

    private void init(Context context) {
        drawRadius = SizeUtil.dp2px(context, 4);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            //initial dimension values
            int width = getWidth();
            int height = getHeight();
            float centerX = width / 2;
            float centerY = height / 2;
            float dp2 = SizeUtil.dp2px(getContext(), 2);

            // draw background
            // canvas.drawPaint(paint);

            // draw line as the position
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(SizeUtil.dp2px(getContext(), 1));
            switch (position) {
                case 5:
                    //end of the items(if the items number is 6)
                    //draw left line only
                    canvas.drawLine(0, centerY, centerX, centerY, paint);
                    break;
                case 0:
                    //start of the items
                    //draw right line only
                    canvas.drawLine(centerX, centerY, width, centerY, paint);
                    break;
                default:
                    //draw left and right line
                    canvas.drawLine(0, centerY, width, centerY, paint);
            }

            //draw the arc
            if (position > 0) {
                float radius;
                if (width > height) {
                    radius = height / 2;
                } else {
                    radius = width / 2;
                }
                float adderY;
                switch (position) {
                    case 1:
                        adderY = centerY / 2;
                        break;
                    case 2:
                        adderY = centerY;
                        break;
                    case 3:
                        adderY = centerY + dp2;
                        break;
                    case 4:
                        adderY = centerY + dp2 * 2;
                        break;
                    case 5:
                        adderY = centerY + dp2 * 4;
                        break;
                    default:
                        adderY = 0;

                }
                paint.setColor(ContextCompat.getColor(getContext(), R.color.distance_arc));
                paint.setStyle(Paint.Style.STROKE);
                @SuppressWarnings("UnnecessaryLocalVariable") float ovalStrokeWidth = dp2;
                paint.setStrokeWidth(ovalStrokeWidth);
                @SuppressLint("DrawAllocation") final RectF oval = new RectF();
                float ovalLeft = centerX - radius + ovalStrokeWidth / 2;
                float ovalTop = centerY - adderY - radius + ovalStrokeWidth / 2;
                float ovalRight = centerX + radius - ovalStrokeWidth / 2;
                float ovalBottom = centerY + adderY + radius - ovalStrokeWidth / 2;
                oval.set(ovalLeft,
                        ovalTop,
                        ovalRight,
                        ovalBottom);
                // change the canvas x by half as a workaround to make the arc center
                canvas.scale(.5F, 1);
                canvas.drawArc(oval, 270, 180, false, paint);
            }

            // draw the center dot
            paint.setStyle(Paint.Style.FILL);
            setDotColor();
            // return the canvas size to the start after the size changed from draw arc
            if (position > 0)
                canvas.scale(2F, 1);
            canvas.drawCircle(width / 2, height / 2, drawRadius, paint);

            // draw text
            paint.setColor(Color.WHITE);
            // this is an example for draw text with center align
            // float textHeight = SizeUtil.dp2px(10, getContext());
            // paint.setTextSize(textHeight);
            // float textWidth = paint.measureText(text);
            // canvas.drawText(text, centerX - textWidth / 2, centerY + textHeight / 2, paint);
            float textHeight = SizeUtil.dp2px(getContext(), 10);
            paint.setTextSize(textHeight);
            float textLeftMargin = drawRadius + dp2 * 2;
            float textTopMargin = drawRadius;
            float textLeftAnchor = centerX + textLeftMargin;
            float textTopAnchor = centerY + textHeight + textTopMargin;
            canvas.drawText(text, textLeftAnchor, textTopAnchor, paint);

            float bitmapLeftAnchor = centerX + drawRadius + dp2 * 2;
            float bitmapTopAnchor = centerY - bm.getHeight() - drawRadius;
            // dipake buat gambar distance kedua yang animasinya ada nyempil bawah biar sama anchornya dengan yang lain
            if (position == 1)
                bitmapTopAnchor += dp2;
            canvas.drawBitmap(bm, bitmapLeftAnchor, bitmapTopAnchor, paint);
        } else {
            paint.setColor(Color.rgb(245, 88, 0));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
    }

    private void setDotColor() {
        if (currentCenter) {
            paint.setColor(Color.parseColor("#a73270"));
        } else {
            paint.setColor(Color.WHITE);
        }
    }

    public void setCurrentCenter(boolean currentCenter) {
        this.currentCenter = currentCenter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private boolean isRunnableRunning = false;
    public Runnable animator = new Runnable() {
        @Override
        public void run() {
            try {
                if (!isRunnableRunning) {
                    isRunnableRunning = true;
                    if (position == 4) {
                        // position 4 is different sequential, better using this for memory optimization
                        // instead of copying the same bitmap to the array list
                        for (int i = 0; i < bmAnimations.size() - 1; i++) {
                            bm = bmAnimations.get(i);
                            ((Activity) getContext()).runOnUiThread(() -> invalidate());
                            try {
                                if (i == bmAnimations.size() - 2)
                                    Thread.sleep(200);
                                else
                                    Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = bmAnimations.size() - 3; i >= 0; i--) {
                            bm = bmAnimations.get(i);
                            ((Activity) getContext()).runOnUiThread(() -> invalidate());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        bm = bmAnimations.get(bmAnimations.size() - 1);
                        ((Activity) getContext()).runOnUiThread(() -> invalidate());
                    } else if (position == 0) {
                        // position 0 have 300ms delay on radius 1_9, before that 200ms
                        for (int i = 0; i < bmAnimations.size(); i++) {
                            bm = bmAnimations.get(i);
                            ((Activity) getContext()).runOnUiThread(() -> invalidate());
                            try {
                                if (i != bmAnimations.size() - 2)
                                    Thread.sleep(200);
                                else
                                    Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ((Activity) getContext()).runOnUiThread(() -> invalidate());
                    } else if (position == 1) {
                        // position 1 is different sequential, better using this for memory optimization
                        // instead of copying the same bitmap to the array list
                        for (int i = 0; i < bmAnimations.size() - 1; i++) {
                            bm = bmAnimations.get(i);
                            ((Activity) getContext()).runOnUiThread(() -> invalidate());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 1; i >= 0; i--) {
                            bm = bmAnimations.get(i);
                            ((Activity) getContext()).runOnUiThread(() -> invalidate());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        bm = bmAnimations.get(bmAnimations.size() - 1);
                        ((Activity) getContext()).runOnUiThread(() -> invalidate());
                    } else {
                        for (Bitmap bitmap : bmAnimations) {
                            bm = bitmap;
                            ((Activity) getContext()).runOnUiThread(() -> invalidate());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    isRunnableRunning = false;
                }
            } catch (Exception e) {
            }
        }
    };
}
