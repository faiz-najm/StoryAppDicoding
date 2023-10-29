package com.bangkit.storyappdicoding.ui.story

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.adapters.LoadingStateAdapter
import com.bangkit.storyappdicoding.adapters.StoryAdapter
import com.bangkit.storyappdicoding.data.remote.response.ListStoryItem
import com.bangkit.storyappdicoding.databinding.FragmentStoryBinding
import com.bangkit.storyappdicoding.viewmodels.StoryViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory

class StoryFragment : Fragment(), StoryAdapter.OnItemClickListener, MenuProvider {

    private val viewModel: StoryViewModel by lazy {
        val factory = ViewModelFactory.getInstance(requireContext())
        ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    private lateinit var myAdapter: StoryAdapter

    private var _binding: FragmentStoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        getStoryData()

        viewModel.sessionLiveData.observe(viewLifecycleOwner) { session ->
            if (!session.isLogin) {
                findNavController().navigate(
                    StoryFragmentDirections.actionStoryFragmentToAuthActivity()
                )
                requireActivity().finish()
            }
        }

        viewModel.storyPagingData.observe(viewLifecycleOwner) {
            myAdapter.submitData(lifecycle, it)

        }

        binding.fabAddStory.setOnClickListener {
            findNavController().navigate(StoryFragmentDirections.actionStoryFragmentToAddStoryFragment())
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (internetAvailable(requireContext())) {
                myAdapter.refresh()
                binding.rvStory.scrollToPosition(0)
            } else {
                showToast("No internet connection")
                binding.swipeRefresh.isRefreshing = false
            }
        }

        return binding.root
    }

    private fun internetAvailable(requireContext: Context): Boolean {
        val connectivityManager =
            requireContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    private fun getStoryData() {
        myAdapter = StoryAdapter()
        myAdapter.setOnItemClickListener(this)

        binding.rvStory.adapter = myAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                myAdapter.retry()
            }
        )

        myAdapter.addLoadStateListener { loadState ->
            binding.tvNotFound.isVisible =
                myAdapter.itemCount < 1 && loadState.source.refresh !is LoadState.Loading
            binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    override fun onResume() {
        super.onResume()
        myAdapter.retry()
        viewModel.getSession()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(
        items: ListStoryItem,
        ivItemPhoto: ImageView,
        tvItemName: TextView,
        tvItemDesc: TextView
    ) {
        // shared element transition
        val extras = FragmentNavigatorExtras(
            ivItemPhoto to items.photoUrl!!,
            tvItemName to items.name!!,
            tvItemDesc to items.description!!
        )

        findNavController().navigate(
            StoryFragmentDirections.actionStoryFragmentToDetailStoryFragment(items),
            extras
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.story_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                ViewModelFactory.refreshInstance()

                findNavController().navigate(
                    StoryFragmentDirections.actionStoryFragmentToAuthActivity()
                )
                requireActivity().finish()
                true
            }

            R.id.action_maps -> {
                findNavController().navigate(
                    StoryFragmentDirections.actionStoryFragmentToStoryLocationFragment()
                )
                true
            }

            else -> false
        }
    }


    private fun showToast(string: String) {
        Toast.makeText(
            requireContext(),
            string,
            Toast.LENGTH_SHORT
        ).show()
    }
}