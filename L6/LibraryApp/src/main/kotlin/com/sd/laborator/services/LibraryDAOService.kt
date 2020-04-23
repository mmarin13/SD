package com.sd.laborator.services

import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.model.Book
import com.sd.laborator.model.Content
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.SQLException
import javax.annotation.PostConstruct

class BookRowMapper : RowMapper<Book> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Book {
        return Book(Content(
            rs.getString("author"),
            rs.getString("text"),
            rs.getString("title"),
            rs.getString("publisher")))
    }
}

@Service
class LibraryDAOService: LibraryDAO {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @PostConstruct
    private fun createBookTable() {
        jdbcTemplate.execute(
            """CREATE TABLE IF NOT EXISTS book(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        author VARCHAR(100),
                        title VARCHAR(100),
                        publisher VARCHAR(100),
                        text TEXT,
                        CONSTRAINT book_uk UNIQUE(author, title, publisher, text)
                    );""")
    }

    override fun getBooks(): Set<Book> {
        val result: MutableList<Book> = jdbcTemplate.query("SELECT * FROM book;", BookRowMapper())
        return result.toSet()
    }

    override fun addBook(book: Book) {
        jdbcTemplate.update("INSERT INTO book(author, title, publisher, text) VALUES (?, ?, ?, ?)",
            book.author, book.name, book.publisher, book.content)
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        val result: MutableList<Book> = jdbcTemplate.query("SELECT * FROM book WHERE author = '$author'",
            BookRowMapper())
        return result.toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        val result: MutableList<Book> = jdbcTemplate.query("SELECT * FROM book WHERE title = '$title'",
            BookRowMapper())
        return result.toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        val result: MutableList<Book> = jdbcTemplate.query("SELECT * FROM book WHERE publisher = '$publisher'",
            BookRowMapper())
        return result.toSet()
    }
}