package edu.utap.gymfree.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.utap.gymfree.R

class CreateFragment : Fragment() {

    private lateinit var createViewModel: CreateViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        createViewModel =
            ViewModelProvider(this).get(CreateViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_create, container, false)
        val textView: TextView = root.findViewById(R.id.text_create)
        createViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}