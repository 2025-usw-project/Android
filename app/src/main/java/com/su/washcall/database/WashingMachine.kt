package com.su.washcall.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 개별 세탁기 정보를 저장하는 Room의 Entity(테이블) 클래스입니다.
 * 'LaundryRoom' 테이블과 외래 키(Foreign Key)로 연결하여 관계를 맺습니다.
 */
@Entity(
    tableName = "washing_machines", // 테이블 이름은 복수형을 사용하는 것이 일반적입니다.
    foreignKeys = [
        ForeignKey(
            entity = LaundryRoom::class,         // 부모 테이블: LaundryRoom
            parentColumns = ["room_id"],         // 🔹부모 테이블의 키 컬럼 이름 (LaundryRoom.kt의 PrimaryKey와 일치해야 함)
            childColumns = ["laundry_room_id"],  // 🔹이 테이블(자식)에서 부모를 가리키는 키 컬럼 이름
            onDelete = ForeignKey.CASCADE        // 부모(세탁실)가 삭제되면, 소속된 자식(세탁기)들도 함께 삭제됩니다.
        )
    ]
)
data class WashingMachine(
    @PrimaryKey
    @ColumnInfo(name = "machine_id")
    val machineId: Int, // 세탁기 고유 ID (기본 키)

    // 외래 키(Foreign Key) 컬럼입니다.
    // 컬럼에 인덱스를 생성하면 데이터 조회 성능이 향상됩니다.
    @ColumnInfo(name = "laundry_room_id", index = true)
    val laundryRoomId: Int,

    @ColumnInfo(name = "machine_name")
    val machineName: String, // 세탁기 이름 (예: "1번 세탁기")

    @ColumnInfo(name = "status")
    val status: String,     // 세탁기 현재 상태 (예: "AVAILABLE", "RUNNING", "OUT_OF_ORDER")

    @ColumnInfo(name = "remaining_time")
    val remainingTime: Int  // 남은 시간 (초 단위 등)
)
