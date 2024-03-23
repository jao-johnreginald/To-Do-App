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
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentUpdateBinding
import com.johnreg.to_doapp.ui.sharedviewmodel.SharedViewModel

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding

    private val args: UpdateFragmentArgs by navArgs()

    private val mSharedViewModel: SharedViewModel by viewModels()

    private val mToDoViewModel: ToDoViewModel by viewModels()

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
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        updateItem()
                        true
                    }
                    R.id.menu_delete -> {
                        showAlertDialogAndDeleteItem()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateItem() {
        when {
            binding.etTitle.text.isEmpty() -> makeSnackbar("Please add a title.")
            else -> {
                val updatedItem = ToDoData(
                    id = args.currentItem.id,
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString(),
                    priority = mSharedViewModel.parseStringToPriority(binding.spinner.selectedItem.toString())
                )
                mToDoViewModel.updateItem(updatedItem)
                makeSnackbar("Successfully updated!")
                // Navigate back
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
        }
    }

    private fun showAlertDialogAndDeleteItem() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete '${args.currentItem.title}'?")
            .setMessage("Are you sure you want to remove '${args.currentItem.title}'?")
            .setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteItem(args.currentItem)
                makeSnackbar("Successfully Removed: ${args.currentItem.title}")
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun makeSnackbar(text: String) = Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

    private fun setUI() {
        binding.etTitle.setText(args.currentItem.title)
        binding.etDescription.setText(args.currentItem.description)
        binding.spinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        binding.spinner.onItemSelectedListener = mSharedViewModel.spinnerListener
    }

}