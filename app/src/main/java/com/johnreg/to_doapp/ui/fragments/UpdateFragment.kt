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
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentUpdateBinding
import com.johnreg.to_doapp.ui.sharedviewmodel.SharedViewModel
import com.johnreg.to_doapp.utils.hideKeyboard

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding

    private val args: UpdateFragmentArgs by navArgs()

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
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
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        if (isNotItemEqualText()) {
                            showDialogAndSaveChanges()
                            true
                        } else {
                            hideKeyboard()
                            false
                        }
                    }

                    R.id.menu_save -> {
                        updateItem()
                        true
                    }

                    R.id.menu_delete -> {
                        showDialogAndDeleteItem()
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
                updateItem()
            }
            .setNegativeButton("Don't Save") { _, _ ->
                // Hide keyboard, show Snackbar, navigate back
                hideKeyboard()
                mSharedViewModel.showSnackbarAndDismiss(
                    "Changes not saved.",
                    binding.root
                )
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun updateItem() {
        if (binding.etTitle.text.isEmpty()) {
            hideKeyboard()
            Snackbar.make(binding.root, "Please add a title.", Snackbar.LENGTH_SHORT).show()
        } else {
            val updatedItem = ToDoData(
                id = args.currentItem.id,
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                priority = mSharedViewModel.parseStringToPriority(binding.spinner.selectedItem.toString())
            )
            mToDoViewModel.updateItem(updatedItem)
            // Hide keyboard, show Snackbar, navigate back
            hideKeyboard()
            mSharedViewModel.showSnackbarAndDismiss("Updated: ${updatedItem.title}", binding.root)
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
    }

    private fun showDialogAndDeleteItem() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete [ ${args.currentItem.title} ]?")
            .setMessage("[ ${args.currentItem.title} ] will be deleted. Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteItem(args.currentItem)
                // Hide keyboard, show Snackbar, navigate back
                hideKeyboard()
                mSharedViewModel.showSnackbarAndDismiss(
                    "Deleted: ${args.currentItem.title}",
                    binding.root
                )
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun setUI() {
        // Set the texts
        binding.etTitle.setText(args.currentItem.title)
        binding.etDescription.setText(args.currentItem.description)
        // Set the spinner selection and color
        binding.spinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        binding.spinner.onItemSelectedListener = mSharedViewModel.spinnerListener
        // Intercept the back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isNotItemEqualText()) {
                showDialogAndSaveChanges()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun isNotItemEqualText(): Boolean {
        return args.currentItem.title !=
                binding.etTitle.text.toString()
                ||
                args.currentItem.description !=
                binding.etDescription.text.toString()
                ||
                args.currentItem.priority !=
                mSharedViewModel.parseStringToPriority(binding.spinner.selectedItem.toString())
    }

}