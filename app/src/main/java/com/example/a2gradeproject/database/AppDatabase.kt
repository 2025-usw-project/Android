package com.example.a2gradeproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// User 관련 코드를 모두 제거하고 LaundryRoom만 포함합니다.
@Database(
    entities = [LaundryRoom::class, User::class], // User::class 제거
    version = 2, // 버전은 다시 1로 초기화
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // User DAO는 제거하고 LaundryRoomDao만 남깁니다.
    abstract fun laundryRoomDao(): LaundryRoomDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "laundry_app_database"
                ).fallbackToDestructiveMigration().build()
                //버전을 올리면 db 삭제 후 재생성 허용
                INSTANCE = instance
                instance
            }
        }
    }
}