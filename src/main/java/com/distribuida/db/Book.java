package com.distribuida.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
@Access(AccessType.FIELD)

//uso de queries nombrados para su reutilización

@NamedQueries(value = {
        @NamedQuery(name = "encontrarLibros",
                query = "SELECT b FROM Book b"),
        @NamedQuery(name = "ObtenerPorId",
                query = "SELECT b FROM Book b WHERE b.id = :id")
})
public class Book {

//Entidades JPA
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "isbn")
    private String isbn;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "price")
    private Double price;

}