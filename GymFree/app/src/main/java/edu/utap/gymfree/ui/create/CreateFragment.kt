package edu.utap.gymfree.ui.create

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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import edu.utap.gymfree.Location
import edu.utap.gymfree.R
import kotlin.properties.Delegates

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
        }
    }
}