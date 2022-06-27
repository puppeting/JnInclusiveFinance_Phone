package com.inclusive.finance.jh.widget.snakebar

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import com.google.android.material.behavior.SwipeDismissBehavior
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.widget.snakebar.SnackbarManager.Companion.getInstance
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class TSnackbar private constructor(private val mParent: ViewGroup?) {
    abstract class Callback {
        @IntDef(DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT, DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE)
        @Retention(RetentionPolicy.SOURCE)
        annotation class DismissEvent

        fun onDismissed(snackbar: TSnackbar?, @DismissEvent event: Int) {}
        fun onShown(snackbar: TSnackbar?) {}

        companion object {
            const val DISMISS_EVENT_SWIPE = 0
            const val DISMISS_EVENT_ACTION = 1
            const val DISMISS_EVENT_TIMEOUT = 2
            const val DISMISS_EVENT_MANUAL = 3
            const val DISMISS_EVENT_CONSECUTIVE = 4
        }
    }

    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Duration
    companion object {
        const val LENGTH_INDEFINITE = -2
        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0
        private const val ANIMATION_DURATION = 250
        private const val ANIMATION_FADE_DURATION = 180
        lateinit var sHandler: Handler
        private const val MSG_SHOW = 0
        private const val MSG_DISMISS = 1
        fun make(view: View, text: CharSequence, @Duration duration: Int): TSnackbar {
            val snackbar = TSnackbar(findSuitableParent(view))
            snackbar.setText(text)
            snackbar.setDuration(duration)
            return snackbar
        }

        fun make(view: View, @StringRes resId: Int, @Duration duration: Int): TSnackbar {
            return make(view, view.resources.getText(resId), duration)
        }

        private fun findSuitableParent(view: View): ViewGroup? {
            var view: View? = view
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    return view
                } else if (view is FrameLayout) {
                    fallback = if (view.getId() == R.id.content) {
                        return view
                    } else {
                        view
                    }
                } else if (view is Toolbar || view is android.widget.Toolbar) {
                    /*
                    If we return the toolbar here, the toast will be attached inside the toolbar.
                    So we need to find a some sibling ViewGroup to the toolbar that comes after the toolbar
                    If we don't find such view, the toast will be attached to the root activity view
                 */
                    if (view.parent is ViewGroup) {
                        val parent = view.parent as ViewGroup

                        // check if there's something else beside toolbar
                        if (parent.childCount > 1) {
                            val childrenCnt = parent.childCount
                            var toolbarIdx = 0
                            var i = 0
                            while (i < childrenCnt) {

                                // find the index of toolbar in the layout (most likely 0, but who knows)
                                if (parent.getChildAt(i) === view) {
                                    toolbarIdx = i
                                    // check if there's something else after the toolbar in the layout
                                    if (toolbarIdx < childrenCnt - 1) {
                                        //try to find some ViewGroup where you can attach the toast
                                        while (i < childrenCnt) {
                                            i++
                                            val v = parent.getChildAt(i)
                                            if (v is ViewGroup) return v
                                        }
                                    }
                                    break
                                }
                                i++
                            }
                        }
                    }

                    //                return (ViewGroup) view;
                }
                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)
            return fallback
        }

        private fun convertDpToPixel(dp: Float, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
            val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            return bitmap
        }

        private fun getBitmap(drawable: Drawable): Bitmap {
            return if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else if (drawable is VectorDrawable) {
                getBitmap(drawable)
            } else {
                throw IllegalArgumentException("unsupported drawable type")
            }
        }

        init {
            sHandler = Handler(Looper.getMainLooper(), Handler.Callback { message ->
                when (message.what) {
                    MSG_SHOW -> {
                        (message.obj as TSnackbar).showView()
                        return@Callback true
                    }
                    MSG_DISMISS -> {
                        (message.obj as TSnackbar).hideView(message.arg1)
                        return@Callback true
                    }
                }
                false
            })
        }
    }

    private val mContext: Context
    private val mView: SnackbarLayout

    @get:Duration
    var duration = 0
        private set
    private var mCallback: Callback? = null

    @Deprecated("")
    fun addIcon(resource_id: Int, size: Int): TSnackbar {
        val tv = mView.messageView
        tv!!.setCompoundDrawablesWithIntrinsicBounds(BitmapDrawable(Bitmap.createScaledBitmap((mContext.resources.getDrawable(resource_id) as BitmapDrawable).bitmap, size, size, true)), null, null, null)
        return this
    }

    fun setIconPadding(padding: Int): TSnackbar {
        val tv = mView.messageView
        tv!!.compoundDrawablePadding = padding
        return this
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    fun setIconLeft(@DrawableRes drawableRes: Int, sizeDp: Float,color:Int?=null): TSnackbar {
        val tv = mView.messageView
        var drawable = ContextCompat.getDrawable(mContext, drawableRes)
        drawable = if (drawable != null) {
            fitDrawable(drawable, convertDpToPixel(sizeDp, mContext).toInt())
        } else {
            throw IllegalArgumentException("resource_id is not a valid drawable!")
        }
        val compoundDrawables = tv!!.compoundDrawables
        tv.setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
        if (color!=null)
        tv.compoundDrawableTintList= ColorStateList.valueOf(color)
        return this
    }

    fun setIconRight(@DrawableRes drawableRes: Int, sizeDp: Float,color:Int?=null): TSnackbar {
        val tv = mView.messageView
        var drawable = ContextCompat.getDrawable(mContext, drawableRes)
        drawable = if (drawable != null) {
            fitDrawable(drawable, convertDpToPixel(sizeDp, mContext).toInt())
        } else {
            throw IllegalArgumentException("resource_id is not a valid drawable!")
        }
        val compoundDrawables = tv!!.compoundDrawables
        tv.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3])
        if (color!=null)
            tv.compoundDrawableTintList= ColorStateList.valueOf(color)
        return this
    }

    /**
     * Overrides the max width of this snackbar's layout. This is typically not necessary; the snackbar
     * width will be according to Google's Material guidelines. Specifically, the max width will be
     *
     *
     * To allow the snackbar to have a width equal to the parent view, set a value <= 0.
     *
     * @param maxWidth the max width in pixels
     * @return this TSnackbar
     */
    fun setMaxWidth(maxWidth: Int): TSnackbar {
        mView.mMaxWidth = maxWidth
        return this
    }

    private fun fitDrawable(drawable: Drawable, sizePx: Int): Drawable {
        var drawable = drawable
        if (drawable.intrinsicWidth != sizePx || drawable.intrinsicHeight != sizePx) {
            if (drawable is BitmapDrawable) {
                drawable = BitmapDrawable(mContext.resources, Bitmap.createScaledBitmap(getBitmap(drawable), sizePx, sizePx, true))
            }
        }
        drawable.setBounds(0, 0, sizePx, sizePx)
        return drawable
    }

    fun setAction(@StringRes resId: Int, listener: View.OnClickListener?): TSnackbar {
        return setAction(mContext.getText(resId), listener)
    }

    fun setAction(text: CharSequence?, listener: View.OnClickListener?): TSnackbar {
        return setAction(text, true, listener)
    }

    fun setAction(text: CharSequence?, shouldDismissOnClick: Boolean, listener: View.OnClickListener?): TSnackbar {
        val tv: TextView? = mView.actionView
        if (TextUtils.isEmpty(text) || listener == null) {
            tv!!.visibility = View.GONE
            tv.setOnClickListener(null)
        } else {
            tv!!.visibility = View.VISIBLE
            tv.text = text
            tv.setOnClickListener { view ->
                listener.onClick(view)
                if (shouldDismissOnClick) {
                    dispatchDismiss(Callback.DISMISS_EVENT_ACTION)
                }
            }
        }
        return this
    }

    fun setActionTextColor(colors: ColorStateList?): TSnackbar {
        val tv: TextView? = mView.actionView
        tv!!.setTextColor(colors)
        return this
    }

    fun setActionTextColor(@ColorInt color: Int): TSnackbar {
        val tv: TextView? = mView.actionView
        tv!!.setTextColor(color)
        return this
    }

    fun setText(message: CharSequence): TSnackbar {
        val tv = mView.messageView
        tv!!.text = message
        return this
    }

    fun setText(@StringRes resId: Int): TSnackbar {
        return setText(mContext.getText(resId))
    }

    fun setDuration(@Duration duration: Int): TSnackbar {
        this.duration = duration
        return this
    }

    val view: View
        get() = mView

    fun show() {
        getInstance()?.show(duration, mManagerCallback)
    }

    fun dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL)
    }

    private fun dispatchDismiss(@Callback.DismissEvent event: Int) {
        getInstance()?.dismiss(mManagerCallback, event)
    }

    fun setCallback(callback: Callback?): TSnackbar {
        mCallback = callback
        return this
    }

    val isShown: Boolean
        get() = getInstance()?.isCurrent(mManagerCallback) == true
    val isShownOrQueued: Boolean
        get() = getInstance()?.isCurrentOrNext(mManagerCallback) == true
    private val mManagerCallback: SnackbarManager.Callback = object : SnackbarManager.Callback {
        override fun show() {
            sHandler?.sendMessage(sHandler?.obtainMessage(MSG_SHOW, this@TSnackbar))
        }

        override fun dismiss(event: Int) {
            sHandler?.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, this@TSnackbar))
        }
    }

    fun showView() {
        if (mView.parent == null) {
            val lp = mView.layoutParams
            if (lp is CoordinatorLayout.LayoutParams) {
                val behavior: Behavior = Behavior()
                behavior.setStartAlphaSwipeDistance(0.1f)
                behavior.setEndAlphaSwipeDistance(0.6f)
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)
                behavior.listener = object : SwipeDismissBehavior.OnDismissListener {
                    override fun onDismiss(view: View) {
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE)
                    }

                    override fun onDragStateChanged(state: Int) {
                        when (state) {
                            SwipeDismissBehavior.STATE_DRAGGING, SwipeDismissBehavior.STATE_SETTLING -> getInstance().cancelTimeout(mManagerCallback)
                            SwipeDismissBehavior.STATE_IDLE -> getInstance().restoreTimeout(mManagerCallback)
                        }
                    }
                }
                lp.behavior = behavior
            }
            mParent!!.addView(mView)
        }
        mView.setOnAttachStateChangeListener(object : SnackbarLayout.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {}
            override fun onViewDetachedFromWindow(v: View?) {
                if (isShownOrQueued) {
                    sHandler!!.post { onViewHidden(Callback.DISMISS_EVENT_MANUAL) }
                }
            }
        })
        if (ViewCompat.isLaidOut(mView)) {
            animateViewIn()
        } else {
            mView.setOnLayoutChangeListener(object : SnackbarLayout.OnLayoutChangeListener {
                override fun onLayoutChange(view: View?, left: Int, top: Int, right: Int, bottom: Int) {
                    animateViewIn()
                    mView.setOnLayoutChangeListener(null)
                }
            })
        }
    }

    private fun animateViewIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.setTranslationY(mView, -mView.height.toFloat())
            ViewCompat.animate(mView).translationY(0f)
                .setInterpolator(com.inclusive.finance.jh.widget.snakebar.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION.toLong())
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationStart(view: View) {
                        mView.animateChildrenIn(ANIMATION_DURATION - ANIMATION_FADE_DURATION, ANIMATION_FADE_DURATION)
                    }

                    override fun onAnimationEnd(view: View) {
                        if (mCallback != null) {
                            mCallback!!.onShown(this@TSnackbar)
                        }
                        getInstance().onShown(mManagerCallback)
                    }
                }).start()
        } else {
            val anim = AnimationUtils.loadAnimation(mView.context, R.anim.top_in)
            anim.interpolator = com.inclusive.finance.jh.widget.snakebar.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
            anim.duration = ANIMATION_DURATION.toLong()
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation) {
                    if (mCallback != null) {
                        mCallback!!.onShown(this@TSnackbar)
                    }
                    getInstance().onShown(mManagerCallback)
                }

                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
            })
            mView.startAnimation(anim)
        }
    }

    private fun animateViewOut(event: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(mView).translationY(-mView.height.toFloat())
                .setInterpolator(com.inclusive.finance.jh.widget.snakebar.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION.toLong())
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationStart(view: View) {
                        mView.animateChildrenOut(0, ANIMATION_FADE_DURATION)
                    }

                    override fun onAnimationEnd(view: View) {
                        onViewHidden(event)
                    }
                }).start()
        } else {
            val anim = AnimationUtils.loadAnimation(mView.context, R.anim.top_out)
            anim.interpolator = com.inclusive.finance.jh.widget.snakebar.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
            anim.duration = ANIMATION_DURATION.toLong()
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation) {
                    onViewHidden(event)
                }

                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
            })
            mView.startAnimation(anim)
        }
    }

    fun hideView(event: Int) {
        if (mView.visibility != View.VISIBLE || isBeingDragged) {
            onViewHidden(event)
        } else {
            animateViewOut(event)
        }
    }

    private fun onViewHidden(event: Int) {
        getInstance().onDismissed(mManagerCallback)
        if (mCallback != null) {
            mCallback!!.onDismissed(this, event)
        }
        val parent = mView.parent
        if (parent is ViewGroup) {
            parent.removeView(mView)
        }
    }

    private val isBeingDragged: Boolean
        private get() {
            val lp = mView.layoutParams
            if (lp is CoordinatorLayout.LayoutParams) {
                val behavior = lp.behavior
                if (behavior is SwipeDismissBehavior<*>) {
                    return (behavior.dragState != SwipeDismissBehavior.STATE_IDLE)
                }
            }
            return false
        }

    class SnackbarLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        LinearLayout(context, attrs) {
        var messageView: TextView? = null
            private set
        var actionView: Button? = null
            private set
        var mMaxWidth: Int
        private val mMaxInlineActionWidth: Int

        interface OnLayoutChangeListener {
            fun onLayoutChange(view: View?, left: Int, top: Int, right: Int, bottom: Int)
        }

        interface OnAttachStateChangeListener {
            fun onViewAttachedToWindow(v: View?)
            fun onViewDetachedFromWindow(v: View?)
        }

        private var mOnLayoutChangeListener: OnLayoutChangeListener? = null
        private var mOnAttachStateChangeListener: OnAttachStateChangeListener? = null
        override fun onFinishInflate() {
            super.onFinishInflate()
            messageView = findViewById<View>(R.id.snackbar_text) as TextView
            actionView = findViewById<View>(R.id.snackbar_action) as Button
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var widthMeasureSpec = widthMeasureSpec
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if (mMaxWidth > 0 && measuredWidth > mMaxWidth) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY)
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
            val multiLineVPadding = resources.getDimensionPixelSize(R.dimen.design_snackbar_padding_vertical_2lines)
            val singleLineVPadding = resources.getDimensionPixelSize(R.dimen.design_snackbar_padding_vertical)
            val isMultiLine = messageView!!.layout.lineCount > 1
            var remeasure = false
            if (isMultiLine && mMaxInlineActionWidth > 0 && actionView!!.measuredWidth > mMaxInlineActionWidth) {
                if (updateViewsWithinLayout(VERTICAL, multiLineVPadding, multiLineVPadding - singleLineVPadding)) {
                    remeasure = true
                }
            } else {
                val messagePadding = if (isMultiLine) multiLineVPadding else singleLineVPadding
                if (updateViewsWithinLayout(HORIZONTAL, messagePadding, messagePadding)) {
                    remeasure = true
                }
            }
            if (remeasure) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }

        fun animateChildrenIn(delay: Int, duration: Int) {
            ViewCompat.setAlpha(messageView, 0f)
            ViewCompat.animate(messageView!!).alpha(1f).setDuration(duration.toLong())
                .setStartDelay(delay.toLong()).start()
            if (actionView!!.visibility == VISIBLE) {
                ViewCompat.setAlpha(actionView, 0f)
                ViewCompat.animate(actionView!!).alpha(1f).setDuration(duration.toLong())
                    .setStartDelay(delay.toLong()).start()
            }
        }

        fun animateChildrenOut(delay: Int, duration: Int) {
            ViewCompat.setAlpha(messageView, 1f)
            ViewCompat.animate(messageView!!).alpha(0f).setDuration(duration.toLong())
                .setStartDelay(delay.toLong()).start()
            if (actionView!!.visibility == VISIBLE) {
                ViewCompat.setAlpha(actionView, 1f)
                ViewCompat.animate(actionView!!).alpha(0f).setDuration(duration.toLong())
                    .setStartDelay(delay.toLong()).start()
            }
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            super.onLayout(changed, l, t, r, b)
            if (mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener!!.onLayoutChange(this, l, t, r, b)
            }
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener!!.onViewAttachedToWindow(this)
            }
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener!!.onViewDetachedFromWindow(this)
            }
        }

        fun setOnLayoutChangeListener(onLayoutChangeListener: OnLayoutChangeListener?) {
            mOnLayoutChangeListener = onLayoutChangeListener
        }

        fun setOnAttachStateChangeListener(listener: OnAttachStateChangeListener?) {
            mOnAttachStateChangeListener = listener
        }

        private fun updateViewsWithinLayout(
            orientation: Int,
            messagePadTop: Int, messagePadBottom: Int,
        ): Boolean {
            var changed = false
            if (orientation != getOrientation()) {
                setOrientation(orientation)
                changed = true
            }
            if (messageView!!.paddingTop != messagePadTop || messageView!!.paddingBottom != messagePadBottom) {
                updateTopBottomPadding(messageView, messagePadTop, messagePadBottom)
                changed = true
            }
            return changed
        }

        companion object {
            private fun updateTopBottomPadding(view: View?, topPadding: Int, bottomPadding: Int) {
                if (ViewCompat.isPaddingRelative(view!!)) {
                    ViewCompat.setPaddingRelative(view, ViewCompat.getPaddingStart(view), topPadding, ViewCompat.getPaddingEnd(view), bottomPadding)
                } else {
                    view.setPadding(view.paddingLeft, topPadding, view.paddingRight, bottomPadding)
                }
            }
        }

        init {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout)
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1)
            mMaxInlineActionWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_maxActionInlineWidth, -1)
            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, a.getDimensionPixelSize(R.styleable.SnackbarLayout_elevation, 0)
                    .toFloat())
            }
            a.recycle()
            isClickable = true
            LayoutInflater.from(context).inflate(R.layout.tsnackbar_layout_include, this)
            ViewCompat.setAccessibilityLiveRegion(this, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
        }
    }

    internal inner class Behavior : SwipeDismissBehavior<SnackbarLayout?>() {
        override fun canSwipeDismissView(child: View): Boolean {
            return child is SnackbarLayout
        }

        override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: SnackbarLayout, event: MotionEvent): Boolean {
            if (parent.isPointInChildBounds(child, event.x.toInt(), event.y.toInt())) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> getInstance().cancelTimeout(mManagerCallback)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> getInstance().restoreTimeout(mManagerCallback)
                }
            }
            return super.onInterceptTouchEvent(parent, child, event)
        }
    }

    init {
        mContext = mParent!!.context
        val inflater = LayoutInflater.from(mContext)
        mView = inflater.inflate(R.layout.tsnackbar_layout, mParent, false) as SnackbarLayout
    }
}