package com.kissspace.common.util.format


/**
 * @author Loja
 * @created on 11/8/18.
 */
object DateFormat {
    val EEE_D_MMM_YYYY_HH_MM_SS_Z = SafeSimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z")
    val YYYY_MM_DD_EEE_HH_MM = SafeSimpleDateFormat("yyyy/MM/dd (EEE) HH:mm")
    val YYYY_MM_DD_EEE_HH_MM_2 = SafeSimpleDateFormat("yyyy-MM-dd (EEE) HH:mm")
    val YYYY_MM_DD_EEE_HH_MM_3 = SafeSimpleDateFormat("yyyy年MM月dd日 (EEE) HH:mm")
    val MM_DD_EEE_HH_MM_CN = SafeSimpleDateFormat("MM月dd日 (EEE) HH:mm")
    val YYYY_MM_DD_HH_MM_ALL_DIGITAL = SafeSimpleDateFormat("yyyyMMddHHmm")
    val YYYY_MM_DD_HH_MM_SS = SafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val YYYY_MM_DD_HH_MM_SS_SSS = SafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    val YYYY_MM_DD_EEE_SLASH  = SafeSimpleDateFormat("yyyy/MM/dd (EEE)")
    val YYYY_MM_DD_EEE = SafeSimpleDateFormat("yyyy-MM-dd (EEE)")
    val YYYY_MM_DD_EEE_CN = SafeSimpleDateFormat("yyyy年MM月dd日 EEE")
    val YYYY_MM_DD_E = SafeSimpleDateFormat("yyyy-MM-dd E")
    val MM_DD_EEE = SafeSimpleDateFormat("MM月dd日 EEE")
    val MM_DD_EEE2 = SafeSimpleDateFormat("MM-dd (EEE)")
    val YYYY_MM_DD_HH_MM_SLASH = SafeSimpleDateFormat("yyyy/MM/dd HH:mm")
    val YYYY_MM_DD_HH_MM_CN = SafeSimpleDateFormat("yyyy年MM月dd日 HH:mm")
    val YYYY_MM_DD_HH_MM = SafeSimpleDateFormat("yyyy-MM-dd HH:mm")
    val YYYY_MM_DD_HH = SafeSimpleDateFormat("yyyy/MM/dd HH时")
    val YYYY_MM_DD_SLASH = SafeSimpleDateFormat("yyyy/MM/dd")
    val YYYY_MM_DD_DOT = SafeSimpleDateFormat("yyyy.MM.dd")
    val YYYY_MM_DD_CN = SafeSimpleDateFormat("yyyy年MM月dd日")
    val YYYY_MM_DD = SafeSimpleDateFormat("yyyy-MM-dd")
    val YY_MM_DD = SafeSimpleDateFormat("yy-MM-dd")
    val YYYY_MM_CN = SafeSimpleDateFormat("yyyy年MM月")
    val YYYY_MM_DOT = SafeSimpleDateFormat("yyyy.MM")
    val YYYY_MM_HYPHEN = SafeSimpleDateFormat("yyyy-MM")
    val YYYY_MM = SafeSimpleDateFormat("yyyy年MM月")
    val YYYY_CN = SafeSimpleDateFormat("yyyy年")

    val MM_DD_HH_MM_DOT = SafeSimpleDateFormat("MM.dd HH:mm")
    val MM_DD_HH_MM_CN = SafeSimpleDateFormat("MM月dd日 HH:mm")
    val MM_DD_HH_MM = SafeSimpleDateFormat("MM-dd HH:mm")
    val MM_DD_HH_MM_SS = SafeSimpleDateFormat("MM-dd HH:mm:ss")
    val MM_DD_SLASH = SafeSimpleDateFormat("MM/dd")
    val MM_DD_DOT = SafeSimpleDateFormat("MM.dd")
    val MM_DD_CN = SafeSimpleDateFormat("MM月dd日")
    val MM_DD = SafeSimpleDateFormat("MM-dd")
    val HH_MM_SS_SIMPLE = SafeSimpleDateFormat("HHmmss")
    val HH_MM_SS = SafeSimpleDateFormat("HH:mm:ss")
    val HH_MM = SafeSimpleDateFormat("HH:mm")
    val MM_SS = SafeSimpleDateFormat("mm:ss")
    val MM_CN = SafeSimpleDateFormat("MM月")
    val M_CN = SafeSimpleDateFormat("M月")
    val M = SafeSimpleDateFormat("M")
    val DD_CN = SafeSimpleDateFormat("dd日")
    val EEE = SafeSimpleDateFormat("EEE")
    val E = SafeSimpleDateFormat("E")
    val YYYY_MM_NOT_ALL = SafeSimpleDateFormat("yyyyMM")
    val MM_SS_CN = SafeSimpleDateFormat("mm分ss秒")
    val YYYY = SafeSimpleDateFormat("yyyy")
    val HH_MM2 = SafeSimpleDateFormat("HH小时mm分")
}