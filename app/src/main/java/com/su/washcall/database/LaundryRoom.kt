package com.su.washcall.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 'laundry_rooms'라는 이름의 데이터베이스 테이블을 정의하는 Entity 클래스입니다.
 * 이 클래스는 서버에서 받아온 세탁실 정보를 나타내며, 데이터베이스의 테이블 스키마가 됩니다.
 */
@Entity(tableName = "laundry_rooms") // 1. 테이블 이름을 명시적으로 지정
data class LaundryRoom(
    /**
     * 세탁실의 고유 ID입니다. 이 값을 기본 키(Primary Key)로 사용합니다.
     * '@ColumnInfo'를 사용하여 데이터베이스 테이블의 실제 컬럼 이름을 'room_id'로 지정합니다.
     * 이렇게 하면 다른 테이블(예: WashingMachine)에서 외래 키(Foreign Key)로 참조할 때
     * 명확하고 일관된 이름을 사용할 수 있습니다.
     */
    @PrimaryKey
    @ColumnInfo(name = "room_id") // 2. 컬럼 이름을 명시적으로 'room_id'로 지정
    val roomId: Int,

    /**
     * 세탁실의 이름입니다. (예: "제1기숙사 세탁실")
     * 컬럼 이름을 'room_name'으로 지정합니다.
     */
    @ColumnInfo(name = "room_name") // 3. 컬럼 이름을 명시적으로 'room_name'으로 지정
    val roomName: String
)
