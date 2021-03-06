package edu.utap.gymfree.ui.dashboard

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.gymfree.Location
import edu.utap.gymfree.R
import edu.utap.gymfree.ui.timeslot.TimeslotFragment

class FireStoreLocationAdapter(private var viewModel: DashboardViewModel)
    : ListAdapter<Location, FireStoreLocationAdapter.VH>(Diff()) {
    private val TAG = "adapter"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
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
        private val dateFormat = SimpleDateFormat("dd MMM, yyyy / hh:mm")
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
        private var guestListButton = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.guestListButton)
        private var guestList = itemView.findViewById<RecyclerView>(R.id.guestList)

        private fun bindElements(item: Location,
                                 userTV: TextView,
                                 timeTV: TextView,
                                 addressTV: TextView,
                                 capacityTV: TextView,
                                 openTV: TextView,
                                 closeTV: TextView,
                                 delBut: com.google.android.material.button.MaterialButton,
                                 delButSure: com.google.android.material.button.MaterialButton,
                                 guestListButton: com.google.android.material.button.MaterialButton,
                                 guestList: RecyclerView
        ) {
            Log.i("XXX-ADAPTER-bindels", item.toString())
            userTV.text = Html.fromHtml(item.name)
            addressTV.text = Html.fromHtml("<b>Address</b>: " + item.address.toString())
            capacityTV.text = Html.fromHtml("<b>Capacity</b>: " + item.capacity?.toInt().toString())
            var openingHour = item.openingTime?.toInt().toString()
            var openingMins = item.openingTime?.minus(item.openingTime?.toInt()!!)?.times(60)?.toInt().toString()
            var closingHour = item.closingTime?.toInt().toString()
            var closingMins = item.closingTime?.minus(item.closingTime?.toInt()!!)?.times(60)?.toInt().toString()

            if (item.openingTime?.minus(item.openingTime?.toInt()!!)?.times(60)?.toInt()!! < 10) {
                openingMins = "0$openingMins"
            }

            if (item.closingTime?.minus(item.closingTime?.toInt()!!)?.times(60)?.toInt()!! < 10) {
                closingMins = "0$closingMins"
            }
            openTV.text = Html.fromHtml("Opens at  $openingHour:$openingMins")
            closeTV.text = Html.fromHtml("Closes at $closingHour:$closingMins")
            timeTV.text = Html.fromHtml("<small>Created on " + dateFormat.format(item.timeStamp?.toDate()).toString() + "<small>")

            delBut.setOnClickListener {
                Log.i(TAG,"longclick - delbut")
                delBut.visibility = View.GONE
                delButSure.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    delBut.visibility = View.VISIBLE
                    delButSure.visibility = View.GONE
                }, 3000)
            }

            delBut.setOnLongClickListener {
                Log.i(TAG,"longclick - delbutsure")
                delBut.visibility = View.GONE
                delButSure.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    delBut.visibility = View.VISIBLE
                    delButSure.visibility = View.GONE
                }, 3000)
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

            guestListButton.setOnClickListener {
                Log.i(TAG, "get details")
                (itemView.context as FragmentActivity)
                        .supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, TimeslotFragment.newInstance(item.rowID))
                        .addToBackStack("select")
                        .commit()
            }

            userTV.visibility = View.VISIBLE
            addressTV.visibility = View.VISIBLE
            capacityTV.visibility = View.VISIBLE
            openTV.visibility = View.VISIBLE
            closeTV.visibility = View.VISIBLE
            timeTV.visibility = View.VISIBLE
            delButSure.visibility = View.GONE
            guestList.visibility = View.GONE

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
                    delButSure,
                    guestListButton,
                    guestList
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
