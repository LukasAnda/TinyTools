package com.tinytools.files.ui.files.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.tinytools.common.fragments.BaseFragment
import com.tinytools.common.recyclical.datasource.dataSourceOf
import com.tinytools.common.recyclical.handle.RecyclicalHandle
import com.tinytools.common.recyclical.setup
import com.tinytools.common.recyclical.withItem
import com.tinytools.files.databinding.FilesFragmentBinding
import com.tinytools.files.databinding.FilesItemGridBinding
import com.tinytools.files.databinding.FilesItemLinearBinding
import com.tinytools.files.model.ui.HybridFileItem
import com.tinytools.files.model.ui.PageStyle
import com.tinytools.files.ui.files.viewmodel.FilesFragmentViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilesFragment : BaseFragment<FilesFragmentBinding>() {
    override fun getViewBinding() = FilesFragmentBinding.inflate(layoutInflater)
    private val viewModel by viewModel<FilesFragmentViewModel>()

    private var directoryItems = dataSourceOf()

    private var recyclicalHandle: RecyclicalHandle? = null
    private var manager: GridLayoutManager? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manager = GridLayoutManager(context, 1)

        initRecycler()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.navigateUp()
            }
        })

        binding?.swap?.setOnClickListener {
            viewModel.changeDirectoryStyle()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestPermissions {

            viewModel.pageItems().observe(viewLifecycleOwner, {
                directoryItems.set(it, ::areTheSame, ::areContentsTheSame)
                recyclicalHandle?.getAdapter()?.notifyItemRangeChanged(0, directoryItems.size())
            })

            viewModel.pageStyle().observe(viewLifecycleOwner, {
                // TODO adjust dynamic span count based on screen width
                when (it) {
                    PageStyle.List -> manager?.spanCount = 1
                    PageStyle.Grid -> manager?.spanCount = 3
                }
            })

            lifecycleScope.launch {
                viewModel.savedDirectories().let {
                    viewModel.listFiles(it)
                }
            }

            viewModel.getDrawerConfiguration()

            viewModel.configuration().observe(viewLifecycleOwner, {
                setConfiguration(it)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclicalHandle = null
        manager = null
    }

    private fun areTheSame(left: Any, right: Any): Boolean {
        return when(left){
            is HybridFileItem -> left.areTheSame(right)
            else -> false
        }
    }

    private fun areContentsTheSame(left: Any, right: Any): Boolean {
        return when(left){
            is HybridFileItem -> left.areContentsTheSame(right)
            else -> false
        }
    }

    private fun requestPermissions(onSuccess: () -> Unit) {
        requestPermissions(0, 0, 0, listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), onSuccess)
    }

    private fun initRecycler() {
        recyclicalHandle = binding?.recycler?.setup {
            withDataSource(directoryItems)
            manager?.let { withLayoutManager(it) }
            withItem<HybridFileItem.HybridFileLinearItem, FilesItemLinearBinding>(FilesItemLinearBinding::inflate) {
                onBind { binding, index, item ->
                    binding.text.text = item.name
                    binding.size.text = item.size
                }

                onClick {
                    viewModel.listFiles(item.file)
                }
            }

            withItem<HybridFileItem.HybridFileGridItem, FilesItemGridBinding>(FilesItemGridBinding::inflate) {
                onBind { binding, index, item ->
                    binding.text.text = item.name
                }

                onClick {
                    viewModel.listFiles(item.file)
                }
            }
        }
    }
}
