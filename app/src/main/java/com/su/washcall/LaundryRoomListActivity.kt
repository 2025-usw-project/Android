package com.su.washcall

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.washcall.adapter.LaundryRoomAdapter // 1. 어댑터 임포트 (아직 안 만들었음)
import com.su.washcall.viewmodel.*

class LaundryRoomListActivity : AppCompatActivity() {

    // 목록 데이터를 가져오기 위한 ViewModel
    private val laundryViewModel: LaundryViewModel by viewModels {
        // LaundryApplication과 Repository가 미리 정의되어 있어야 함
        LaundryViewModelFactory((application as MyApplication).repository)
    }

    // '구독' 기능을 처리하기 위한 ViewModel (이전에 만듦)
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var laundryAdapter: LaundryRoomAdapter // 2. 어댑터 변수 선언
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1단계에서 만든 레이아웃 파일을 화면으로 설정
        setContentView(R.layout.activity_laundry_room_list)

        // 레이아웃의 뷰들과 변수를 연결
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.rvLaundryRooms)

        // 3. 리사이클러뷰와 어댑터 설정 (아래 함수 호출)
        setupRecyclerView()

        // 4. ViewModel의 데이터 변경을 감지하여 UI 업데이트 (아래 함수 호출)
        observeLaundryViewModel()
        observeUserViewModel()

        // 5. 화면이 처음 열릴 때 서버에서 최신 데이터를 가져옴 (아래 함수 호출)
        refreshServerData()
    }

    private fun setupRecyclerView() {
        // 3-1. 어댑터 생성: 각 아이템 클릭 시 userViewModel의 구독 함수를 호출
        laundryAdapter = LaundryRoomAdapter { room ->
            Toast.makeText(this, "'${room.roomName}' 구독 요청...", Toast.LENGTH_SHORT).show()
            userViewModel.subscribeToLaundryRoom(room.roomName)
        }

        // 3-2. 리사이클러뷰에 어댑터와 레이아웃 매니저 설정
        recyclerView.adapter = laundryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeLaundryViewModel() {
        // 4-1. 'laundryViewModel'의 세탁실 목록(allLaundryRooms)에 변화가 생기면,
        //      어댑터에 새로운 목록을 전달하여 화면을 자동으로 업데이트
        laundryViewModel.allLaundryRooms.observe(this) { rooms ->
            rooms?.let {
                laundryAdapter.submitList(it)
            }
        }
    }

    private fun observeUserViewModel() {
        // 4-2. 'userViewModel'의 구독 결과(subscribeResult)에 변화가 생기면,
        //      결과에 따라 Toast 메시지와 ProgressBar를 제어
        userViewModel.subscribeResult.observe(this) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is ApiResult.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "✅ ${result.message}", Toast.LENGTH_LONG).show()
                    // TODO: 구독 성공 시 다음 화면(WashingMachineStatusActivity)으로 이동
                }
                is ApiResult.Failure -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "❌ 구독 실패: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 5-1. 서버에서 데이터를 새로고침하는 함수
    private fun refreshServerData() {
        val token = getToken() // SharedPreferences에서 토큰 가져오기
        if (token != null) {
            progressBar.visibility = View.VISIBLE
            // laundryViewModel을 통해 서버 데이터 새로고침 명령
            laundryViewModel.refreshData("Bearer $token")
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_LONG).show()
            // TODO: 로그인 화면으로 이동하는 로직 추가
        }
    }

    // SharedPreferences에서 저장된 JWT 토큰을 가져오는 함수
    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", null)
    }
}
