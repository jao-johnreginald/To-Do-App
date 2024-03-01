package com.johnreg.to_doapp.ui.fragments

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
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentAddBinding
import com.johnreg.to_doapp.ui.sharedviewmodel.SharedViewModel

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    private val mToDoViewModel: ToDoViewModel by viewModels()

    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        setSpinner()
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add -> {
                        insertData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun insertData() {
        when {
            binding.etTitle.text.isEmpty() -> makeSnackbar("Please add a title.")
            else -> {
                val newData = ToDoData(
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString(),
                    priority = mSharedViewModel.parseStringToPriority(binding.spinner.selectedItem.toString())
                )
                mToDoViewModel.createItem(newData)
                makeSnackbar("Successfully added!")
                // Navigate back
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            }
        }
    }

    private fun makeSnackbar(text: String) = Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

    private fun setSpinner() { binding.spinner.onItemSelectedListener = mSharedViewModel.spinnerListener }

}