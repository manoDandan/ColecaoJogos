package com.example.colecaojogos.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.colecaojogos.model.Game

/**
 * Camada de dados: concentra todo o acesso ao SQLite e expõe as
 * quatro operações do CRUD (insert, getAll/getById, update, delete).
 */
class GameRepository(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_PLATFORM TEXT NOT NULL,
                $COL_GENRE TEXT NOT NULL,
                $COL_YEAR INTEGER NOT NULL,
                $COL_RATING REAL NOT NULL,
                $COL_COMPLETED INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    // CREATE
    fun insert(game: Game): Long =
        writableDatabase.insert(TABLE, null, game.toContentValues())

    // READ
    fun getAll(): List<Game> {
        val games = mutableListOf<Game>()
        readableDatabase.query(
            TABLE, null, null, null, null, null, "$COL_TITLE COLLATE NOCASE ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                games.add(cursor.toGame())
            }
        }
        return games
    }

    fun getById(id: Long): Game? =
        readableDatabase.query(
            TABLE, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toGame() else null
        }

    // UPDATE
    fun update(game: Game): Int =
        writableDatabase.update(
            TABLE, game.toContentValues(), "$COL_ID = ?", arrayOf(game.id.toString())
        )

    // DELETE
    fun delete(id: Long): Int =
        writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))

    private fun Game.toContentValues() = ContentValues().apply {
        put(COL_TITLE, title)
        put(COL_PLATFORM, platform)
        put(COL_GENRE, genre)
        put(COL_YEAR, year)
        put(COL_RATING, rating)
        put(COL_COMPLETED, if (completed) 1 else 0)
    }

    private fun Cursor.toGame() = Game(
        id = getLong(getColumnIndexOrThrow(COL_ID)),
        title = getString(getColumnIndexOrThrow(COL_TITLE)),
        platform = getString(getColumnIndexOrThrow(COL_PLATFORM)),
        genre = getString(getColumnIndexOrThrow(COL_GENRE)),
        year = getInt(getColumnIndexOrThrow(COL_YEAR)),
        rating = getFloat(getColumnIndexOrThrow(COL_RATING)),
        completed = getInt(getColumnIndexOrThrow(COL_COMPLETED)) == 1
    )

    private companion object {
        const val DB_NAME = "colecao_jogos.db"
        const val DB_VERSION = 1
        const val TABLE = "games"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_PLATFORM = "platform"
        const val COL_GENRE = "genre"
        const val COL_YEAR = "year"
        const val COL_RATING = "rating"
        const val COL_COMPLETED = "completed"
    }
}
