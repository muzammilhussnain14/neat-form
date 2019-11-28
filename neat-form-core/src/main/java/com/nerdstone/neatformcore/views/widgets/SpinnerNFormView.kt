package com.nerdstone.neatformcore.views.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.nerdstone.neatformcore.domain.data.DataActionListener
import com.nerdstone.neatformcore.domain.model.NFormViewDetails
import com.nerdstone.neatformcore.domain.model.NFormViewProperty
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.domain.view.RootView
import com.nerdstone.neatformcore.utils.ViewUtils
import com.nerdstone.neatformcore.views.builders.SpinnerViewBuilder
import com.nerdstone.neatformcore.views.handlers.ViewDispatcher

class SpinnerNFormView : LinearLayout, NFormView {

    override lateinit var viewProperties: NFormViewProperty
    override var dataActionListener: DataActionListener? = null
    override val viewBuilder = SpinnerViewBuilder(this)
    override var viewDetails = NFormViewDetails(this)
    override val nFormRootView get() = this.parent as RootView

    init {
        orientation = VERTICAL
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun initView(
        viewProperty: NFormViewProperty, viewDispatcher: ViewDispatcher
    ): NFormView {
        ViewUtils.setupView(this, viewProperty, viewDispatcher)
        return this
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        resetValueWhenHidden()
    }

    override fun resetValueWhenHidden() {
        if (visibility == View.GONE) {
            viewBuilder.resetSpinnerValue()
        }
    }

    override fun validaValues(): Boolean {
        if (viewProperties.requiredStatus != null && viewDetails.value==null) {

            val materialSpinner = (viewDetails.view as ViewGroup).getChildAt(0) as TextView
            materialSpinner.error=viewProperties.requiredStatus?.split(":")!![1]
            return false
        }

        return true
    }
}