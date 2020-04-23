package com.sd.laborator.services

import com.sd.laborator.interfaces.CachingDAO
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException
import javax.annotation.PostConstruct


class CacheResultRowMapper : RowMapper<String> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): String {
        return rs.getString("result")
    }
}

@Service
class CachingDAOService : CachingDAO {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @PostConstruct
    private fun createCacheTable() {
        jdbcTemplate.execute(
            """CREATE TABLE IF NOT EXISTS cache(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp INTEGER,
                        query VARCHAR(150),
                        result VARCHAR(2000),
                        CONSTRAINT cache_uk UNIQUE(timestamp, query, result)
                    );""")
    }

    override fun exists(query: String): String? {
        val timeline = System.currentTimeMillis() - 3600000
        val result: MutableList<String> = jdbcTemplate.query("SELECT result FROM cache WHERE query = '$query' " +
                "AND timestamp >= $timeline", CacheResultRowMapper())
        return try {
            result.first()
        } catch(e: Exception) {
            ""
        }
    }

    override fun addToCache(query: String, result: String) {
        jdbcTemplate.update("INSERT INTO cache(timestamp, query, result) VALUES (?, ?, ?, )",
            System.currentTimeMillis(), query, result)
    }
}