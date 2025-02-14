package org.wikipedia.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import org.wikipedia.util.ResourceUtil

class DrawableItemDecoration @JvmOverloads constructor(context: Context, @AttrRes id: Int,
                                                       private val drawStart: Boolean = true,
                                                       private val drawEnd: Boolean = true,
                                                       private val horizontalPadding: Int = -1) : ItemDecoration() {

    private val drawable: Drawable = AppCompatResources.getDrawable(context, ResourceUtil.getThemedAttributeId(context, id))!!

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = drawable.intrinsicHeight
        if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            outRect.bottom = drawable.intrinsicHeight
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        if (parent.childCount == 0) {
            return
        }
        val end = parent.childCount - 1
        for (i in (if (drawStart) 0 else 1) until end) {
            draw(canvas, bounds(parent, parent.getChildAt(i), true))
        }
        draw(canvas, bounds(parent, parent.getChildAt(end), true))
        if (drawEnd) {
            draw(canvas, bounds(parent, parent.getChildAt(end), false))
        }
    }

    private fun bounds(parent: RecyclerView, child: View, top: Boolean): Rect {
        val layoutManager = parent.layoutManager
        val bounds = Rect()
        bounds.right = parent.width - if (horizontalPadding != -1) horizontalPadding else parent.paddingRight
        bounds.left = if (horizontalPadding != -1) horizontalPadding else parent.paddingLeft
        val height = drawable.intrinsicHeight
        bounds.top = if (top) layoutManager!!.getDecoratedTop(child) else layoutManager!!.getDecoratedBottom(child) - height
        bounds.bottom = bounds.top + height
        return bounds
    }

    private fun draw(canvas: Canvas, bounds: Rect) {
        drawable.bounds = bounds
        drawable.draw(canvas)
    }
}