package edu.utap.gymfree.ui.dashboard_member

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import edu.utap.gymfree.R
import edu.utap.gymfree.Reservation

class FireStoreReservationAdapter(private var viewModel: DashboardViewModel)
    : ListAdapter<Reservation, FireStoreReservationAdapter.VH>(Diff()) {
    private val TAG = "adapter"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Reservation>() {
        override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
            return oldItem.rowID == newItem.rowID
        }


        override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.userId == newItem.userId
                    && oldItem.startTime == newItem.startTime
                    && oldItem.endTime == newItem.endTime
                    && oldItem.timeslotId == newItem.timeslotId
                    && oldItem.locationId == newItem.locationId
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

        private fun bindElements(item: Reservation,
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
            val loc = db.collection("locations").document(item.locationId!!).get()
            loc.addOnSuccessListener {
                val name = it.getString("name")
                val address = it.getString("address")
                val pictureUUID = it.getString("pictureUUID")
                userTV.text = Html.fromHtml(name)
                addressTV.text = Html.fromHtml("<b>Address</b>: $address")


//                val reservation = mapOf(
//                        "name" to name,
//                        "rowId" to resID,
//                        "userId" to myUid(),
//                        "startTime" to timeslot.startTime,
//                        "endTime" to timeslot.endTime,
//                        "timeslotId" to timeslot.rowId,
//                        "locationId" to locationId
//
//                )
            }

            var startTime = item.startTime
            var endTime = item.endTime


            openTV.text = Html.fromHtml("From  $startTime")
            closeTV.text = Html.fromHtml("To $endTime")
//            timeTV.text = Html.fromHtml("<small>Created on " + dateFormat.format(item.timeStamp?.toDate()).toString() + "<small>")

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
                viewModel.deleteReservations(item)
            }

            delButSure.setOnLongClickListener {
                Log.i(TAG,"clik - delbutsure")
                viewModel.deleteReservations(item)
                true
            }

            guestListButton.setOnClickListener {
                Log.i(TAG, "get details")
                guestList.visibility = View.VISIBLE
                guestListButton.visibility = View.GONE
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

        fun bind(item: Reservation?) {
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
