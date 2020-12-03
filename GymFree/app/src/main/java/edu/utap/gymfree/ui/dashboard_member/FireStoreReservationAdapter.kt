package edu.utap.gymfree.ui.dashboard_member

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import edu.utap.gymfree.R
import edu.utap.gymfree.Reservation
import edu.utap.gymfree.ui.chat.ChatFragment
import edu.utap.gymfree.ui.timeslot.TimeslotFragment


import java.util.*

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
        private val sdf = SimpleDateFormat("MM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul
        private var userTV = itemView.findViewById<TextView>(R.id.locationUserTV)
        private var addressTV = itemView.findViewById<TextView>(R.id.locationAddressTV)
        private var dateTV = itemView.findViewById<TextView>(R.id.locationDateTV)
        private var timeslotTV = itemView.findViewById<TextView>(R.id.locationTimeslotTV)
        // buttons
        private var cancelBut = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.cancelButton)
        private var cancelButSure = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.cancelButtonSure)
        private var contactBut = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.contactButton)
        private var routeBut = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.routeButton)
        // recycler view
        private var reservationList = itemView.findViewById<RecyclerView>(R.id.guestList)



        private fun bindElements(item: Reservation,
                                 userTV: TextView,
                                 addressTV: TextView,
                                 dateTV: TextView,
                                 timeslotTV: TextView,
                                 cancelBut: com.google.android.material.button.MaterialButton,
                                 cancelButSure: com.google.android.material.button.MaterialButton,
                                 contactBut: com.google.android.material.button.MaterialButton,
                                 routeBut: com.google.android.material.button.MaterialButton,
                                 reservationList: RecyclerView
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

            var start = sdf.parse(item.startTime)
            var end = sdf.parse(item.endTime)
            val dayofweek = SimpleDateFormat("EEEE", Locale.ENGLISH)

            dateTV.text = Html.fromHtml("<b>Date</b>: ${dayofweek.format(start)}, ${start.month+1}/${start.date}/${start.year}")

            var startMinutes = start.minutes.toString()
            if (startMinutes == "0"){
                startMinutes = "00"
            }

            var endMinutes = end.minutes.toString()
            if (endMinutes == "0"){
                endMinutes = "00"
            }

            val tslot = Html.fromHtml("<b>Time</b>: ${start.hours}:${startMinutes} - ${end.hours}:${endMinutes}")
            timeslotTV.text = tslot

            cancelBut.setOnClickListener {
                Log.i(TAG,"longclick - delbut")
                cancelBut.visibility = View.GONE
                cancelButSure.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    cancelBut.visibility = View.VISIBLE
                    cancelButSure.visibility = View.GONE
                }, 3000)
            }

            cancelBut.setOnLongClickListener {
                Log.i(TAG,"longclick - delbutsure")
                cancelBut.visibility = View.GONE
                cancelButSure.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    cancelBut.visibility = View.VISIBLE
                    cancelButSure.visibility = View.GONE
                }, 3000)
                true
            }

            cancelButSure.setOnClickListener {
                Log.i(TAG,"click - cancelbut")
//                viewModel.deleteLocation(item)
            }

            cancelButSure.setOnLongClickListener {
                Log.i(TAG,"click - delbutsure")
//                viewModel.deleteLocation(item)
                true
            }

            contactBut.setOnClickListener {
                Log.i(TAG,"click - delbutsure")
            }



            userTV.visibility = View.VISIBLE
            addressTV.visibility = View.VISIBLE
            dateTV.visibility = View.VISIBLE
            timeslotTV.visibility = View.VISIBLE
            cancelBut.visibility = View.VISIBLE
            contactBut.visibility = View.VISIBLE
            routeBut.visibility = View.VISIBLE
            cancelButSure.visibility = View.GONE
            reservationList.visibility = View.GONE
        }

        fun bind(item: Reservation?) {
            if (item == null) {
                Log.i("XXX-ADAPTER-bind", "NULL!")
                return
            }
            bindElements(
                    item,
                    userTV,
                    addressTV,
                    dateTV,
                    timeslotTV,
                    cancelBut,
                    cancelButSure,
                    contactBut,
                    routeBut,
                    reservationList
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_reservation, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i("XXX-adapter", getItem(holder.adapterPosition).toString())
        holder.bind(getItem(holder.adapterPosition))
    }
}
