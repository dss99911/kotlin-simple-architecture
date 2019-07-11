package kim.jeonghyeon.androidlibrary.ui.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.core.view.animation.PathInterpolatorCompat;

/**
 * Created by hyun on 16. 2. 25..
 * https://matthewlein.com/tools/ceaser
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AnimationUtil {
//    public static final int DEFAULT_DURATION = 300;

    public static Interpolator getBezierCurve(float x1, float y1, float x2, float y2) {
        return PathInterpolatorCompat.create(x1, y1, x2, y2);
    }

    public static Interpolator getEaseInCurve() {
        return PathInterpolatorCompat.create(0.420f, 0.000f, 1.000f, 1.000f);
    }

    public static Interpolator getEaseInOutCurve() {
        return PathInterpolatorCompat.create(0.420f, 0.000f, 0.580f, 1.000f);
    }

    public static Interpolator getSpringCurve() {
        return new Interpolator() {
            @Override
            public float getInterpolation(float input) {

                return easeOut(input, 0, 1, 1);
            }

            public float easeOut(float t, float b, float c, float d) {
                if (t == 0) return b;
                if ((t /= d) == 1) return b + c;
                float p = d * .3f;
                float s = p / 4;
                return (c * (float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p) + c + b);
            }
        };
    }

    public static Interpolator getDefaultEaseCurve() {
        return PathInterpolatorCompat.create(0.250f, 0.100f, 0.250f, 1.000f);
    }

    public static float getCurrentValue(float animatedValue, float first, float last) {
        return (last - first) * animatedValue + first;
    }

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(float animatedValue);
    }

    @SuppressWarnings("unused")
    public interface AnimatorListener {
        void onAnimationStart();

        void onAnimationEnd();
    }

//    public static class SpringAnimator {
//        private final Spring spring;
//        private final SpringSystem springSystem;
//        private AnimatorUpdateListener animatorUpdateListener;
//        private AnimatorListener animatorListener;
//        private long delay;
//        private View v;
//
//        //		public SpringSystem getSpringSystem() {//다른 애니메이션에서 재활용 하기 위해
////			return springSystem;
////		}
//        private boolean reuse;
//        private boolean isCancel;
//        private SimpleSpringListener springListener = new SimpleSpringListener() {
//            @Override
//            public void onSpringUpdate(Spring spring) {
//                super.onSpringUpdate(spring);
//                if (animatorUpdateListener != null && !isCancel) {
//                    animatorUpdateListener.onAnimationUpdate((float) spring.getCurrentValue());
//                }
//            }
//
//            @Override
//            public void onSpringAtRest(Spring spring) {
//                super.onSpringAtRest(spring);
//                if (animatorListener != null && !isCancel) {
//                    animatorListener.onAnimationEnd();
//                }
//                if (!reuse) spring.destroy();
//            }
//
//            @Override
//            public void onSpringActivate(Spring spring) {
//                super.onSpringActivate(spring);
//                isCancel = false;
//                if (animatorListener != null) {
//                    animatorListener.onAnimationStart();
//                }
//            }
//        };
//
//        public SpringAnimator(float tension, float friction, float velocity) {
//            this.springSystem = SpringSystem.create();
//            spring = springSystem.createSpring();
//            spring.setSpringConfig(new SpringConfig(tension, friction));
//            spring.setVelocity(velocity);
//            spring.addListener(this.springListener);
//        }
////		public SpringAnimator(SpringSystem springSystem, float tension, float friction, float velocity) {
////			this.springSystem = springSystem;
////			spring = springSystem.createSpring();
////			spring.setSpringConfig(new SpringConfig(tension, friction));
////			spring.setVelocity(velocity);
////		}
//
//        public void setUpdateListener(AnimatorUpdateListener animatorUpdateListener) {
//            this.animatorUpdateListener = animatorUpdateListener;
//        }
//
//        public void setListener(AnimatorListener animatorListener) {
//
//            this.animatorListener = animatorListener;
//        }
//
//        public void setStartDelay(long delay, View v) {
//            this.delay = delay;
//            this.v = v;
//        }
//
//        public void start() {
//            if (delay != 0l) {
//                v.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        spring.setEndValue(1);
//                    }
//                }, delay);
//            } else spring.setEndValue(1);
//        }
//
//        public void cancel() {
//            isCancel = true;
//        }
//
//        public void destroy() {
//            spring.destroy();
//        }
//
//        public void setReUse() {
//            reuse = true;
//        }
//    }

    /**
     * 0f -> 1f
     */
    public static class RepeatAnimationWrapper {
        final View v;
        ValueAnimator animator;
        final int duration;
        final ValueAnimator.AnimatorUpdateListener listener;
        Runnable runnable;
        Runnable runnableCancel;

        public RepeatAnimationWrapper(View v, ValueAnimator.AnimatorUpdateListener listener, int duration) {
            this.v = v;
            this.listener = listener;
            this.duration = duration;
        }

        public void start() {
            if (animator == null) {
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(duration);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(listener);
                animator.setStartDelay(0);
                animator.setInterpolator(new LinearInterpolator());
                runnable = () -> animator.start();
                runnableCancel = () -> animator.cancel();
            }
            if (!isRunning()) {
                v.post(runnable);
            }
        }

        public void cancel() {
            if (animator != null && isRunning())
                v.post(() -> animator.cancel());
        }

        public boolean isRunning() {
            return animator.isStarted() || animator.isRunning();
        }
    }

    public static class ShowHideWrapper {
        final View v;
        View[] sameViews;
        View[] diffViews;
        final int duration;
        boolean isShowing;
        final boolean hideAtFirst;
        Runnable onHideListener;
        OnAnimation onAnimation = new OnAnimation() {
            @Override
            public void onUpdate(View v, float animatedValue) {
                v.setAlpha(animatedValue);
            }

            @Override
            public void onStart(@NonNull View v, boolean showing) {
                if (showing) v.setAlpha(0);
                else v.setAlpha(1);
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(@NonNull View v, boolean showing) {
                if (showing) {
                    v.setAlpha(1);
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setAlpha(0);
                    v.setVisibility(View.INVISIBLE);
                }
            }
        };
        ValueAnimator valueAnimatorShow;
        Runnable actionShow;
        ValueAnimator valueAnimatorDismiss;
        Runnable actionHide;
        private Interpolator interpolator;
        private ValueAnimator.AnimatorUpdateListener animationUpdateListener;
        private final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animatedValue = (float) animation.getAnimatedValue();
                float diffAnimatedValue = 1 - animatedValue;
                onAnimation.onUpdate(v, animatedValue);
//				v.setAlpha(animatedValue);
                if (sameViews != null) for (View same : sameViews) {
                    onAnimation.onUpdate(same, animatedValue);
//					same.setAlpha(animatedValue);
                }
                if (diffViews != null) for (View diff : diffViews) {
                    onAnimation.onUpdate(diff, diffAnimatedValue);
//					diff.setAlpha(diffAnimatedValue);
                }

                if (animationUpdateListener != null) {
                    animationUpdateListener.onAnimationUpdate(animation);
                }


            }
        };

        public ShowHideWrapper(View v, int duration, boolean hideAtFirst, OnAnimation... onAnimations) {
            this.v = v;
            this.duration = duration;
            this.hideAtFirst = hideAtFirst;
            isShowing = !hideAtFirst;
            if (onAnimations.length > 0) onAnimation = onAnimations[0];
            if (hideAtFirst) {
                onAnimation.onEnd(v, false);
//				v.setAlpha(0);
//				v.setVisibility(View.INVISIBLE);
            } else {
                onAnimation.onEnd(v, true);
//				v.setAlpha(1);
//				v.setVisibility(View.VISIBLE);
            }
        }

        public void setOnHideListener(Runnable onHideListener) {
            this.onHideListener = onHideListener;
        }

        public void setAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener animationUpdateListener) {

            this.animationUpdateListener = animationUpdateListener;
        }

        public void setSameViews(@NonNull View... sameViews) {
            this.sameViews = sameViews;
            for (View same : sameViews) {
                if (hideAtFirst) {
                    onAnimation.onEnd(same, false);
//					same.setAlpha(0);
//					same.setVisibility(View.INVISIBLE);
                } else {
                    onAnimation.onEnd(same, true);
//					same.setAlpha(1);
//					same.setVisibility(View.VISIBLE);
                }
            }

        }

        public void setDiffViews(@NonNull View... diffViews) {
            this.diffViews = diffViews;
            for (View same : diffViews) {
                if (!hideAtFirst) {
                    onAnimation.onEnd(same, false);
//					same.setAlpha(0);
//					same.setVisibility(View.INVISIBLE);
                } else {
                    onAnimation.onEnd(same, true);
//					same.setAlpha(1);
//					same.setVisibility(View.VISIBLE);
                }
            }
        }

        public void show() {
            isShowing = true;

            if (valueAnimatorShow == null) {
                valueAnimatorShow = ValueAnimator.ofFloat(0f, 1f);
                if (interpolator != null) valueAnimatorShow.setInterpolator(interpolator);
                valueAnimatorShow.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        onAnimation.onStart(v, true);
                        if (sameViews != null) for (View same : sameViews) {
                            onAnimation.onStart(same, true);
//							same.setVisibility(View.VISIBLE);
                        }
                        if (diffViews != null) for (View diff : diffViews) {
                            onAnimation.onStart(diff, false);
//							diff.setVisibility(View.VISIBLE);
                        }
                        if (valueAnimatorDismiss != null && (valueAnimatorDismiss.isRunning() || valueAnimatorDismiss.isStarted()))
                            valueAnimatorDismiss.cancel();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        onAnimation.onEnd(v, true);
                        if (sameViews != null) for (View same : sameViews) {
                            onAnimation.onEnd(same, true);
                        }
                        if (diffViews != null) for (View diff : diffViews) {
                            onAnimation.onEnd(diff, false);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                valueAnimatorShow.setDuration(duration).addUpdateListener(updateListener);
                actionShow = () -> valueAnimatorShow.start();
//				objectAnimatorShow = ObjectAnimator.ofFloat(v, View.ALPHA, 0f, 1f).setDuration(duration);
            }

            v.post(actionShow);


        }

        public void dismiss() {
            if (!isShowing) return;
            isShowing = false;


            if (valueAnimatorDismiss == null) {
                valueAnimatorDismiss = ValueAnimator.ofFloat(1f, 0f).setDuration(duration);
                valueAnimatorDismiss.addUpdateListener(updateListener);
                if (interpolator != null) valueAnimatorDismiss.setInterpolator(interpolator);
                valueAnimatorDismiss.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        onAnimation.onStart(v, false);
                        if (sameViews != null) for (View same : sameViews) {
                            onAnimation.onStart(same, false);
                            //							same.setVisibility(View.VISIBLE);
                        }
                        if (diffViews != null) for (View diff : diffViews) {
                            onAnimation.onStart(diff, true);
                            //							diff.setVisibility(View.VISIBLE);
                        }

                        if (valueAnimatorShow != null && (valueAnimatorShow.isRunning() || valueAnimatorShow.isStarted()))
                            valueAnimatorShow.cancel();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onAnimation.onEnd(v, false);
                        if (sameViews != null) for (View same : sameViews) {
                            onAnimation.onEnd(same, false);
                            //							same.setVisibility(View.VISIBLE);
                        }
                        if (diffViews != null) for (View diff : diffViews) {
                            onAnimation.onEnd(diff, true);
                            //							diff.setVisibility(View.VISIBLE);
                        }

//						v.setVisibility(View.GONE);
//						if (sameViews != null) for (View same : sameViews) {
//							same.setVisibility(View.GONE);
//						}
                        if (onHideListener != null) onHideListener.run();


                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                actionHide = () -> valueAnimatorDismiss.start();
            }
            v.post(actionHide);
        }

        public boolean isShowing() {
            return isShowing;
        }

        public void setShowing() {
            isShowing = true;
            v.setVisibility(View.VISIBLE);
            v.setAlpha(1f);
        }

        public void setInterpolator(Interpolator interpolator) {

            this.interpolator = interpolator;
        }

        public interface OnAnimation {
            void onStart(View v, boolean showing);

            void onEnd(View v, boolean showing);

            void onUpdate(View v, float animatedValue);
        }
    }
}
