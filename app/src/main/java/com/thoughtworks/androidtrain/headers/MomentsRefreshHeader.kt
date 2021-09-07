package com.thoughtworks.androidtrain.headers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.thoughtworks.androidtrain.R


class MomentsRefreshHeader : ConstraintLayout, RefreshHeader {
    private lateinit var refreshIconView: ImageView
    private lateinit var spinAnimator: ObjectAnimator
    private lateinit var onRefreshAnimator: AnimatorSet
    private lateinit var mRefreshLayout: RefreshLayout
    private lateinit var mRefreshKernel: RefreshKernel
    private var onRefresh: (() -> Unit)? = null

    lateinit var profileAvatarView: ImageView
    lateinit var profileUsernameView: TextView
    lateinit var profileBackgroundView: ImageView

    constructor(context: Context): super(context) { initView(context) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView(context) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView(context) }

    fun setOnRefresh(onRefresh: () -> Unit) {
        this.onRefresh = onRefresh
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        if (!this::mRefreshKernel.isInitialized) {
            mRefreshKernel = kernel
            mRefreshLayout = kernel.refreshLayout
            profileAvatarView = findViewById(R.id.moment_user_profile_avatar)
            profileUsernameView = findViewById(R.id.moment_user_profile_username)
            profileBackgroundView = findViewById(R.id.user_profile_background)
        }
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.PullDownToRefresh -> {
                onRefreshAnimator.cancel()
            }
            RefreshState.ReleaseToRefresh -> {
                refreshIconView.visibility = VISIBLE
                spinAnimator.start()
                onRefreshAnimator.start()
                onRefresh?.let { it() }
            }
            RefreshState.PullDownCanceled -> {
                spinAnimator.cancel()
                refreshIconView.visibility = INVISIBLE
            }
            else -> return
        }
    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        if ((mRefreshLayout as SmartRefreshLayout).state != RefreshState.PullDownToRefresh) {
            refreshIconView.y = (500 - offset).toFloat()
        }
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        mRefreshKernel.setState(RefreshState.None)
        mRefreshKernel.setState(RefreshState.RefreshReleased)
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean) = 0

    override fun getView(): View = this

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    override fun setPrimaryColors(vararg colors: Int) {}

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {}

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun isSupportHorizontalDrag() = false

    private fun initView(context: Context) {
        refreshIconView = ImageView(context).apply {
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.moments, null))
            visibility = VISIBLE
            translationZ = 100F
        }

        val params = LayoutParams(100, 100).also {
            it.topToTop = this.id
            it.startToStart = this.id
            it.marginStart = (50F * context.resources.displayMetrics.density).toInt()
        }

        addView(refreshIconView, 0, params)
        initAnimators()
    }

    private fun initAnimators() {
        spinAnimator = ObjectAnimator.ofFloat(refreshIconView, "rotation", 36000f).apply {
            duration = 25000
            interpolator = LinearInterpolator()
        }

        val enterAnimator = ObjectAnimator.ofFloat(refreshIconView, "translationY", 250F).apply {
            duration = 10
            interpolator = LinearInterpolator()
        }

        val onExitAnimator = ObjectAnimator.ofFloat(refreshIconView, "translationY", 0F).apply {
            duration = 500
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    refreshIconView.visibility = INVISIBLE
                    spinAnimator.cancel()
                }
            })
        }

        onRefreshAnimator = AnimatorSet().apply {
            play(enterAnimator)
            play(onExitAnimator).after(3000L)
        }
    }
}