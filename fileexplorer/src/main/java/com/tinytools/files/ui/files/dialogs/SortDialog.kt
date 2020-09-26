package com.tinytools.files.ui.files.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.tinytools.common.fragments.BaseDialogFragment
import com.tinytools.common.helpers.setNavigationResult
import com.tinytools.files.R
import com.tinytools.files.data.ui.PageSortOrder
import com.tinytools.files.data.ui.PageSortType
import com.tinytools.files.databinding.SortDialogFragmentBinding
import kotlinx.android.parcel.Parcelize

const val SORT_BY_KEY = "SORT_BY_KEY"

class SortDialog : BaseDialogFragment<SortDialogFragmentBinding>() {
    override fun getViewBinding() = SortDialogFragmentBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavigationResult(SORT_BY_KEY, null)

        binding?.ascending?.setOnClickListener {
            setNavigationResult(SORT_BY_KEY, SortDialogResult(getSortType(), PageSortOrder.Ascending))
            dismiss()
        }

        binding?.descending?.setOnClickListener {
            setNavigationResult(SORT_BY_KEY, SortDialogResult(getSortType(), PageSortOrder.Descending))
            dismiss()
        }
    }

    private fun getSortType() = when (binding?.group?.checkedRadioButtonId) {
        R.id.name -> PageSortType.Name
        R.id.size -> PageSortType.Size
        R.id.date -> PageSortType.Date
        else -> PageSortType.Name
    }

    @Parcelize
    data class SortDialogResult(val sortType: PageSortType, val sortOrder: PageSortOrder): Parcelable
}

