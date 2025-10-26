package com.su.washcall.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * LaundryRoom 테이블에 접근하기 위한 함수들을 정의하는 인터페이스입니다.
 * Room 라이브러리가 이 인터페이스의 구현체를 자동으로 생성해줍니다.
 */
@Dao
interface LaundryRoomDao {
    /**
     * 데이터베이스에 저장된 모든 세탁실 목록을 Flow 형태로 반환합니다.
     * 데이터가 변경되면 Flow가 새로운 리스트를 자동으로 발행하여 UI가 갱신될 수 있습니다.
     */
    @Query("SELECT * FROM laundry_rooms")
    fun getAllLaundryRooms(): Flow<List<LaundryRoom>>

    /**
     * 새로운 세탁실 목록을 데이터베이스에 삽입합니다.
     * 만약 동일한 Primary Key(roomId)를 가진 데이터가 이미 존재하면, 덮어씁니다 (REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laundryRooms: List<LaundryRoom>)

    /**
     * 테이블에 있는 모든 데이터를 삭제합니다.
     * 새로운 데이터로 갱신하기 전에 기존 데이터를 지울 때 사용됩니다.
     */
    @Query("DELETE FROM laundry_rooms")
    suspend fun deleteAll()
}
