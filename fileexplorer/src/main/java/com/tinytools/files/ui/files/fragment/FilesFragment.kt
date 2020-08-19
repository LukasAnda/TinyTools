package com.tinytools.files.ui.files.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.tinytools.common.fragments.BaseFragment
import com.tinytools.files.databinding.FilesFragmentBinding
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.model.ui.PageConfig
import com.tinytools.files.model.ui.PageStyle
import com.tinytools.files.ui.files.adapters.FilesListAdapter
import com.tinytools.files.ui.files.adapters.FilesPagerAdapter
import com.tinytools.files.ui.files.viewmodel.FilesFragmentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilesFragment : BaseFragment<FilesFragmentBinding>(){
    override fun getViewBinding() = FilesFragmentBinding.inflate(layoutInflater)
    private val viewModel by viewModel<FilesFragmentViewModel>()

    private var adapter: FilesPagerAdapter? = null
    private var currentPage: Int = 0

    //region Listeners
    private val fileListHandler: FilesListAdapter.FilesListAdapterHandler = object : FilesListAdapter.FilesListAdapterHandler {
        override fun onFileSelected(path: HybridFile) {
            viewModel.listFiles(currentPage, path)
        }
    }
    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val parentDirectoryPath = viewModel.directory(currentPage)?.parent(requireContext()) ?: ""
                viewModel.listFiles(currentPage, HybridFile(parentDirectoryPath).getTypedFile(requireContext()))
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        requestPermissions(0,0,0, listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            adapter?.swapData(createPageConfigs(viewModel.pageCount()))

            viewModel.pageItems().observe(viewLifecycleOwner, Observer {
                adapter?.swapPageData(it.first, it.second)
            })

            lifecycleScope.launch {
                viewModel.savedDirectories().forEachIndexed { index, hybridFile ->
                    viewModel.listFiles(index, hybridFile)
                    delay(200)
                }
            }
        }
    }

    private fun initViewPager(){
        adapter = FilesPagerAdapter(object : FilesPagerAdapter.FilesPageAdapterHandler {})

        binding?.pager?.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding?.pager?.adapter = adapter

        binding?.pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }
        })
    }

    private fun createPageConfigs(numberOfPages: Int): List<PageConfig>{
        val allPages = mutableListOf<PageConfig>()
        repeat(numberOfPages){
            val layoutManager = GridLayoutManager(context, 1)
            val adapter = FilesListAdapter(layoutManager, fileListHandler)
            allPages.add(PageConfig(layoutManager, adapter, PageStyle.List))
        }

        return allPages
    }
}
