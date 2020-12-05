package edu.utap.gymfree.ui.dashboard_chat

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
import kotlinx.android.synthetic.main.fragment_chat_dashboard.*


class ChatDashboardFragment : Fragment() {
    private val TAG = "XXX-ChatDashboardFragment"
    private val viewModel: ChatDashboardViewModel by activityViewModels()
    private lateinit var ChatDashboardAdapter: ChatDashboardAdapter

    companion object {
        fun newInstance(locationID: String): ChatDashboardFragment {
            return ChatDashboardFragment()
        }
    }

    private fun initRecyclerView()  {
        ChatDashboardAdapter = ChatDashboardAdapter(viewModel)

        userRV.adapter = ChatDashboardAdapter
        userRV.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chat_dashboard, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        viewModel.getUsers()
        viewModel.observeUsers().observe(viewLifecycleOwner, Observer {
            Log.d(javaClass.simpleName, "Observe users $it")
            Log.i(TAG, it.toString())
            ChatDashboardAdapter.submitList(it)
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.getUsers()
    }
}