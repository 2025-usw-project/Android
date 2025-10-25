// 경로: app/src/main/java/com/su/washcall/database/WashingMachine.kt
package com.su.washcall.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 개별 세탁기 정보를 저장하는 Room의 Entity (테이블) 클래스
 * LaundryRoom 테이블과 Foreign Key로 연결됩니다.
 */
@Entity(
    tableName = "washing_machine_table",
    foreignKeys = [
        ForeignKey(
            entity = LaundryRoom::class,          // 부모 테이블
            parentColumns = ["room_id"],          // 부모 테이블의 키 (LaundryRoom.roomId)
            childColumns = ["laundry_room_id"],   // 이 테이블의 키 (WashingMachine.laundryRoomId)
            onDelete = ForeignKey.CASCADE         // 세탁실이 삭제되면 소속된 세탁기들도 함께 삭제
        )
    ]
)
data class WashingMachine(
    @PrimaryKey
    @ColumnInfo(name = "machine_id")
    val machineId: Int, // 세탁기 고유 ID (Primary Key)

    @ColumnInfo(name = "laundry_room_id", index = true) // FK는 성능을 위해 index를 설정하는 것이 좋음
    val laundryRoomId: Int, // 소속된 세탁실 ID (Foreign Key)

    @ColumnInfo(name = "machine_name")
    val machineName: String, // 세탁기 이름 (예: "1번 세탁기")

    @ColumnInfo(name = "status")
    val status: String     // 상태 (예: "대기중", "세탁중", "고장")
)
