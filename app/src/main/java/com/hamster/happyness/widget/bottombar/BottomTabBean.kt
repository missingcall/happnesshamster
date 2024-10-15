 package com.hamster.happyness.widget.bottombar

data class BottomTabBean(
    var text: String,
    val position: Int,
    var normalTextColor:Int,
    var selectedTextColor:Int,
    var normalDrawable: Int,
    var selectDrawable: Int,
    var isSelected: Boolean
)