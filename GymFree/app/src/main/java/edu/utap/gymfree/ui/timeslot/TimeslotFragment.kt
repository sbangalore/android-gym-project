package edu.utap.gymfree.ui.timeslot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth

import edu.utap.gymfree.R
import kotlinx.android.synthetic.main.fragment_timeslots.*


class TimeslotFragment : Fragment() {
    private val TAG = "XXX-TimeslotFragment"
    private val viewModel: TimeslotViewModel by activityViewModels()
    private lateinit var TimeslotAdapter: TimeslotAdapter
    val currEmail = FirebaseAuth.getInstance().currentUser?.email

    companion object {
        fun newInstance(locationID: String): TimeslotFragment {
            val fragment = TimeslotFragment()

            val bundle = Bundle().apply {
                putString("locationID", locationID)
            }

            fragment.arguments = bundle

            return fragment
        }
    }

    private fun initRecyclerView()  {
        TimeslotAdapter = TimeslotAdapter(viewModel)

        timeslotRV.adapter = TimeslotAdapter
        timeslotRV.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timeslots, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        val locationID = arguments?.getString("locationID")!!
        Log.i(TAG, locationID)
        viewModel.getTimeslots(locationID)
        viewModel.observeTimeslots().observe(viewLifecycleOwner, Observer {
            Log.d(javaClass.simpleName, "Observe timeslots $it")
            Log.i(TAG, it.toString())
            TimeslotAdapter.submitList(it)
        })
        if (currEmail.equals(resources.getString(R.string.owner_email))) {
            TimeslotsTitle.text = "Select the timeslot for which you'd like to view the guest list."
        }

    }

    override fun onResume() {
        super.onResume()
        val locationID = arguments?.getString("locationID")!!
        viewModel.getTimeslots(locationID)
    }
}