package edu.utap.gymfree.ui.dashboard

import android.icu.text.SimpleDateFormat
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.gymfree.Location
import edu.utap.gymfree.R

class FireStoreLocationAdapter(private var viewModel: DashboardViewModel)
    : ListAdapter<Location, FireStoreLocationAdapter.VH>(Diff()) {
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
        private val dateFormat = SimpleDateFormat("hh:mm:ss MM-dd-yyyy")
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul
        private var userTV = itemView.findViewById<TextView>(R.id.locationUserTV)
        private var createdTV = itemView.findViewById<TextView>(R.id.locationTimeTV)
        private var addressTV = itemView.findViewById<TextView>(R.id.locationAddressTV)
        private var capacityTV = itemView.findViewById<TextView>(R.id.locationCapacityTV)
        private var openTV = itemView.findViewById<TextView>(R.id.locationOpeningTV)
        private var closeTV = itemView.findViewById<TextView>(R.id.locationClosingTV)

        private fun bindElements(item: Location,
                                 userTV: TextView,
                                 timeTV: TextView,
                                 addressTV: TextView,
                                 capacityTV: TextView,
                                 openTV: TextView,
                                 closeTV: TextView
        ) {
            Log.i("XXX-ADAPTER-bindels", item.toString())
            userTV.text = Html.fromHtml(item.name)
            addressTV.text = Html.fromHtml("<b>Address</b>: " + item.address.toString())
            capacityTV.text = Html.fromHtml("<b>Capacity</b>: " + item.capacity?.toInt().toString())
            openTV.text = Html.fromHtml("<b>Opening time</b>: " + item.openingTime.toString())
            closeTV.text = Html.fromHtml("<b>Closing time</b>: " +  item.closingTime.toString())
            timeTV.text = Html.fromHtml("<b>Created on</b>: " + dateFormat.format(item.timeStamp?.toDate()).toString())

            userTV.visibility = View.VISIBLE
            addressTV.visibility = View.VISIBLE
            capacityTV.visibility = View.VISIBLE
            openTV.visibility = View.VISIBLE
            closeTV.visibility = View.VISIBLE
            timeTV.visibility = View.VISIBLE
        }

        fun bind(item: Location?) {
            if (item == null) {
                Log.i("XXX-ADAPTER-bind", "NULL!")
                return
            }
            bindElements(
                    item,
                    userTV,
                    createdTV,
                    addressTV,
                    capacityTV,
                    openTV,
                    closeTV
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_location, parent, false)
        //Log.d(MainActivity.TAG, "Create VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i("XXX-adapter", getItem(holder.adapterPosition).toString())
        holder.bind(getItem(holder.adapterPosition))
    }
}
