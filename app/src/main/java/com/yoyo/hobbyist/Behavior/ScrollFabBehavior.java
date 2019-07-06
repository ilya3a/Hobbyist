package com.yoyo.hobbyist.Behavior;

import android.content.Context;
import android.support.design.behavior.HideBottomViewOnScrollBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

public class ScrollFabBehavior extends HideBottomViewOnScrollBehavior {

    public ScrollFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed<0){
            child.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator()).start();
//            child.setVisibility(View.INVISIBLE);
//            hide();
        }else if (dyConsumed>0){
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child
                    .getLayoutParams();
            int fabMargin = lp.bottomMargin;
            child.animate().translationY(child.getHeight() + fabMargin)
                    .setInterpolator(new AccelerateInterpolator()).start();
        }

    }
}
