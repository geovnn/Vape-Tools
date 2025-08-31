package com.geovnn.vapetools.ui.screen.saved_screen.state

import android.net.Uri
import com.geovnn.vapetools.data.db.LiquidDto
import com.geovnn.vapetools.data.db.SortType
import com.geovnn.vapetools.data.model.Liquid
import com.geovnn.vapetools.ui.common.composable.LiquidParametersState
import com.geovnn.vapetools.ui.common.composable.VapeTextFieldState
import com.geovnn.vapetools.ui.common.composable.VapeTopAppBarState
import java.time.LocalDate

data class SavedLiquidsState(
    val topAppBar: VapeTopAppBarState = VapeTopAppBarState(),
    val content: Content = Content(),
    val editDialog: Dialog? = null,
    val deleteDialog: DeleteDialog? = null
) {
    data class Content(
        val liquids: List<Liquid> = emptyList(),
        val sortType: SortType = SortType.NAME
    )

    data class Dialog(
        val id: Int = 0,
        val name: VapeTextFieldState = VapeTextFieldState(),
        val liquidParameters: LiquidParametersState = LiquidParametersState(),
        val steepingDate: VapeTextFieldState = VapeTextFieldState(),
        val note: VapeTextFieldState = VapeTextFieldState(),
        val rating: Int = 0,
        val imageUri: Uri? = null,
    )

    data class DeleteDialog(
        val title: String,
        val description: String,
        val liquid: Liquid
    )
}

