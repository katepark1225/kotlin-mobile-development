package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.klipwallet.app2app.api.Klip
import com.klipwallet.app2app.api.KlipCallback
import com.klipwallet.app2app.api.request.AuthRequest
import com.klipwallet.app2app.api.request.TokenTxRequest
import com.klipwallet.app2app.api.request.model.BAppInfo
import com.klipwallet.app2app.api.response.KlipErrorResponse
import com.klipwallet.app2app.api.response.KlipResponse
import com.klipwallet.app2app.exception.KlipRequestException
import org.json.JSONObject

class main : Fragment() {

    lateinit var request_key: String
    // 클립 통신에 사용될 토큰

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        return view
    }

    // 토큰 전송하기
    private fun deposit() {
        lateinit var request_key: String
        val tokenSCA = "0x02cbe46fb8a1f579254a9b485788f2d86cad51aa"
        // BORA 토큰
        // 클립에서 유통되는 가상화폐는 모두 SCA라는 번호가 있음
        val bappInfo = BAppInfo("App Name")
        // 이용자의 화면에 표시될 앱명
        val klip = Klip.getInstance(requireContext())
        // Klip 앱을 이니셜라이징 함
        val req = TokenTxRequest.Builder()
            .contract(tokenSCA)
            .to(Wallet_Address) // 돈을 보낼 주소
            .amount(Sending_Amount) // 보낼 금액
            .build()
            
        // prepare() API를 실행하면 콜백으로 아래 로직이 이어짐
        val callback: KlipCallback<KlipResponse> =
            object : KlipCallback<KlipResponse> {
                override fun onSuccess(res: KlipResponse) {
                    val jsonObj = JSONObject(res.toString())
                    request_key = jsonObj.getString("request_key")
                    try {
                        klip.request(request_key)
                        // JSON으로 받은 토큰으로 최종 요청함
                    } catch (e: KlipRequestException) {
                        e.printStackTrace()
                    }
                }
                override fun onFail(res: KlipErrorResponse) {}
            }
        try {
            klip.prepare(req, bappInfo, callback)
        } catch (e: KlipRequestException) {
            e.printStackTrace();
        }
        
        // 사용자가 클립 앱에서 동의를 하고 본 앱으로 돌아와 버튼을 누르면 결과를 요청하는 getResult() API가 실행되고 아래 콜백으로 이어짐
        val callback1: KlipCallback<KlipResponse> =
            object : KlipCallback<KlipResponse> {
                override fun onSuccess(res: KlipResponse) {
                    val result = res.toString()
                    if (result.contains("completed")) {
                        // 성공적
                    } else {
                        // 앱을 강제로 종료시킴
                    }
                }

                override fun onFail(res: KlipErrorResponse) {}
            }
        try {
            klip.getResult(request_key, callback1)
        } catch (e: KlipRequestException) {
            e.printStackTrace()
        }
    }
}
