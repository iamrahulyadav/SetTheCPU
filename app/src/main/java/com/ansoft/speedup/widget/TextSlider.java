package com.ansoft.speedup.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTimeConstants;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.EthiopicChronology;

public class TextSlider extends LinearLayout {
    private int childCount;
    private int childrenWidth;
    private String currentVal;
    int displayWidth;
    private boolean enabled;
    private boolean isScrolling;
    private List<SliderItem> itemList;
    private OnSnapListener listener;
    public SliderItem mCenterView;
    private boolean mDragMode;
    private int mInitialOffset;
    private int mLastScroll;
    private int mLastX;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mOriginalOffset;
    private int mScrollX;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int maxIndex;
    private int maxScrollX;
    private int minScrollX;
    private int objHeight;
    private int objWidth;
    private Integer[] offsets;
    private final int overScrollAmount;
    private float scrollScale;
    float trackScrollX;
    float trackScrollY;
    private Map<String, Integer> valueMap;
    private String[] values;

    public interface OnSnapListener {
        void onSnap(String str);
    }

    public TextSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
        this.offsets = null;
        this.isScrolling = false;
        this.objWidth = 80;
        this.objHeight = 50;
        this.overScrollAmount = (int) (((double) this.objWidth) / 1.61803399d);
        setLayoutParams(new LayoutParams(-1, -2));
        this.mScroller = new Scroller(getContext());
        setGravity(16);
        setOrientation(HORIZONTAL);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.valueMap = new HashMap();
        getBackground().setDither(true);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void init(String[] values, Integer index, int parentWidth) {
        int i;
        this.values = values;
        this.objWidth = (int) (((float) this.objWidth) * getResources().getDisplayMetrics().density);
        this.objHeight = (int) (((float) this.objHeight) * getResources().getDisplayMetrics().density);
        for (i = 0; i < values.length; i++) {
            this.valueMap.put(values[i], Integer.valueOf(i));
        }
        this.maxIndex = values.length;
        this.currentVal = values[index.intValue()];
        this.displayWidth = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        this.childCount = this.displayWidth / this.objWidth;
        if (this.displayWidth % this.objWidth != 0) {
            this.childCount++;
        }
        if (this.childCount % 2 == 0) {
            this.childCount++;
        }
        int centerIndex = this.childCount / 2;
        removeAllViews();
        i = 0;
        while (i < this.childCount) {
            addView(new SliderItem(getContext(), i == centerIndex, 26, 26, 9, 0.8f), new LayoutParams(this.objWidth, this.objHeight));
            i++;
        }
        this.mCenterView = (SliderItem) getChildAt(centerIndex);
        this.mCenterView.setVals((Integer) this.valueMap.get(this.currentVal), this.currentVal);
        this.itemList = new ArrayList();
        for (i = 0; i < this.childCount; i++) {
            this.itemList.add((SliderItem) getChildAt(i));
        }
        int j;
        SliderItem thisView;
        for (i = centerIndex + 1; i < this.childCount; i++) {
            thisView = (SliderItem) this.itemList.get(i);
            j = i - centerIndex;
            if (((Integer) this.valueMap.get(this.currentVal)).intValue() + j >= values.length) {
                thisView.setVals(Integer.valueOf(((Integer) this.valueMap.get(this.currentVal)).intValue() + j), null);
            } else {
                try {
                    thisView.setVals(Integer.valueOf(((Integer) this.valueMap.get(this.currentVal)).intValue() + j), values[((Integer) this.valueMap.get(this.currentVal)).intValue() + j]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    thisView.setVals(Integer.valueOf(-2), null);
                }
            }
        }
        for (i = centerIndex - 1; i >= 0; i--) {
            thisView = (SliderItem) this.itemList.get(i);
            j = i - centerIndex;
            if (((Integer) this.valueMap.get(this.currentVal)).intValue() + j < 0) {
                thisView.setVals(Integer.valueOf(((Integer) this.valueMap.get(this.currentVal)).intValue() + j), null);
            } else {
                try {
                    thisView.setVals(Integer.valueOf(((Integer) this.valueMap.get(this.currentVal)).intValue() + j), values[((Integer) this.valueMap.get(this.currentVal)).intValue() + j]);
                } catch (ArrayIndexOutOfBoundsException e2) {
                    thisView.setVals(Integer.valueOf(-2), null);
                }
            }
        }
        this.childrenWidth = this.childCount * this.objWidth;
        this.scrollScale = ((1.4f * ((float) values.length)) * ((float) this.objWidth)) / ((float) getContext().getResources().getDisplayMetrics().widthPixels);
        if (this.scrollScale > 2.6f) {
            this.scrollScale = 2.6f;
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mInitialOffset = (this.childrenWidth - w) / 2;
        this.mOriginalOffset = this.mInitialOffset;
        super.scrollTo(this.mInitialOffset, 0);
        this.mScrollX = this.mInitialOffset;
        this.mLastScroll = this.mInitialOffset;
        recomputeBounds();
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void setValue(String value) {
        setIndex(((Integer) this.valueMap.get(value)).intValue());
    }

    public void setValue2(String value) {
        setIndex2(((Integer) this.valueMap.get(value)).intValue());
    }

    public void setIndex(int index) {
        if (this.offsets != null) {
            snapTo(this.offsets[index].intValue());
            return;
        }
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        moveElements(-Math.round((float) (index - ((SliderItem) getChildAt(getChildCount() / 2)).getIndex())));
        this.currentVal = this.values[index];
        super.scrollTo(this.mInitialOffset, 0);
        this.mScrollX = this.mInitialOffset;
        this.mLastScroll = this.mInitialOffset;
        recomputeBounds();
    }

    public void setIndex2(int index) {
        this.mScrollX += (index - getIndex(this.currentVal).intValue()) * this.objWidth;
        this.currentVal = this.values[index];
        snapToNearest();
    }

    private void recomputeBounds() {
        int index = getIndex(this.currentVal).intValue();
        this.offsets = new Integer[this.values.length];
        this.minScrollX = this.mInitialOffset - (this.objWidth * index);
        this.maxScrollX = ((this.objWidth * this.values.length) - (this.objWidth * (index + 1))) + this.mInitialOffset;
        for (int i = 0; i < this.offsets.length; i++) {
            this.offsets[i] = Integer.valueOf(this.minScrollX + (this.objWidth * i));
        }
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            this.mScrollX = this.mScroller.getCurrX();
            if (this.mScrollX > this.maxScrollX) {
                this.mScrollX = this.maxScrollX;
            } else if (this.mScrollX < this.minScrollX) {
                this.mScrollX = this.minScrollX;
            }
            reScrollTo(this.mScrollX, 0, true);
            postInvalidate();
        }
    }

    public void scrollTo(int x, int y) {
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        if (x > this.maxScrollX) {
            x = this.maxScrollX;
        } else if (x < this.minScrollX) {
            x = this.minScrollX;
        }
        reScrollTo(x, y, true);
    }

    protected void reScrollTo(int x, int y, boolean notify) {
        int scrollX = getScrollX();
        int scrollDiff = x - this.mLastScroll;
        if ((x >= this.maxScrollX || x <= this.minScrollX) && !this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        if (getChildCount() > 0) {
            scrollX += scrollDiff;
            int offset = (x - this.mInitialOffset) / this.childrenWidth;
            if (x <= this.maxScrollX && x >= this.minScrollX) {
                if (scrollX - this.mInitialOffset > this.objWidth / 2) {
                    int relativeScroll = scrollX - this.mInitialOffset;
                    moveElements(-(((this.objWidth / 2) + relativeScroll) / this.objWidth));
                    scrollX = (((relativeScroll - (this.objWidth / 2)) % this.objWidth) + this.mInitialOffset) - (this.objWidth / 2);
                } else if (this.mInitialOffset - scrollX > this.objWidth / 2) {
                    moveElements(((this.objWidth / 2) + (this.mInitialOffset - scrollX)) / this.objWidth);
                    scrollX = (this.mInitialOffset + (this.objWidth / 2)) - (((this.mInitialOffset + (this.objWidth / 2)) - scrollX) % this.objWidth);
                }
            }
        }
        super.scrollTo(scrollX, y);
        if (this.listener == null || notify) {
            this.mLastScroll = x;
        } else {
            this.mLastScroll = x;
        }
        if (this.mScroller.isFinished() && !this.isScrolling) {
            this.isScrolling = true;
            snapToNearest();
        }
    }

    protected void moveElements(int steps) {
        if (steps != 0) {
            for (int i = 0; i != getChildCount(); i++) {
                SliderItem tv = (SliderItem) getChildAt(i);
                if (tv.index.intValue() - steps < 0) {
                    tv.setVals(Integer.valueOf(tv.index.intValue() - steps), null);
                } else if (tv.index.intValue() - steps > this.maxIndex - 1) {
                    tv.setVals(Integer.valueOf(tv.index.intValue() - steps), null);
                } else {
                    tv.setVals(Integer.valueOf(tv.index.intValue() - steps), this.values[tv.index.intValue() - steps]);
                }
                if (tv.isCenter) {
                    this.currentVal = this.values[this.mCenterView.getIndex()];
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.enabled) {
            return super.onTouchEvent(ev);
        }
        int action = ev.getAction();
        int x = (int) ev.getX();
        if (action == 0) {
            this.mDragMode = true;
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
                snapToNearest();
            }
        } else if (action == 261) {
            this.mScroller.abortAnimation();
            snapToNearest();
        }
        if (!this.mDragMode) {
            return super.onTouchEvent(ev);
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (action) {
            case MutableDateTime.ROUND_NONE /*0*/:
                getParent().requestDisallowInterceptTouchEvent(true);
                this.isScrolling = true;
                this.trackScrollX = ev.getX();
                this.trackScrollY = ev.getY();
                break;
            case EthiopicChronology.EE /*1*/:
                if (Math.abs(ev.getX() - this.trackScrollX) <= 8.0f && Math.abs(ev.getY() - this.trackScrollY) <= 8.0f) {
                    this.isScrolling = true;
                    this.mScrollX = (int) (((float) this.mScrollX) + (ev.getX() - ((float) (getWidth() / 2))));
                    snapToNearest();
                    break;
                }
                this.isScrolling = false;
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(DateTimeConstants.MILLIS_PER_SECOND);
                int initialVelocity = (int) Math.min(velocityTracker.getXVelocity(), (float) this.mMaximumVelocity);
                if (getChildCount() > 0 && Math.abs(initialVelocity) > this.mMinimumVelocity) {
                    fling(-initialVelocity);
                    break;
                }
                this.isScrolling = true;
                snapToNearest();
                break;
            case MutableDateTime.ROUND_CEILING /*2*/:
                requestDisallowInterceptTouchEvent(true);
                this.isScrolling = true;
                this.mScrollX = (int) (((float) this.mScrollX) + (((float) (this.mLastX - x)) * this.scrollScale));
                if (this.mScrollX > this.maxScrollX + this.overScrollAmount) {
                    this.mScrollX = this.maxScrollX + this.overScrollAmount;
                } else if (this.mScrollX < this.minScrollX - this.overScrollAmount) {
                    this.mScrollX = this.minScrollX - this.overScrollAmount;
                }
                reScrollTo(this.mScrollX, 0, true);
                break;
            case MutableDateTime.ROUND_HALF_FLOOR /*3*/:
                break;
            default:
                this.mDragMode = false;
                break;
        }
        this.mLastX = x;
        return super.onTouchEvent(ev);
    }

    private void fling(int velocityX) {
        if (getChildCount() > 0) {
            this.mScroller.fling(this.mScrollX, 0, velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            invalidate();
        }
    }

    public void setOnSnapListener(OnSnapListener l) {
        this.listener = l;
    }

    public Integer getIndex(String currentFreq) {
        return (Integer) this.valueMap.get(currentFreq);
    }

    private Integer findNearestOffset(Integer x) {
        int minDiff = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < this.offsets.length; i++) {
            int diff = Math.abs(this.offsets[i].intValue() - x.intValue());
            if (diff < minDiff) {
                minDiff = diff;
                minIndex = i;
            }
        }
        return this.offsets[minIndex];
    }

    private Integer findNearestIndex(Integer x) {
        int minDiff = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < this.offsets.length; i++) {
            int diff = Math.abs(this.offsets[i].intValue() - x.intValue());
            if (diff < minDiff) {
                minDiff = diff;
                minIndex = i;
            }
        }
        return Integer.valueOf(minIndex);
    }

    private void snapToNearest() {
        snapTo(findNearestOffset(Integer.valueOf(this.mScrollX)).intValue());
    }

    private void snapTo(int x) {
        this.mScroller.startScroll(this.mScrollX, 0, x - this.mScrollX, 0, 200);
        invalidate();
        this.currentVal = this.values[findNearestIndex(Integer.valueOf(x)).intValue()];
        this.listener.onSnap(this.currentVal);
    }

    public String getValue() {
        return this.currentVal;
    }

    public Integer getIndex() {
        return (Integer) this.valueMap.get(this.currentVal);
    }

    public void setCaption(String caption) {
        this.mCenterView.setCaption(caption);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
