package edu.utap.gymfree.ui.book

import android.icu.text.SimpleDateFormat
import android.opengl.Visibility
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.gymfree.ui.book.Timeslot
import edu.utap.gymfree.R
import kotlinx.coroutines.delay

class TimeslotAdapter(private var viewModel: TimeslotViewModel)
    : ListAdapter<Timeslot, TimeslotAdapter.VH>(Diff()) {
    private val TAG = "Timeslot adapter"
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Timeslot>() {
        override fun areItemsTheSame(oldItem: Timeslot, newItem: Timeslot): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: Timeslot, newItem: Timeslot): Boolean {
            return oldItem.startTime == newItem.startTime
                    && oldItem.endTime == newItem.endTime
        }
    }
    companion object {
        private val dateFormat = SimpleDateFormat("hh:mm dd MMM, yyyy")
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul

        private var timeslotText = itemView.findViewById<TextView>(R.id.timeslotText)
        private var bookBut = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.bookBut)


        private fun bindElements(item: Timeslot,
                                 rowText: TextView,
                                 bookBut: com.google.android.material.button.MaterialButton
        ) {
            Log.i("XXX-ADAPTER-bindels", item.toString())
            val slots = Html.fromHtml(item.startTime).toString() + " " + Html.fromHtml(item.endTime).toString()
            timeslotText.text = Html.fromHtml(slots)

            rowText.visibility = View.VISIBLE

            bookBut.setOnClickListener {
                (itemView.context as FragmentActivity)
                        .supportFragmentManager
                        .popBackStack("select", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                Log.d(TAG, "POPPED")
            }

        }

        fun bind(item: Timeslot?) {
            if (item == null) {
                Log.i("XXX-ADAPTER-bind", "NULL!")
                return
            }
            bindElements(
                    item,
                    timeslotText,
                    bookBut,
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_timeslot, parent, false)
        //Log.d(MainActivity.TAG, "Create VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i("XXX-adapter", getItem(holder.adapterPosition).toString())
        holder.bind(getItem(holder.adapterPosition))
    }
}
