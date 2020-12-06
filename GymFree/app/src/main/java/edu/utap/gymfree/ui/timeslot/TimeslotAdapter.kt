package edu.utap.gymfree.ui.timeslot

import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.gymfree.R
import edu.utap.gymfree.ui.users.UsersFragment
import java.util.*

class TimeslotAdapter(private var viewModel: TimeslotViewModel)
    : ListAdapter<Timeslot, TimeslotAdapter.VH>(Diff()) {
    private val TAG = "Timeslot adapter"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val currEmail = FirebaseAuth.getInstance().currentUser?.email
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Timeslot>() {
        override fun areItemsTheSame(oldItem: Timeslot, newItem: Timeslot): Boolean {
            return oldItem.rowId == newItem.rowId
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
        private var rowHolder = itemView.findViewById<ConstraintLayout>(R.id.timeslotRow)

        private fun bindElements(item: Timeslot,
                                 timeslotText: TextView,
                                 bookBut: com.google.android.material.button.MaterialButton,
                                 rowHolder: ConstraintLayout
        ) {
            Log.i("XXX-ADAPTER-bindels", item.toString())
            val loc = viewModel.observeLocID().value!!

            // number of reservations remaining
            val numReservations = db
                    .collection("locations")
                    .document(loc)
                    .collection("timeslots")
                    .document(item.rowId)
                    .collection("reservations")
                    .get()

            numReservations.addOnSuccessListener {
                val numRes = it.size()
                val capacity  = db
                    .collection("locations")
                    .document(loc)
                    .get()

                if (currEmail.equals("owner@example.com")) {
                    bookBut.text = "VIEW\n $numRes BOOKED"
                }

                capacity.addOnSuccessListener {
                    val cap = it.getLong("capacity")!!
                    val remaining = cap - numRes
                    if (remaining <= 0){
                        // remove item from list
                        rowHolder.visibility = View.GONE
                        notifyItemRemoved(adapterPosition)
                    }
                    else{
                        if (!currEmail.equals("owner@example.com")) {
                            bookBut.text = "BOOK\n $remaining REMAINING"
                        }
                        // keep item in list
//                        itemView.visibility = View.VISIBLE
                        bookBut.setOnClickListener {
                            if (currEmail.equals("owner@example.com")) {
                                Log.d(TAG, "XXX clicked on view, timeslot: ${item.rowId}")
                                Toast.makeText(itemView.context,
                                        "Guestlist for ${item.startTime}", Toast.LENGTH_SHORT)
                                (itemView.context as FragmentActivity)
                                        .supportFragmentManager
                                        .beginTransaction()
                                        .replace(R.id.nav_host_fragment, UsersFragment.newInstance(loc, item.rowId))
                                        .addToBackStack("select")
                                        .commit()
                            } else {
                                Log.d(TAG, "XXX clicked on book, timeslot: ${item.rowId}")
                                val locID = viewModel.observeLocID().value
                                viewModel.addReservation(item, locID!!)
                                Toast.makeText(itemView.context,
                                        "Succesfully booked ${item.startTime}", Toast.LENGTH_SHORT)
                                (itemView.context as FragmentActivity)
                                        .supportFragmentManager
                                        .popBackStack("select", FragmentManager.POP_BACK_STACK_INCLUSIVE)



                                Log.d(TAG, "POPPED")
                            }
                        }
                    }
                }
            }


            val startSlot =  item.startTime.toString()
            val endSlot = item.endTime.toString()

            val sdf = SimpleDateFormat("MM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val startFormated = sdf.parse(startSlot)
            val endFormated = sdf.parse(endSlot)

            var startMinutes = startFormated.minutes.toString()
            if (startMinutes == "0"){
                startMinutes = "00"
            }

            var endMinutes = endFormated.minutes.toString()
            if (endMinutes == "0"){
                endMinutes = "00"
            }


            val dateText = (startFormated.month + 1).toString() + "/" +  startFormated.date.toString()
            val timeText = startFormated.hours.toString() + ":" + startMinutes + " to " + endFormated.hours.toString() + ":" + endMinutes


            timeslotText.text = dateText + "\n" + timeText

//            rowText.visibility = View.VISIBLE


        }

        fun bind(item: Timeslot?) {
            if (item == null) {
                Log.i("XXX-ADAPTER-bind", "NULL!")
                return
            }

            val dateFormat = SimpleDateFormat("MM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val time = dateFormat.parse(item.endTime)
//            if (time > Calendar.getInstance().time) {
            bindElements(
                    item,
                    timeslotText,
                    bookBut,
                    rowHolder
            )
//            } else {
//                rowHolder.visibility = View.GONE
//            }
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
