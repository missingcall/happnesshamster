package com.kissspace.mine.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SizeUtils
import com.didi.drouter.annotation.Router
import com.king.zxing.util.CodeUtils
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.ShareUtil
import com.kissspace.common.util.checkAlbumPermission
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityInvitationPosterBinding
import com.kissspace.util.context
import com.kissspace.util.loadImage
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.umeng.socialize.utils.CommonUtil

@Router(path = RouterPath.PATH_INVITATION_POSTER)
class InvitationPosterActivity : BaseActivity(R.layout.mine_activity_invitation_poster) {

    private val mBinding by dataBinding<MineActivityInvitationPosterBinding>()
    private val mViewModel by viewModels<MineViewModel>()

    private var mUrl: String? = null
    private var mQrCode: Bitmap? = null

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.lifecycleOwner = this

        //生成二维码
        generateQRCode()

        mBinding.ivIcon.loadImage(MMKVProvider.userInfo?.profilePath, com.kissspace.module_util.R.drawable.common_ic_default, 8f, 8f, 8f, 8f)
        mBinding.tvName.text = MMKVProvider.userInfo?.nickname
        mBinding.tvUid.text = "UID : " + MMKVProvider.displayId

    }

    private fun generateQRCode() {
        mViewModel.getQrUrl {

            /*val bitmapArray = EncodeUtils.base64Decode(it)
            mQrCode = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
            mBinding.ivQrCode.setImageBitmap(mQrCode)*/

            mUrl = it

            mQrCode = CodeUtils.createQRCode(
                mUrl,
                SizeUtils.dp2px(70f),
                BitmapFactory.decodeResource(context.resources, com.kissspace.module_util.R.drawable.common_ic_default)
            )
            mBinding.ivQrCode.setImageBitmap(mQrCode)

            mBinding.btnWechat.safeClick {
                //分享到对话
                ShareUtil.sendToWeiXin("title", mUrl, "des", null, SendMessageToWX.Req.WXSceneSession, this)
            }

            mBinding.btnSave.safeClick {
                checkAlbumPermission {
                    ImageUtils.save2Album(mQrCode, Bitmap.CompressFormat.PNG)
                    customToast("图片已保存到相册")
                }

            }

            mBinding.btnWechatFriends.safeClick {
                //分享到朋友圈
                ShareUtil.sendToWeiXin("title", mUrl, "des", null, SendMessageToWX.Req.WXSceneTimeline, this)
            }
        }

    }


}