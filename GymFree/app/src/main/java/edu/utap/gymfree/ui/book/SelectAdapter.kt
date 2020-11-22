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
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.gymfree.Location
import edu.utap.gymfree.R
import kotlinx.coroutines.delay

class SelectAdapter(private var viewModel: SelectViewModel)
    : ListAdapter<Location, SelectAdapter.VH>(Diff()) {
    private val TAG = "Select adapter"
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.pictureUUID == newItem.pictureUUID
                    && oldItem.address == newItem.address
                    && oldItem.capacity?.equals(newItem.capacity) ?: true
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }
    companion object {
        private val dateFormat = SimpleDateFormat("hh:mm dd MMM, yyyy")
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul

        private var rowText = itemView.findViewById<TextView>(R.id.rowText)
        private var proceedBut = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.proceedBut)

        private fun bindElements(item: Location,
                                 rowText: TextView,
                                 proceedBut: com.google.android.material.button.MaterialButton
        ) {
            Log.i("XXX-ADAPTER-bindels", item.toString())
            rowText.text = Html.fromHtml(item.name)

            proceedBut.setOnClickListener {
                Log.i(TAG,"Proceed clicked")
                (itemView.context as FragmentActivity)
                        .supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, TimeslotFragment.newInstance(item.rowID))
                            .addToBackStack(null)
                            .commit()

            }


            rowText.visibility = View.VISIBLE
        }

        fun bind(item: Location?) {
            if (item == null) {
                Log.i("XXX-ADAPTER-bind", "NULL!")
                return
            }
            bindElements(
                    item,
                    rowText,
                    proceedBut,
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_select_location, parent, false)
        //Log.d(MainActivity.TAG, "Create VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i("XXX-adapter", getItem(holder.adapterPosition).toString())
        holder.bind(getItem(holder.adapterPosition))
    }
}
