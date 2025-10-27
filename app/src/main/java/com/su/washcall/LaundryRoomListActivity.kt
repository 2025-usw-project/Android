// 파일 경로: app/src/main/java/com/su/washcall/LaundryRoomListActivity.kt
package com.su.washcall

// 더 이상 SharedPreferences를 사용하지 않으므로 Context import는 필요 없습니다.
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.washcall.adapter.LaundryRoomAdapter
import com.su.washcall.database.LaundryRoom
import com.su.washcall.viewmodel.LaundryViewModel
import com.su.washcall.viewmodel.LaundryViewModelFactory

class LaundryRoomListActivity : AppCompatActivity() {

    private val laundryViewModel: LaundryViewModel by viewModels {
        LaundryViewModelFactory(application)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var laundryAdapter: LaundryRoomAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laundry_room_list)

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.rvLaundryRooms)

        setupRecyclerView()
        observeViewModels()

        // --- ▼▼▼ [최종 수정] 액티비티가 생성될 때 ViewModel의 refreshData()를 직접 호출합니다. ▼▼▼ ---
        laundryViewModel.refreshData()
        // --- ▲▲▲ [최종 수정] ▲▲▲ ---
    }

    private fun setupRecyclerView() {
        laundryAdapter = LaundryRoomAdapter { room ->
            Toast.makeText(this, "${room.roomName} 클릭됨", Toast.LENGTH_SHORT).show()
            // 여기에 나중에 세탁기 목록 액티비티(MachinListActivity)로 이동하는 코드를 추가할 수 있습니다.
        }
        recyclerView.adapter = laundryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModels() {
        // 1. 세탁실 목록 변경을 관찰합니다.
        laundryViewModel.allLaundryRooms.observe(this) { rooms: List<LaundryRoom>? ->
            rooms?.let { laundryAdapter.submitList(it) }
        }

        // 2. ViewModel에서 보내는 간단한 토스트 메시지를 관찰합니다.
        laundryViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // 3. '세탁기 추가' 또는 '새로고침' 작업 상태를 관찰합니다.
        laundryViewModel.operationStatus.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    // --- ▼▼▼ [최종 수정] 더 이상 토큰을 직접 처리할 필요가 없으므로 아래 함수들은 모두 삭제합니다. ▼▼▼ ---
    /*
    private fun refreshServerData() {
        ...
    }

    private fun getToken(): String? {
        ...
    }
    */
    // --- ▲▲▲ [최종 수정] ▲▲▲ ---
}

