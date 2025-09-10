package com.geovnn.vapetools.ui.screen.saved_screen.state

import android.net.Uri
import com.geovnn.vapetools.data.db.SortType
import com.geovnn.vapetools.data.model.Liquid
import com.geovnn.vapetools.helper.UiText
import com.geovnn.vapetools.ui.common.composable.LiquidParametersState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import com.geovnn.vapetools.ui.screen.saved_screen.composable.ImageBoxState

data class SavedLiquidsState(
    val topAppBar: VapeTopAppBarState = VapeTopAppBarState(),
    val content: Content = Content(),
    val editDialog: Dialog? = null,
    val deleteDialog: DeleteDialog? = null
) {
    data class Content(
        val liquids: LiquidList = LiquidList(),
        val sortType: SortType = SortType.NAME
    ) {
        data class LiquidList(
            val liquids: List<LiquidCardState> = emptyList(),
        ) {
            data class LiquidCardState(
                val id: Int,
                val name: String,
                val imageBox: ImageBoxState?,
                val rating: Int,
                val quantityLabel: UiText,
                val pgRatioLabel: UiText,
                val nicotineStrengthLabel: UiText,
                val aromaLabel : UiText,
                val noteLabel : String,
                val steepingLabel: UiText?,
            )
        }
    }


    data class Dialog(
        val id: Int = 0,
        val name: VapeTextFieldState = VapeTextFieldState(),
        val liquidParameters: LiquidParametersState = LiquidParametersState(),
        val steepingDate: VapeTextFieldState = VapeTextFieldState(),
        val note: VapeTextFieldState = VapeTextFieldState(),
        val rating: Int = 0,
        val imageUri: ImageBoxState = ImageBoxState(),
        val tempImageUri: Uri,
        val titleLabel: UiText
    )

    data class DeleteDialog(
        val title: String,
        val description: UiText,
        val liquid: Liquid,
        val confirmButtonLabel: UiText,
        val cancelButtonLabel: UiText
    )
}

