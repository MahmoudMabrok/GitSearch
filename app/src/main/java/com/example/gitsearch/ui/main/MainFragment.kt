package com.example.gitsearch.ui.main

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gitsearch.databinding.FragmentMainBinding
import com.example.gitsearch.utilities.InjectorUtils


class MainFragment : Fragment() {

    val viewModel: MainViewModel by lazy{
        val factory = InjectorUtils.provideQuotesViewModelFactory()
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private var recyclerViewState:Parcelable? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = UsersAdapter(emptyList())

    //private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerview = binding.recyclerview
        //recyclerview.layoutManager = LinearLayoutManager(this.context)
        recyclerview.adapter = adapter

        viewModel.getUsers().observe(viewLifecycleOwner, Observer{ users ->
            adapter.updateList(users)

            //Return to scrolling position
            if(recyclerViewState != null)
            {
                binding.recyclerview.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
        })


        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE)
                {
                    //load more only if count is divisible by 30
                    if(recyclerView.layoutManager?.itemCount != null && recyclerView.layoutManager?.itemCount!!%30 == 0)
                    {
                        recyclerViewState = recyclerView.layoutManager!!.onSaveInstanceState()
                        viewModel.loadMore()
                    }
                }
            }
        })

        binding.buttonSearchUsers.setOnClickListener {
            viewModel.searchUsers(binding.editTextSearch.text.toString(), false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        val factory = InjectorUtils.provideQuotesViewModelFactory()
//        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

    }

}