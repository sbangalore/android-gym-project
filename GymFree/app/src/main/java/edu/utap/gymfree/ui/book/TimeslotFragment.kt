package edu.utap.gymfree.ui.book

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PlayGamesAuthProvider

import com.google.firebase.ktx.Firebase
import edu.utap.gymfree.R
import kotlinx.android.synthetic.main.fragment_timeslots.*
import kotlinx.android.synthetic.main.row_timeslot.*


class TimeslotFragment : Fragment() {
    private val TAG = "XXX-TimeslotFragment"
    private val viewModel: TimeslotViewModel by activityViewModels()
    private lateinit var TimeslotAdapter: TimeslotAdapter

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
        viewModel.getLocations(locationID)
        viewModel.observeLocations().observe(viewLifecycleOwner, Observer {
            Log.d(javaClass.simpleName, "Observe Chat $it")
            Log.i("XXX-DBFragment", it.toString())
            TimeslotAdapter.submitList(it)
        })


    }

    override fun onResume() {
        super.onResume()
        val locationID = arguments?.getString("locationID")!!
        viewModel.getLocations(locationID)
    }
}