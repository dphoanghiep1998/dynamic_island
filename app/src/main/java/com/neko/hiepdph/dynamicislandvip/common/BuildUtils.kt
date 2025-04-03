package com.neko.hiepdph.dynamicislandvip.common

import android.os.Build

fun buildMinVersionR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
fun buildMinVersion30(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun buildMinVersionQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun buildMinVersion29(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun buildMinVersionSV2(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
fun buildMinVersion32(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2

fun buildMinVersionP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
fun buildMinVersion28(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

fun buildMinVersionS(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun buildMinVersion31(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S


fun buildMinVersionN(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun buildMinVersion24(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N


fun buildMinVersionT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
fun buildMinVersion33(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun buildMaxVersionN(): Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N
fun buildMaxVersion24(): Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N

fun buildMinVersionM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun buildMinVersion23(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun buildMinVersionO(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun buildMinVersion26(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

//fun buildMinVersionT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun buildMinVersionJellyBeanMr1(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

fun buildMinVersionMR1(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

