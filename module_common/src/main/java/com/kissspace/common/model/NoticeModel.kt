package com.kissspace.common.model

import kotlinx.serialization.Serializable

/**
 * @description:通知
 * @author: yxt
 * @create: 2024-11-23 09:48
 * @param createTime 创建时间
 * @param endTime 结束时间
 * @param hasDelete 是否删除  0:未删除 1：已删除
 * @param 主键id 主键id
 * @param intVersion 去‘.’的版本号
 * @param noticeFrequency 公告频率 001 启动始终弹出 002 仅启动弹出一次
 * @param noticeIcon 公告图
 * @param noticeLink 公告链接
 * @param noticeParam 公告内容
 * @param operator 操作人
 * @param operatorTime 操作时间
 * @param os 渠道 1:android 2:ios 3:web
 * @param versionNo 版本号
 **/
@Serializable
data class NoticeModel(
    val beginTime: String,
    val createTime: String,
    val endTime: String,
    val hasDelete: String,
    val id: Long,
    val intVersion: String,
    val noticeFrequency: String,
    val noticeIcon: String,
    val noticeLink: String,
    val noticeParam: String,
    val `operator`: String,
    val operatorTime: String,
    val os: String,
    val versionNo: String
)