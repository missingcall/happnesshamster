package com.kissspace.common.config

import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.module_common.BuildConfig

/**
 * @Author gaohangbo
 * @Date 2023/2/10 10:59.
 * @Describe
 */
object ConstantsKey {
    //即构appId
   // const val ZEGO_APP_ID_RELEASE =

    //正式的key
    const val NETEASE_APP_KEY_RELEASE = BuildConfig.NETEASE_APP_KEY

    //网易易盾
    const val NETEASE_SHILED =  BuildConfig.YiDun_APP_BusinessId //"115d6bd4aaf14ca69b6e779e1e31cff6"

    //网易易盾智能风控(产品编号)
    const val NETEASE_RISK_MANAGEMENT = BuildConfig.YiDun_Control_Id//"YD00672461427411"
    const val NETEASE_RISK_MANAGEMENT_BUSINESSID = BuildConfig.YiDun_Control_Key//"9d147c6e988ec010ac507bf1e16ebbc7"

    //友盟 debugappkey
    const val UMENGKEY_DEBUG_KEY = "6421559dd64e686139551ab6"

    //友盟密钥
    const val UMENGKEY_AUTH_SDK_INFO_DEBUG =
        "WkmUY+AQs6eIsXI16hQe/Lb5geNRgwy8/IRIbVxfKprAVAP99cAk4YLJIgo4GA1bNq+cO0LnpPkVatMmiGLnyqG+6PFYRj5+JKLmsg1zzvRJtHwMZRU6idAciv/h9IydRYBp6NDQu4GdK94dfZpWVxFmsWQeZNpvT8nU8eRkML9o4vwcbOuPiyzkHv9zbsW8vzSHgQd6pZ+Qjmd6W3pq4r29yiqt04yr5RZe2eLX0a8QVqoZIapO88vtIESLQEwwW251dc3KyRGi53PRAL8t0kAoRYuOKW1Q3DRFq9ySHtjvlnAbk3U8Zax7PkYmxcT4"

    //友盟预发布key
    const val UMENGKEY_AUTH_SDK_INFO_PRE =
        "5voFTsqr+kRvveuhC2wkLVuXJXrC6D4yq6XLyRgLrBnGgnWxG3PuRC6wSeNzZU5fGkkekQ6btaZWdnOjJvqUoVetUmfT/9m8Xw/lRSKFJmE8t9lVdqyFwoiIc6C8edBRDGGyVfM/comT3efl+SKycJbx+eFudCrsAJnhreOVbetoWmHuXy2rugc6KXK6a8WY7+lfSUNUezz0d5CLH/WkTvPa/TO4QP7jXn/Lq2QSgMPE9m1ja9BcgrYpNoC90NqlvrSgnj57MOMWSxNrHShh+KXZO9MV54Tk+YbjFAc73S7K8f9ReMrWhNTPqexllBFy"

    const val UMENGKEY_PRE_KEY = "64158b6eba6a5259c420a98f"


    const val UMENGKEY_RELEASE_KEY = "6421559dd64e686139551ab6"

    const val UMENGKEY_AUTH_SDK_INFO_RELEASE =
        "WkmUY+AQs6eIsXI16hQe/Lb5geNRgwy8/IRIbVxfKprAVAP99cAk4YLJIgo4GA1bNq+cO0LnpPkVatMmiGLnyqG+6PFYRj5+JKLmsg1zzvRJtHwMZRU6idAciv/h9IydRYBp6NDQu4GdK94dfZpWVxFmsWQeZNpvT8nU8eRkML9o4vwcbOuPiyzkHv9zbsW8vzSHgQd6pZ+Qjmd6W3pq4r29yiqt04yr5RZe2eLX0a8QVqoZIapO88vtIESLQEwwW251dc3KyRGi53PRAL8t0kAoRYuOKW1Q3DRFq9ySHtjvlnAbk3U8Zax7PkYmxcT4"


    var WECHAT_APPID: String = ""
//    val WECHAT_APPID: String?
//        get() = MMKVProvider.wxConfig?.wechat_app_id

    val WECHAT_APPSECRET:String?
        get() = MMKVProvider.wxConfig?.wechat_app_secret
    //数美
    const val SM_APPID_DEBUG = "test"
    const val SM_APPID_RELEASE = "hamster"
    const val SM_ORGANIZATION = "NokK7G85CP9UwJjH3dov"
    const val SM_PUBLICKEY =
        "MIIDLzCCAhegAwIBAgIBMDANBgkqhkiG9w0BAQUFADAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wHhcNMjAwNzMwMDgwMDQ2WhcNNDAwNzI1MDgwMDQ2WjAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDSHLhGaUZRQAtlAt4ZZTUjiO2qr8LD7pewYRMTkKxqrIJWD9nh+wM5tZ/jShvX0LpfJq4zaKe+sNca9nQOhTM3wOUpVI8sgQRFXo06fDTHqYNDJ9Oh0N7YyaIvSj0+NOGxp0u3pTUl0PE8tqiCyFRgwSAbft8fcjDo3QJt9NzYlHPKjMIwHqi4kjctZeVlDWXW2YxoQuGXpXkH9SP6/Up/5e9u9rjgFMaV99IY7x98VsaYRE/XbqOSkwuxRpzWUNUBkLlrXhpGQ6ZX4tMx0aT+n9kEiebbJcEihK3zTqSO0maZQoxO8tljorG+H3q1d/hpUv/COENvcNIAVg6T17h5AgMBAAGjUDBOMB0GA1UdDgQWBBSiMYRWv2cA1nqXroZgQ5/+Z7IIfzAfBgNVHSMEGDAWgBSiMYRWv2cA1nqXroZgQ5/+Z7IIfzAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQB4LdUJYCeatIuiKcd1Ro9j95yYqZQsqruB4K1qMJBGj8dDsWAo00SUTllxlITUU63bc8t0ekqLYP2RZCHsNwy99hfBkD03R80+XBkhetLvCilopUf/NzhFSykXq2nRR4zUrzzJncsSOzVWIewmobus3u2Us3/5CkOVHyjbts5hBNEcn8Q3YO4Iu9evg5/CeNd7j7AJJXA8wvAnLKIjnvuQdb5SAgr82tm/kFm5ACZfZhBMbkqjm/yVPTCOMZhDKupYRa4raih4CHdUSvYSIups9Z3ulTM7VuPd27m8YNTggwJE3HJRHsJYNHg771fpnHqY8Lsfy0LKJo94ndawslQO"

    //杉德
    const val SAND_PAY_CHANNEL_CODE = "test"

    //游戏id-飞行棋
    const val GAME_ID_FLIGHT_CHESS: Long = 1468180338417074177

    //游戏id-台球
    const val GAME_ID_BILLIARD: Long = 1582551621189419010

    //游戏id-你画我猜
    const val GAME_ID_DRAW_AND_GUESS: Long = 1461228410184400899

    const val GAME_APP_ID = "1673968199178969089"

    const val GAME_APP_KEY = "i1nAEpPRYMc5nFgvvSQSP8H4NR09qVfj"

    const val GAME_APP_SECRET = "QPH4nQy7FYm5nfkXF1Y46w5FZnQOhGBy"
}