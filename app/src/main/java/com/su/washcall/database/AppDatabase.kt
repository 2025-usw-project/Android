// 경로: app/src/main/java/com/su/washcall/database/AppDatabase.kt
package com.su.washcall.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LaundryRoom::class, WashingMachine::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun laundryRoomDao(): LaundryRoomDao

    companion object {
        // @Volatile: 이 변수에 대한 모든 쓰기 작업이 즉시 다른 스레드에 보이도록 보장
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // INSTANCE가 null이면 새로 생성하고, null이 아니면 기존 인스턴스를 반환 (싱글톤 패턴)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "washcall_database"
                )
                    .fallbackToDestructiveMigration() // 개발 중에는 스키마 변경 시 DB를 삭제하고 다시 생성
                    .build()
                INSTANCE = instance
                // 반환
                instance
            }
        }
    }
}
