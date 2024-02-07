package com.johnreg.to_doapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentListBinding
import com.johnreg.to_doapp.fragments.SharedViewModel

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

        setOnClickListeners()
        setMenu()
        setRecyclerView()
        showViewsWhenDatabaseIsEmpty()
    }

    private fun setOnClickListeners() {
        binding.floatingActionButton.setOnClickListener {
            // Use nav controllers to navigate to add fragment
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete_all -> {
                        showAlertDialogAndDeleteAll()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
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