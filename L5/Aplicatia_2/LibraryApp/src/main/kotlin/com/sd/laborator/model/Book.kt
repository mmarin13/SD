package com.sd.laborator.model

class Book(private var data: Content) {

    var name: String?
        get() {
            return data.name
        }
        set(value) {
            data.name = value
        }

    var author: String?
        get() {
            return data.author
        }
        set(value) {
            data.author = value
        }

    var publisher: String?
        get() {
            return data.publisher
        }
        set(value) {
            data.publisher = value
        }

    var content: String?
        get() {
            return data.text
        }
        set(value) {
            data.text = value
        }

    fun hasAuthor(author: String): Boolean {
        val result = data.author?.contains(author, true) ?: return false
        return result
    }

    fun hasTitle(title: String): Boolean {
        val result = data.name?.contains(title, true) ?: return false
        return result
    }

    fun publishedBy(publisher: String): Boolean {
        val result = data.publisher?.contains(publisher, true) ?: return false
        return result
    }

}