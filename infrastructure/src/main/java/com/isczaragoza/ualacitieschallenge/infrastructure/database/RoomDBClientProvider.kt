package com.isczaragoza.ualacitieschallenge.infrastructure.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.isczaragoza.ualacitieschallenge.data.database.DBClientProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RoomDBClientProvider @Inject constructor(@ApplicationContext val context: Context) :
    DBClientProvider<UalaCitiesChallengeRoomDB> {
    override fun provideDBClient(): UalaCitiesChallengeRoomDB {

        val triggersSQL = listOf(
            """
    CREATE TRIGGER IF NOT EXISTS cityentity_ai AFTER INSERT ON CityEntity BEGIN
      INSERT INTO CityEntityFts(rowid, name) VALUES (new.id, new.name);
    END;
    """.trimIndent(),
            """
    CREATE TRIGGER IF NOT EXISTS cityentity_ad AFTER DELETE ON CityEntity BEGIN
      DELETE FROM CityEntityFts WHERE rowid = old.id;
    END;
    """.trimIndent(),
            """
    CREATE TRIGGER IF NOT EXISTS cityentity_au AFTER UPDATE ON CityEntity BEGIN
      UPDATE CityEntityFts SET name = new.name WHERE rowid = old.id;
    END;
    """.trimIndent()
        )

        val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                triggersSQL.forEach { sql ->
                    db.execSQL(sql)
                }
            }
        }

        return Room.databaseBuilder(
            context,
            UalaCitiesChallengeRoomDB::class.java,
            UalaCitiesChallengeRoomDB.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            //.addCallback(roomCallback) enfoque si no se puede usar @Fts4(contentEntity = ...)
            .build()
    }
}
