package com.tinytools.files.ui.files.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.afollestad.materialcab.createCab
import com.tinytools.common.fragments.BaseFragment
import com.tinytools.common.helpers.getNavigationResult
import com.tinytools.common.helpers.onBackPressedTwiceFinish
import com.tinytools.common.model.Event
import com.tinytools.common.recyclical.datasource.selectableDataSourceOf
import com.tinytools.common.recyclical.datasource.selectableDataSourceTypedOf
import com.tinytools.common.recyclical.handle.RecyclicalHandle
import com.tinytools.common.recyclical.setup
import com.tinytools.common.recyclical.viewholder.hasSelection
import com.tinytools.common.recyclical.viewholder.isSelected
import com.tinytools.common.recyclical.withItem
import com.tinytools.common.views.DrawerView
import com.tinytools.files.R
import com.tinytools.files.data.ui.Directory
import com.tinytools.files.data.ui.HybridFileItem
import com.tinytools.files.data.ui.PageViewStyle
import com.tinytools.files.databinding.FilesFragmentBinding
import com.tinytools.files.databinding.FilesItemGridBinding
import com.tinytools.files.databinding.FilesItemLinearBinding
import com.tinytools.files.helpers.MimeType
import com.tinytools.files.helpers.getColor
import com.tinytools.files.helpers.getIcon
import com.tinytools.files.helpers.px
import com.tinytools.files.ui.files.dialogs.SORT_BY_KEY
import com.tinytools.files.ui.files.dialogs.SortDialog
import com.tinytools.files.ui.files.viewmodel.FilesFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class FilesFragment : BaseFragment<FilesFragmentBinding>(), DrawerView.DrawerHandler {
    override fun getViewBinding() = FilesFragmentBinding.inflate(layoutInflater)
    private val viewModel by viewModel<FilesFragmentViewModel>()

    private var directoryItems = selectableDataSourceTypedOf<HybridFileItem>().apply {
        onSelectionChange { invalidateCab() }
    }

    private var cab: AttachedCab? = null

    private var recyclicalHandle: RecyclicalHandle? = null
    private var manager: GridLayoutManager? = null
    private var toggle: ActionBarDrawerToggle? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.run {
            setSupportActionBar(binding?.toolbar)
            toggle = ActionBarDrawerToggle(this, binding?.root, R.string.open_drawer, R.string.close_drawer)
            binding?.root?.addDrawerListener(toggle!!)
            toggle?.syncState()
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
        this.setHasOptionsMenu(true)


        manager = GridLayoutManager(context, 1)

        initRecycler()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(directoryItems.hasSelection()) {
                    directoryItems.deselectAll()
                    return
                }
                if (closeDrawerIfPossible()) return
                if (!viewModel.navigateUp()) {
                    activity?.onBackPressedTwiceFinish(R.string.exit_app_prompt)
                }
            }
        })

        binding?.refreshLayout?.setOnRefreshListener {
            binding?.refreshLayout?.isRefreshing = false
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
                activity?.invalidateOptionsMenu()
                // TODO adjust dynamic span count based on screen width
                when (it.viewStyle) {
                    PageViewStyle.List -> manager?.spanCount = 1
                    PageViewStyle.Grid -> manager?.spanCount = 3
                }
            })

            viewModel.currentDirectory().observe(viewLifecycleOwner, {
                viewModel.listFiles(it)
            })

            viewModel.getDrawerConfiguration()

            viewModel.configuration().observe(viewLifecycleOwner, {
                binding?.drawer?.reloadConfiguration(it.apply { handler = this@FilesFragment })
            })

            viewModel.events().observe(viewLifecycleOwner, {
                when (it) {
                    Event.Loading -> {
                        binding?.refreshLayout?.isRefreshing = true
                    }
                    is Event.Failure -> {
                        binding?.refreshLayout?.isRefreshing = false
                    }
                    Event.Success -> {
                        binding?.refreshLayout?.isRefreshing = false
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.files__menu, menu)
        when (viewModel.pageStyle().value?.viewStyle) {
            PageViewStyle.List -> {
                menu.findItem(R.id.list).isVisible = false
                menu.findItem(R.id.grid).isVisible = true
            }
            PageViewStyle.Grid -> {
                menu.findItem(R.id.list).isVisible = true
                menu.findItem(R.id.grid).isVisible = false
            }
            null -> {
                menu.findItem(R.id.list).isVisible = false
                menu.findItem(R.id.grid).isVisible = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        when (item.itemId) {
            R.id.list, R.id.grid -> {
                viewModel.changeDirectoryStyle()
                return true
            }
            R.id.sort -> {
                manageSortType()
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclicalHandle = null
        manager = null
        toggle = null
    }

    override fun onItemSelected(item: DrawerView.Item) {
        when (val payload = item.item) {
            is Directory -> {
                directoryItems.set(emptyList())
                viewModel.changeDirectory(payload)
            }
        }
        closeDrawerIfPossible()
    }

    private fun areTheSame(left: Any, right: Any): Boolean {
        return when (left) {
            is HybridFileItem -> left.areTheSame(right)
            else -> false
        }
    }

    private fun areContentsTheSame(left: Any, right: Any): Boolean {
        return when (left) {
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

                    // Default path is empty string so Coil will fail and show error. This allows us to omit if-else
                    binding.icon.load(File(item.file.path)) {
                        diskCachePolicy(CachePolicy.ENABLED)
                        memoryCachePolicy(CachePolicy.ENABLED)
                        error(item.type.getIcon())
                        if (item.type == MimeType.Image || item.type == MimeType.Video) {
                            transformations(CircleCropTransformation())
                        }
                        target(
                                onError = { drawable ->
                                    binding.iconBackground.setBackgroundColor(item.type.getColor(resources))
                                    binding.icon.setImageDrawable(drawable)
                                    binding.icon.setPadding(8.px)
                                },

                                onSuccess = { drawable ->
                                    binding.iconBackground.setBackgroundColor(Color.TRANSPARENT)
                                    binding.icon.setImageDrawable(drawable)
                                    binding.icon.setPadding(0)
                                }
                        )
                    }

                    binding.root.setBackgroundColor(if(isSelected()) Color.parseColor("#e0e0e0") else Color.TRANSPARENT)
                }

                onClick {
                    if (hasSelection()) {
                        toggleSelection()
                    } else {
                        viewModel.listFiles(item.file)
                    }
                }

                onLongClick {
                    toggleSelection()
                }
            }

            withItem<HybridFileItem.HybridFileGridItem, FilesItemGridBinding>(FilesItemGridBinding::inflate) {
                onBind { binding, index, item ->
                    binding.text.text = item.name
                    // Default path is empty string so Coil will fail and show error. This allows us to omit if-else
                    binding.icon.load(File(item.file.path)) {
                        diskCachePolicy(CachePolicy.ENABLED)
                        memoryCachePolicy(CachePolicy.ENABLED)
                        error(item.type.getIcon())
                        target(
                                onError = { drawable ->
                                    binding.iconBackground.setBackgroundColor(item.type.getColor(resources))
                                    binding.icon.setImageDrawable(drawable)
                                    binding.icon.setPadding(32.px)
                                },

                                onSuccess = { drawable ->
                                    binding.iconBackground.setBackgroundColor(Color.TRANSPARENT)
                                    binding.icon.setImageDrawable(drawable)
                                    binding.icon.setPadding(16.px)
                                }
                        )
                    }

                    binding.root.setBackgroundColor(if(isSelected()) Color.parseColor("#e0e0e0") else Color.TRANSPARENT)
                }

                onClick {
                    if (hasSelection()) {
                        toggleSelection()
                    } else {
                        viewModel.listFiles(item.file)
                    }
                }

                onLongClick {
                    toggleSelection()
                }
            }
        }
    }

    private fun closeDrawerIfPossible(): Boolean {
        if (binding?.root?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.root?.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    private fun invalidateCab() {
        if (directoryItems.hasSelection()) {
            if (cab.isActive()) {
                cab?.apply {
                    title(literal = getString(R.string.x_items, directoryItems.getSelectionCount()))
                }
            } else {
                cab = createCab(R.id.cab_stub) {
                    title(literal = getString(R.string.x_items, directoryItems.getSelectionCount()))
                    backgroundColor(literal = Color.BLACK)
                    fadeIn()
                    onDestroy {
                        directoryItems.deselectAll()
                        true
                    }
                }
            }
        } else {
            cab.destroy()
        }
    }

    private fun manageSortType() {
        findNavController().navigate(FilesFragmentDirections.actionFilesFragmentToSortDialog())

        getNavigationResult<SortDialog.SortDialogResult>(R.id.filesFragment, SORT_BY_KEY) {
            directoryItems.deselectAll()
            viewModel.changeSortStyle(it.sortType, it.sortOrder)
        }
    }
}
