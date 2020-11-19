package edu.utap.gymfree.ui.create

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.gymfree.Location
import edu.utap.gymfree.R
import java.util.*
import kotlin.math.floor

class CreateFragment : Fragment() {
    val TAG = "XXX-CreateFragment"

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var createViewModel: CreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createViewModel =
            ViewModelProvider(this).get(CreateViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_create, container, false)
        val tv = root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textField)
        val lb = root.findViewById<com.google.android.material.button.MaterialButton>(R.id.locationNameButton)

        tv.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == EditorInfo.IME_ACTION_DONE) {
                locationNameListener(root)
            }
            false
        }

        lb.setOnClickListener {
            locationNameListener(root)
        }

        return root
    }

    fun locationNameListener(root: View) {
        val tv = root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textField)
        val av = root.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.addressField)
        val cv = root.findViewById<com.google.android.material.slider.Slider>(R.id.slider)
        val rs = root.findViewById<com.google.android.material.slider.RangeSlider>(R.id.sliderOpening)

        var name = tv.editText?.text.toString()
        var address = av.editText?.text.toString()
        var capacity = cv.value
        var valid = true
        var materialTimePicker: MaterialTimePicker
        av.error = null
        tv.error = null

        if (name == null || name == "") {
            tv.error = "Please provide a name."
            valid = false
        }

        if (address == null || address == "") {
            av.error = "Please provide an address."
            valid = false
        }

        if (valid) {
            Log.i(TAG, "$name || capacity: ${capacity} ||${address} || hours: ${rs.valueFrom}:${rs.valueTo} received")
            val user = FirebaseAuth.getInstance().currentUser
            val location = Location().apply {
                ownerUid = user?.uid
                pictureUUID = null
            }
            location.openingTime = rs.values[0]
            location.closingTime = rs.values[1]
            location.name = name
            location.address = address
            location.capacity = capacity
            location.rowID = db.collection("locations").document().id

            Log.i(TAG, location.name)
            Log.i(TAG, location.address)
            Log.i(TAG, location.capacity.toString())

            db
                .collection("locations")
                .document(location.rowID)
                .set(location)
                .addOnSuccessListener {
                    Toast.makeText(
                        activity?.applicationContext,
                        "$name successfully created.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    activity?.supportFragmentManager?.popBackStack();
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                    Toast.makeText(activity?.applicationContext, "Error creating location. Please try again.", Toast.LENGTH_SHORT).show()
                }
            // add subcollections
            // loop for 14 days
            for(i in 0 until 14){
                // open and close times
                val open  = rs.values[0]
                val close = rs.values[1]

                // calendar instance
                val cal = Calendar.getInstance()

                // todays date
                val month = cal.get(Calendar.MONTH) + 1 // calendar months are indexed at 0 (JAN)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val sdf = SimpleDateFormat("MM dd HH:mm:ss z yyyy", Locale.ENGLISH)

                // start
                var t = sdf.parse("$month $day ${open.toInt()}:00:00 EST 2020") // all done
                // add 30 minutes if you open at X:30
                if (open % 1 != 0f){
                    t = sdf.parse("$month $day ${open.toInt()}:30:00 EST 2020")
                }

                cal.time = t
                // add by the day we are on
                cal.add(Calendar.DATE, i)
                var startTime = sdf.format(cal.time)
                Log.d(TAG, "time: ${cal.time}")
                // add one hour
                cal.add(Calendar.HOUR, 1)
                var endTime = sdf.format(cal.time)
                val numSlots = floor(close - open)
                Log.d(TAG, "START: $startTime")
                Log.d(TAG, "END: $endTime")
                for (i in 0 until numSlots.toInt()){
                    val stamp1 = mapOf(
                            "startTime" to startTime,
                            "endTime" to endTime,
                            "rowId" to db.collection("timeslots").document().id
                    )
                    // add subcollection timeslot
                    db
                            .collection("locations")
                            .document(location.rowID)
                            .collection("timeslots")
                            .add(stamp1)
                    // increment one hour
                    startTime = endTime
                    cal.add(Calendar.HOUR, 1)
                    endTime = sdf.format(cal.time)

                    Log.d(TAG, "START: $startTime")
                    Log.d(TAG, "END: $endTime")

                }
            }

        }
    }
}