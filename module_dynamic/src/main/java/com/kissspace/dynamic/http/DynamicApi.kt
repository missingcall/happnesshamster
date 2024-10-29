package com.kissspace.dynamic.http

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/29 21:13
 * @Description:
 *
 */
object DynamicApi {

    //发布动态
    const val API_INSERT_DYNAMICS = "/hamster-user/dynamics/insertDynamics"

    //获取动态列表
    const val API_GET_DYNAMICS_LIST = "/hamster-user/dynamics/pageQueryDynamicsList"


    const val API_GET_DYNAMICS_LIST_RECOMMEND = "/hamster-user/dynamics/pageQueryDynamicsList"

    const val API_GET_DYNAMICS_WITH_USER = "/hamster-user/dynamics/queryThisUserDynamicsList"


    const val API_GET_DYNAMICS_LIST_FOLLOW = "/hamster-user/dynamics/queryDynamicsFollowWithInterestList"

    const val API_LIKE_DYNAMICS = "/hamster-user/dynamics/likeOrCancelLiking"

    const val API_DYNAMIC_FOLLOW = "/hamster-user/userAttention/attentionUser"

    const val API_DYNAMIC_UN_FOLLOW = "/hamster-user/dynamics/likeOrCancelLiking"

    const val API_GET_COMMENT_DETAIL = "/hamster-user/dynamics/obtainUserInformationForComments"

    const val API_GET_DYNAMIC_LILES = "/hamster-user/dynamics/obtainUserInformationForLikes"

    const val API_ADD_COMMENT = "/hamster-user/dynamics/insertCommentsDynamic"

    const val API_DELETE_DYNAMIC = "/hamster-user/dynamics/deleteDynamics"

    const val API_DYNAMIC_DETAIL = "/hamster-user/dynamics/obtainDynamicDetailsBasedOnDynamicId"

}