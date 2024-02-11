package com.johnreg.to_doapp.fragments.list

import android.app.AlertDialog
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
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentListBinding
import com.johnreg.to_doapp.fragments.SharedViewModel
import com.johnreg.to_doapp.fragments.list.adapter.ListAdapter
import com.johnreg.to_doapp.utils.observeOnceOnly
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding

    private val adapter: ListAdapter by lazy { ListAdapter() }

    private val mToDoViewModel: ToDoViewModel by viewModels()

    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFabListener()
        setMenu()
        setRecyclerView()
        showViewsWhenDatabaseIsEmpty()
    }

    private fun setFabListener() = binding.floatingActionButton.setOnClickListener {
        // Use nav controllers to navigate to add fragment
        findNavController().navigate(R.id.action_listFragment_to_addFragment)
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflate the menu
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
                // Set the SearchView and Listener
                setSearchViewListener(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete_all -> {
                        showAlertDialogAndDeleteAll()
                        true
                    }
                    R.id.menu_priority_high -> {
                        mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
                            adapter.setData(it)
                        }
                        true
                    }
                    R.id.menu_priority_low -> {
                        mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) {
                            adapter.setData(it)
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setSearchViewListener(menu: Menu) {
        val searchView = menu.findItem(R.id.menu_search).actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchThroughDatabase(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchThroughDatabase(query)
                }
                return true
            }
        })
    }

    private fun searchThroughDatabase(query: String) {
        // Finds any values that have "query" in any position whether start, end, first, second, etc.
        val searchQuery = "%$query%"
        // Inside the searchDatabase Query pass this searchQuery and observe this LiveData
        // Whenever the data changes or we type something, the observer and adapter will be notified
        mToDoViewModel.searchDatabase(searchQuery).observeOnceOnly(viewLifecycleOwner) { data ->
            data?.let {
                adapter.setData(it)
            }
        }
    }

    private fun showAlertDialogAndDeleteAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete everything?")
            .setMessage("Are you sure you want to remove everything?")
            .setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteAll()
                Snackbar.make(binding.root, "Successfully Removed Everything!", Snackbar.LENGTH_LONG).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // Animate the RecyclerView, you need notifyItemRemoved and notifyItemChanged for it to work
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 200
        }
        /*
        The observer will monitor this LiveData object returned by getAllData
        Everytime the database has a change, the observer will get notified, then
        use that new data to set the adapter data and the RecyclerView will get updated

        Also the checkIfDatabaseEmpty function will run in SharedViewModel with the data passed in,
        then the MutableLiveData value will be set to the data passed in
         */
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        }
        // Swipe to Delete
        swipeToDelete()
    }

    private fun swipeToDelete() {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Store the item being swiped inside deletedItem and delete that item
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                val position = viewHolder.adapterPosition
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(position)
                // Restore deleted item
                restoreDeletedData(deletedItem, position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun restoreDeletedData(deletedItem: ToDoData, position: Int) {
        val snackbar = Snackbar.make(binding.root, "Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
        }
        snackbar.show()
    }

    private fun showViewsWhenDatabaseIsEmpty() {
        // Observe this MutableLiveData object and whenever its value changes run an if check
        // If the it boolean is true then show the Views, if false then hide the Views
        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner) {
            if (it) {
                binding.imageViewNoData.visibility = View.VISIBLE
                binding.textViewNoData.visibility = View.VISIBLE
            } else {
                binding.imageViewNoData.visibility = View.INVISIBLE
                binding.textViewNoData.visibility = View.INVISIBLE
            }
        }
    }

}