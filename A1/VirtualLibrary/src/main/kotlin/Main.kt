package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val library = Library("Central Library")

    val digitalBook = DigitalBook (
        "Kotlin in Action",
        "Dmitry Jemerov",
        2017,
        4.5,
        "PDF"
    )

    val physicalBook = PhysicalBook (
        "Clean code",
        "Robert C. Martin",
        2008,
        3,
        650.0,
        true
    )

    val classicBook = PhysicalBook (
        "1984",
        "George Orwell",
        1949,
        2,
        400.0,
        false
    )
    library.addBook(digitalBook)
    library.addBook(physicalBook)
    library.addBook(classicBook)

    println("\n--- Library Catalog ---")
    library.showBooks()

    println("\n--- Borrowing Books ---")
    library.borrowBook("Clean Code")
    library.borrowBook("1984")
    library.borrowBook("1984")
    library.borrowBook("1984") // should fail

    println("\n--- Returning Books ---")
    library.returnBook("1984")

    println("\n--- Search by Author ---")
    library.searchByAuthor("George Orwell")
}