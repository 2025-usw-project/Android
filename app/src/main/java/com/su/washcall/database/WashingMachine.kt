// 경로: app/src/main/java/su/database/WashingMachine.kt
package su.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "washing_machine_table",
    foreignKeys = [
        ForeignKey(
            entity = LaundryRoom::class,
            parentColumns = ["id"],   // LaundryRoom.id
            childColumns = ["roomId"], // WashingMachine.roomId
            onDelete = ForeignKey.CASCADE // 세탁실 삭제 시 소속 세탁기들도 삭제
        )
    ]
)
data class WashingMachine(
    @PrimaryKey(autoGenerate = true)
    val machineId: Int = 0, // 세탁기 고유 번호

    val roomId: String,     // 소속 세탁실 ID (FK)
    val name: String,       // 세탁기 이름 (예: "1번 세탁기")
    val status: String,     // 상태 (예: "대기중", "세탁중", "고장")
    val remainingTime: Int  // 남은 시간 (분 단위)
)
