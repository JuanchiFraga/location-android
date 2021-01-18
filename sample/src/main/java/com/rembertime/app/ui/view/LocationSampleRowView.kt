package com.rembertime.app.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.cardview.widget.CardView
import com.rembertime.app.R
import kotlinx.android.synthetic.main.location_sample_row_layout.view.*

class LocationSampleRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.location_sample_row_layout, this)
        val params = MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val margin = resources.getDimensionPixelSize(R.dimen._6sdp)
        params.setMargins(margin, margin, margin, margin)
        layoutParams = params
        cardElevation = resources.getDimension(R.dimen._6sdp)
        radius = resources.getDimension(R.dimen._6sdp)
    }

    fun setTitle(title: String) {
        rowTitle.text = title
    }

    fun setDescription(description: String) {
        rowDescription.text = description
    }
}