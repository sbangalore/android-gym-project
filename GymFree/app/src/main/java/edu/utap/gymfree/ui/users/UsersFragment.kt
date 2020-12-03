package edu.utap.gymfree.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.gymfree.R
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment() {
    private val viewModel: UsersViewModel by activityViewModels()
    private lateinit var UsersAdapter: UsersAdapter

    companion object {
        private val TAG = "XXX-UsersFragment"
        fun newInstance(locationID: String, timeslotID: String): UsersFragment {
            val fragment = UsersFragment()
            Log.i(TAG, "Fragment created")

            val bundle = Bundle().apply {
                putString("locationID", locationID)
                putString("timeslotID", timeslotID)
            }

            fragment.arguments = bundle

            return fragment
        }
    }

    private fun initRecyclerView()  {
        UsersAdapter = UsersAdapter(viewModel)
        Log.i(TAG, "Adapter initialized")

        usersRV.adapter = UsersAdapter
        usersRV.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "inflate view")
        val root = inflater.inflate(R.layout.fragment_users, container, false)
        Log.i(TAG, "view inflated")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "init recycler view")
        initRecyclerView()
        Log.i(TAG, "recycler view initialized")

        val locationID = arguments?.getString("locationID")!!
        val timeslotID = arguments?.getString("timeslotID")!!
        Log.i(TAG, "$locationID $timeslotID")
        viewModel.getUsers(locationID, timeslotID)
        Log.i(TAG, "get users finished")

        viewModel.observeUsers().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "Observe users $it")
            Log.i(TAG, it.toString())
            UsersAdapter.submitList(it)
        })
    }

    override fun onResume() {
        super.onResume()
        val locationID = arguments?.getString("locationID")!!
        val timeslotID = arguments?.getString("timeslotID")!!
        viewModel.getUsers(locationID, timeslotID)
    }
}