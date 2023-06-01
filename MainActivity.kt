package com.example.myapplication
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainParent : AppCompatActivity() {

    private lateinit var home: RelativeLayout
    private lateinit var menu: RelativeLayout
    private lateinit var personal: RelativeLayout
    private lateinit var view: FrameLayout
    var username: String = ""
    private lateinit var bottomnav: LinearLayout
    lateinit var loadingLayout: LinearLayout
    lateinit var loading1: View
    lateinit var loading2: View
    lateinit var loading3: View
    lateinit var loading4: View
    lateinit var animator1: ObjectAnimator
    lateinit var animator2: ObjectAnimator
    lateinit var animator3: ObjectAnimator
    lateinit var animator4: ObjectAnimator
    lateinit var home_variables: List<String>
    lateinit var menu_variables: List<String>
    lateinit var personal_variables: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        // 인터넷 연결 시 필요

        home = findViewById(R.id.home)
        menu = findViewById(R.id.menu)
        personal = findViewById(R.id.personal)
        view = findViewById(R.id.view)
        bottomnav = findViewById(R.id.bottomNav)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)
        
        // 로딩 동작 설정 ------------------------------------------------

        animator1 = ObjectAnimator.ofFloat(loading1, "translationY", 50f)
        animator1.interpolator = CycleInterpolator(1f)
        animator1.duration = 2000

        animator2 = ObjectAnimator.ofFloat(loading2, "translationY", 50f)
        animator2.interpolator = CycleInterpolator(1f)
        animator2.duration = 2000
        animator2.startDelay = 200

        animator3 = ObjectAnimator.ofFloat(loading3, "translationY", 50f)
        animator3.interpolator = CycleInterpolator(1f)
        animator3.duration = 2000
        animator3.startDelay = 400

        animator4 = ObjectAnimator.ofFloat(loading4, "translationY", 50f)
        animator4.interpolator = CycleInterpolator(1f)
        animator4.duration = 2000
        animator4.startDelay = 600

        animator1.repeatCount = Animation.INFINITE
        animator2.repeatCount = Animation.INFINITE
        animator3.repeatCount = Animation.INFINITE
        animator4.repeatCount = Animation.INFINITE

        animator1.repeatMode = ValueAnimator.RESTART
        animator2.repeatMode = ValueAnimator.RESTART
        animator3.repeatMode = ValueAnimator.RESTART
        animator4.repeatMode = ValueAnimator.RESTART
      
       // ----------------------------------------------------------------

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        username = sharedPreference.getString("username", "").toString()
        // 세션 불러옴

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
          // 앱 실행 이벤트가 아닌 재로딩 이벤트인 경우 데이터 불러오는 중 로딩 화면 표시
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            getUserInfo(username)
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } else {
          // 앱 실행 이벤트인 경우 데이터 불러오는 중 splash screen 표시
            bottomnav.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, landing())
                .commit()
            getUserInfo(username)
            bottomnav.visibility = View.VISIBLE
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.view, home())
                .commit()

        // 하단 메뉴를 클릭하여 fragment view를 변경 -------------
        home.setOnClickListener {
            if (username == "") {
                startActivity(Intent(this, login::class.java))
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view, home())
                    .commit()
            }
        }
        menu.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, menu())
                .commit()
        }
        personal.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, personal())
                .commit()
        }
       // ------------------------------------------------------
    }

    private fun getUserInfo(s: String) {
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(s, "UTF-8")
        val mURL = URL(URL_STRING)
        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                // URL로 수신한 JSON을 화면별 리스트에 저장하고 향후 뷰에서는 바로 불러옴
                val jsonObj = JSONObject(response.toString())
                if (jsonObj.getString("success") == "true") {

                    if (jsonObj.has("home")) {
                        if (jsonObj.getString("home").contains(",")) {
                            home_variables = jsonObj.getString("home").split(",").toList()
                        }
                    }

                    if (jsonObj.has("menu")) {
                        if (jsonObj.getString("home").contains(",")) {
                            menu_variables = jsonObj.getString("home").split(",").toList()
                        }
                    }

                    if (jsonObj.has("personal")) {
                        if (jsonObj.getString("home").contains(",")) {
                            personal_variables = jsonObj.getString("home").split(",").toList()
                        }
                    }
                // ---------------------------------------------------------------
                } else {
                    startActivity(Intent(applicationContext, ConnectionError::class.java))
                }
            }
        }
    }
}
