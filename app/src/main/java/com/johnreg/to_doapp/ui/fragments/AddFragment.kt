package com.johnreg.to_doapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentAddBinding
import com.johnreg.to_doapp.ui.sharedviewmodel.SharedViewModel
import com.johnreg.to_doapp.utils.hideKeyboard
import com.johnreg.to_doapp.utils.showSnackbarPleaseAddATitle

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
        setUI()
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        if (isNotEmptyTexts()) {
                            showDialogAndSaveChanges()
                            true
                        } else {
                            hideKeyboard()
                            false
                        }
                    }

                    R.id.menu_add -> {
                        createNewItem()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDialogAndSaveChanges() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Do you want to save your changes?")
            .setPositiveButton("Save") { _, _ ->
                createNewItem()
            }
            .setNegativeButton("Don't Save") { _, _ ->
                // Hide keyboard, show Snackbar, navigate back
                hideKeyboard()
                mSharedViewModel.showSnackbarAndDismiss(
                    "Changes not saved.",
                    binding.root
                )
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun createNewItem() {
        if (binding.etTitle.text.isEmpty()) {
            hideKeyboard()
            showSnackbarPleaseAddATitle()
        } else {
            val newItem = ToDoData(
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                priority = mSharedViewModel.parseStringToPriority(binding.spinner.selectedItem.toString())
            )
            mToDoViewModel.createItem(newItem)
            // Hide keyboard, show Snackbar, navigate back
            hideKeyboard()
            mSharedViewModel.showSnackbarAndDismiss("Added: ${binding.etTitle.text}", binding.root)
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
    }

    private fun setUI() {
        // Set the Spinner color
        binding.spinner.onItemSelectedListener = mSharedViewModel.spinnerListener
        // Intercept the back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isNotEmptyTexts()) {
                showDialogAndSaveChanges()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun isNotEmptyTexts(): Boolean {
        return binding.etTitle.text.isNotEmpty() || binding.etDescription.text.isNotEmpty()
    }

}