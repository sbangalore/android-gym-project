package edu.utap.gymfree.ui.dashboard

import android.icu.text.SimpleDateFormat
import android.opengl.Visibility
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
import kotlinx.coroutines.delay

class FireStoreLocationAdapter(private var viewModel: DashboardViewModel)
    : ListAdapter<Location, FireStoreLocationAdapter.VH>(Diff()) {
    private val TAG = "adapter"
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
        private var userTV = itemView.findViewById<TextView>(R.id.locationUserTV)
        private var createdTV = itemView.findViewById<TextView>(R.id.locationTimeTV)
        private var addressTV = itemView.findViewById<TextView>(R.id.locationAddressTV)
        private var capacityTV = itemView.findViewById<TextView>(R.id.locationCapacityTV)
        private var openTV = itemView.findViewById<TextView>(R.id.locationOpeningTV)
        private var closeTV = itemView.findViewById<TextView>(R.id.locationClosingTV)
        private var delBut = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.deleteButton)
        private var delButSure = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.deleteButtonSure)

        private fun bindElements(item: Location,
                                 userTV: TextView,
                                 timeTV: TextView,
                                 addressTV: TextView,
                                 capacityTV: TextView,
                                 openTV: TextView,
                                 closeTV: TextView,
                                 delBut: com.google.android.material.button.MaterialButton,
                                 delButSure: com.google.android.material.button.MaterialButton
        ) {
            Log.i("XXX-ADAPTER-bindels", item.toString())
            userTV.text = Html.fromHtml(item.name)
            addressTV.text = Html.fromHtml("<b>Address</b>: " + item.address.toString())
            capacityTV.text = Html.fromHtml("<b>Capacity</b>: " + item.capacity?.toInt().toString())
            var openingHour = item.openingTime?.toInt().toString()
            var openingMins = item.openingTime?.minus(item.openingTime?.toInt()!!)?.times(60)?.toInt().toString()
            var closingHour = item.closingTime?.toInt().toString()
            var closingMins = item.closingTime?.minus(item.closingTime?.toInt()!!)?.times(60)?.toInt().toString()
            if (item.openingTime?.toInt()!! < 10) {
                openingHour = "0$openingHour"
            }
            if (item.openingTime?.minus(item.openingTime?.toInt()!!)?.times(60)?.toInt()!! < 10) {
                openingMins = "0$openingMins"
            }
            if (item.closingTime?.toInt()!! < 10) {
                closingHour = "0$closingHour"
            }
            if (item.closingTime?.minus(item.closingTime?.toInt()!!)?.times(60)?.toInt()!! < 10) {
                closingMins = "0$closingMins"
            }
            openTV.text = Html.fromHtml("<b>Opening</b>: $openingHour:$openingMins")
            closeTV.text = Html.fromHtml("<b>Closing</b>: $closingHour:$closingMins")
            timeTV.text = Html.fromHtml("<b>Created on</b>: " + dateFormat.format(item.timeStamp?.toDate()).toString())

            delBut.setOnClickListener {
                Log.i(TAG,"longclick - delbut")
                delBut.visibility = View.GONE
                delButSure.visibility = View.VISIBLE
                Thread.sleep(2000)
                delBut.visibility = View.VISIBLE
                delButSure.visibility = View.GONE
            }

            delBut.setOnLongClickListener {
                Log.i(TAG,"longclick - delbutsure")
                delBut.visibility = View.GONE
                delButSure.visibility = View.VISIBLE
                Thread.sleep(2000)
                delBut.visibility = View.VISIBLE
                delButSure.visibility = View.GONE
                true
            }

            delButSure.setOnClickListener {
                Log.i(TAG,"clik - delbut")
                viewModel.deleteLocation(item)
            }

            delButSure.setOnLongClickListener {
                Log.i(TAG,"clik - delbutsure")
                viewModel.deleteLocation(item)
                true
            }

            userTV.visibility = View.VISIBLE
            addressTV.visibility = View.VISIBLE
            capacityTV.visibility = View.VISIBLE
            openTV.visibility = View.VISIBLE
            closeTV.visibility = View.VISIBLE
            timeTV.visibility = View.VISIBLE
            delButSure.visibility = View.GONE
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
                    closeTV,
                    delBut,
                    delButSure
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
