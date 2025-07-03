package com.example.hairsalonappointments.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.adapters.AppointmentDetailAdapter
import com.example.hairsalonappointments.data.AppointmentDetailItem

class StickyHeaderItemDecoration(private val recyclerView: RecyclerView, private val adapter: AppointmentDetailAdapter) : RecyclerView.ItemDecoration() {

    private var stickyHeaderView: View? = null
    private var stickyHeaderPosition = -1

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0)
        topChild ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        topChildPosition == RecyclerView.NO_POSITION ?: return

        val headerPosition = findStickyHeaderPosition(topChildPosition)
        if (headerPosition >= 0) {
            if (stickyHeaderPosition != headerPosition) {
                stickyHeaderPosition = headerPosition
                stickyHeaderView = createStickyHeader(headerPosition)
            }

            stickyHeaderView?.let { header ->
                val contactPoint = header.bottom
                val childInContact = getChildInContact(parent, contactPoint)

                if (childInContact != null && adapter.getItemViewType(parent.getChildAdapterPosition(childInContact)) == adapter.getItemViewType(headerPosition)) {
                    if (childInContact.top > 0) {
                        c.save()
                        c.translate(0f, (childInContact.top - header.height).toFloat())
                        header.draw(c)
                        c.restore()
                    } else {
                        drawHeader(c, header)
                    }
                } else {
                    drawHeader(c, header)
                }
            }
        }
    }

    private fun findStickyHeaderPosition(startPosition: Int): Int {
        for (i in startPosition downTo 0) {
            if (adapter.currentList[i] is AppointmentDetailItem.PreviousVisitsHeader) {
                return i
            }
        }
        return -1
    }

    private fun createStickyHeader(position: Int): View {
        val viewHolder = adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(position))
        adapter.onBindViewHolder(viewHolder, position)
        val header = viewHolder.itemView

        val widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        header.measure(widthSpec, heightSpec)
        header.layout(0, 0, header.measuredWidth, header.measuredHeight)
        return header
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.bottom > contactPoint && child.top <= contactPoint) {
                return child
            }
        }
        return null
    }
}