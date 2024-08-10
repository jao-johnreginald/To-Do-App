package com.johnreg.to_doapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentListBinding
import com.johnreg.to_doapp.ui.adapter.ListAdapter
import com.johnreg.to_doapp.ui.sharedviewmodel.SharedViewModel
import com.johnreg.to_doapp.utils.hideKeyboard
import com.johnreg.to_doapp.utils.observeOnceOnly
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var _listAdapter: ListAdapter? = null
    private val listAdapter get() = _listAdapter!!

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _listAdapter = ListAdapter()

        setMenu()
        setFabAndRecyclerView()
        setSwipeToDelete()
    }

    // Fixes adapter still observing getSearchedItems after app resumes from background
    override fun onResume() {
        super.onResume()
        observeAllData()
    }

    // Fixes FragmentListBinding and ListAdapter memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _listAdapter = null
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
                // Set the SearchView and Listener
                setSearchViewAndListener(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.menu_delete_all -> {
                        // Show the alert dialog and delete all items
                        showDialogAndDeleteAllItems()
                        true
                    }

                    R.id.menu_priority_high -> {
                        // Sort by high priority
                        mToDoViewModel.sortByHighPriority.observeOnceOnly(
                            viewLifecycleOwner
                        ) { sortByHighPriority ->
                            listAdapter.setData(sortByHighPriority)
                        }
                        true
                    }

                    R.id.menu_priority_low -> {
                        // Sort by low priority
                        mToDoViewModel.sortByLowPriority.observeOnceOnly(
                            viewLifecycleOwner
                        ) { sortByLowPriority ->
                            listAdapter.setData(sortByLowPriority)
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setSearchViewAndListener(menu: Menu) {
        val searchView = menu.findItem(R.id.menu_search).actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Hide keyboard
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // If query is not null, search through the database
                if (query != null) searchThroughDatabase(query)
                return true
            }
        })
    }

    private fun searchThroughDatabase(query: String) {
        // Finds any values that have "query" in any position whether start, end, first, second, etc.
        val searchQuery = "%$query%"
        // Inside the searchDatabase Query pass this searchQuery and observe this LiveData
        // Whenever the data changes or we type something, the observer and adapter will be notified
        mToDoViewModel.getSearchedItems(searchQuery).observeOnceOnly(
            viewLifecycleOwner
        ) { searchedItems ->
            listAdapter.setData(searchedItems)
        }
    }

    private fun showDialogAndDeleteAllItems() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_everything_title))
            .setMessage(getString(R.string.delete_everything_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                // Store the dataList inside deletedItems and delete all items
                mToDoViewModel.getAllItems.observeOnceOnly(viewLifecycleOwner) { deletedItems ->
                    // Restore deleted dataList
                    showSnackbarAndRestoreAllItems(deletedItems)
                }
                mToDoViewModel.deleteAllItems()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showSnackbarAndRestoreAllItems(deletedItems: List<ToDoData>) {
        val snackbar = Snackbar.make(
            binding.root, getString(R.string.successfully_removed_everything), Snackbar.LENGTH_LONG
        )
        snackbar.setAction(getString(R.string.undo)) {
            for (i in deletedItems.indices) mToDoViewModel.createItem(deletedItems[i])
        }
        snackbar.show()
    }

    private fun setFabAndRecyclerView() {
        binding.fab.setOnClickListener {
            // Use nav controllers to navigate to add fragment
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
        binding.recyclerView.apply {
            // Adapter
            adapter = listAdapter
            // Layout Manager - 2 columns, VERTICAL orientation
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            // Animate the RecyclerView
            itemAnimator = SlideInUpAnimator()
        }
    }

    private fun setSwipeToDelete() {
        // 0 because we're not going to drag, only swipe. LEFT because we're going to swipe left
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            // Return false because we're not going to move our items
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            // Swipe to delete
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Store the item being swiped inside deletedItem and delete that item
                val deletedItem = listAdapter.getCurrentItem(viewHolder.adapterPosition)
                mToDoViewModel.deleteItem(deletedItem)
                // Restore deleted item
                showSnackbarAndRestoreItem(deletedItem)
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun showSnackbarAndRestoreItem(deletedItem: ToDoData) {
        val snackbar = Snackbar.make(
            binding.root, "Deleted: ${deletedItem.title}", Snackbar.LENGTH_LONG
        )
        snackbar.setAction(getString(R.string.undo)) { mToDoViewModel.createItem(deletedItem) }
        snackbar.show()
    }

    private fun observeAllData() {
        // Observe this getAllItems LiveData, everytime it changes, get the newDataList
        // Set the listAdapter data and the isDataEmpty MutableLiveData
        mToDoViewModel.getAllItems.observe(viewLifecycleOwner) { newDataList ->
            listAdapter.setData(newDataList)
            mSharedViewModel.setMutableLiveData(newDataList)
        }
        // Observe this MutableLiveData object and whenever its value changes run an if check
        // If the boolean is true then show the Views, if false then hide the Views
        mSharedViewModel.isDatabaseEmpty.observe(viewLifecycleOwner) { isDatabaseEmpty ->
            if (isDatabaseEmpty) {
                binding.imageNoData.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.imageNoData.visibility = View.INVISIBLE
                binding.tvNoData.visibility = View.INVISIBLE
            }
        }
    }

}