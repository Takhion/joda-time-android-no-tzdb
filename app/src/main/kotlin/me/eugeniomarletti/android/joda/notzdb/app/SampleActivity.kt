package me.eugeniomarletti.android.joda.notzdb.app

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class SampleActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val d1 = DateTime().minusYears(2).minusMonths(3).toString(DateTimeFormat.fullDateTime())
        val d2 = DateTime().minusYears(2).minusMonths(6).toString(DateTimeFormat.fullDateTime())
        val s = "$d1\n$d2"

        TextView(this)
            .apply {
                text = s
                gravity = Gravity.CENTER
                layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                        .apply {
                            gravity = Gravity.CENTER
                        }
            }
            .let(::setContentView)
    }
}
