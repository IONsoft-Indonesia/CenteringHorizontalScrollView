package id.co.ionsoft.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import id.co.ionsoft.customview.util.SizeUtil;

/**
 * A centering HSV loosely based on http://iotasol.blogspot.com/2011/08/creating-custom-horizontal-scroll-view.html
 * Created on 11/27/15
 *
 * @author hendrawd
 */
public class CenteringHorizontalScrollView extends HorizontalScrollView implements View.OnTouchListener {

    public interface NewItemListener {
        void onNewItemCentered(View targetView);
    }

    private static final int SWIPE_PAGE_ON_FACTOR = 3;

    private NewItemListener mNewItemListener;

    private int mActiveItem = 0;

    private int mItemWidth;

    private float mPrevScrollX;

    private boolean mStart = true;

    private boolean mNewItemCentered = false;

    public CenteringHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mItemWidth = (int) SizeUtil.dp2px(context, 96);// or whatever your item width is.
        setOnTouchListener(this);
    }

    public void setNewItemListener(NewItemListener newItemListener) {
        this.mNewItemListener = newItemListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();

        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mStart) {
                    mPrevScrollX = x;
                    mStart = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                mStart = true;
                // minimal factor that the new item should be changed to the next or previous item like in percent
                // so if the scroll offset is more than the min factor, the focus will be changed to the next or previous item
                int minFactor = mItemWidth / SWIPE_PAGE_ON_FACTOR;
                // change the focus to the next item
                if ((mPrevScrollX - (float) x) > minFactor) {
                    if (mActiveItem < getMaxItemCount() - 1) {
                        mActiveItem = mActiveItem + 1;
                        playSoundEffect(android.view.SoundEffectConstants.CLICK);
                        mNewItemCentered = true;
                    }
                }
                // change the focus to the previous item
                else if (((float) x - mPrevScrollX) > minFactor) {
                    if (mActiveItem > 0) {
                        mActiveItem = mActiveItem - 1;
                        playSoundEffect(android.view.SoundEffectConstants.CLICK);
                        mNewItemCentered = true;
                    }
                }

                scrollToActiveItem();
                handled = true;
                break;
            default:
                break;
        }

        return handled;
    }

    private int getMaxItemCount() {
        return ((LinearLayout) getChildAt(0)).getChildCount();
    }

    public LinearLayout getLinearLayout() {
        return (LinearLayout) getChildAt(0);
    }

    /**
     * Centers the current view the best it can.
     */
    public void centerCurrentItem() {
        if (getMaxItemCount() == 0) {
            return;
        }

        int currentX = getScrollX();
        View targetChild;
        int currentChild = -1;

        do {
            currentChild++;
            targetChild = getLinearLayout().getChildAt(currentChild);
        } while (currentChild < getMaxItemCount() && targetChild.getLeft() < currentX);

        if (mActiveItem != currentChild) {
            mActiveItem = currentChild;
            scrollToActiveItem();
        }
    }

    /**
     * Scrolls the list view to the currently active child.
     */
    private void scrollToActiveItem() {
        int maxItemCount = getMaxItemCount();
        if (maxItemCount == 0) {
            return;
        }

        int targetItem = Math.min(maxItemCount - 1, mActiveItem);
        mActiveItem = Math.max(0, targetItem);

        // Scroll so that the target child is centered
        View targetView = getCurrentItem();

        int targetLeft = targetView.getLeft();
        int childWidth = targetView.getRight() - targetLeft;

        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int targetScroll = targetLeft - ((width - childWidth) / 2);

        if (mNewItemCentered) {
            LinearLayout ll = getLinearLayout();
            int itemLength = ll.getChildCount();
            for (int i = 0; i < itemLength; i++) {
                DistanceItemView distanceItemView = ((DistanceItemView) ll.getChildAt(i));
                if (distanceItemView == targetView) {
                    distanceItemView.setCurrentCenter(true);
                } else {
                    distanceItemView.setCurrentCenter(false);
                }
                distanceItemView.invalidate();
            }

            if (mNewItemListener != null) {
                mNewItemListener.onNewItemCentered(targetView);
            }
            mNewItemCentered = false;
        }

        super.smoothScrollTo(targetScroll, 0);
        // or
        // super.scrollTo(targetScroll, 0);
        // if need to notify parent that new item is centered
        // targetView.requestFocus();
    }

    /**
     * Sets the current item and centers it.
     *
     * @param currentItem The new current item.
     */
    public void setCurrentItemAndCenter(int currentItem) {
        mActiveItem = currentItem;
        mNewItemCentered = true;
        scrollToActiveItem();
    }

    public View getCurrentItem() {
        return getLinearLayout().getChildAt(mActiveItem);
    }

    public int getActiveItemPosition() {
        return mActiveItem;
    }
}
