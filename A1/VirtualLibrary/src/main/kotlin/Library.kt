package org.example

class Library (
    var name: String
) {

    private val books = mutableListOf<Book>()

    fun addBook(book: Book) {
        books.add(book)
        totalBooksCreated++
    }

    fun showBooks() {
        if (books.isEmpty()) {
            println("Não existem livros registados!")
            return
        }

        for(book in books) {
            println(book)
        }
    }

    fun borrowBook(title: String) {
        var found = false

        for (book in books) {

            if (book.title.equals(title, ignoreCase = true)) {
                found = true

                if(book is PhysicalBook) {
                    if(book.availableCopies > 0) {
                        book.availableCopies--
                        println("Livro '${book.title}' emprestado com sucesso. Cópias restantes: ${book.availableCopies}")

                        if(book.availableCopies == 0){
                            println("Atenção: O livro '${book.title}' está agora fora de stock.")
                        }
                    }
                    else {
                        println("Não há cópias disponíveis do livro ${book.title}.")
                    }
                }
                else if (book is DigitalBook) {
                    println("\nO livro ${book.title} é digital. Emprestado com sucesso.")
                }
                return
            }
        }
        if(!found) {
            println("Nenhum livro com esse título foi encontrado.")
        }
    }

    fun returnBook(title: String) {
        for (book in books) {
            if(book.title.equals(title, ignoreCase = true)) {
                if (book is PhysicalBook) {
                    book.availableCopies++
                    println("Livro ${book.title} devolvido com sucesso.")
                } else {
                    println("O livro ${book.title} é digital.")
                }

                return
            }
        }
        println("Livro não encontrado.")
    }

    fun searchByAuthor(author: String) {
        var found = false

        println("Livros do autor '${author}:'")

        for (book in books) {
            if(book.author.equals(author, ignoreCase = true)){
                if (book is PhysicalBook) {
                    println("- ${book.title} (${book.getPublicationCategory()}, ${book.availableCopies} disponíveis)")
                }
                else {
                    println("- ${book.title} (${book.getPublicationCategory()}, digital)")
                }
            }
        }

        if (!found) {
            println("Nenhum livro de $author encontrado.")
        }
    }

    companion object {
        private var totalBooksCreated = 0

        fun getTotalBooksCreated(): Int {
            return totalBooksCreated
        }
    }
}