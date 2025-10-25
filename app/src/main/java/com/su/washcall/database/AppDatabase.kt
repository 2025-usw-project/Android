// 경로: app/src/main/java/su/database/AppDatabase.kt
package su.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LaundryRoom::class, User::class, WashingMachine::class], // ✅ 추가됨
    version = 4, // 🔼 버전 올리기 (변경사항 반영)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun laundryRoomDao(): LaundryRoomDao
    abstract fun userDao(): UserDao
    abstract fun washingMachineDao(): WashingMachineDao // ✅ 추가됨

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "laundry_app_database"
                )
                    .fallbackToDestructiveMigration() // DB 변경 시 자동 재생성
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
