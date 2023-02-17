package com.distribuida.rest;
import com.distribuida.clientes.authors.AuthorRestProxy;
import com.distribuida.clientes.authors.AuthorsCliente;
import com.distribuida.db.Book;
import com.distribuida.dtos.BookDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.List;
import java.util.stream.Collectors;

@Path("/books")

public class BookRest {


    @PersistenceContext(unitName = "test")
    private EntityManager entityManager;

    @RestClient
    @Inject
    AuthorRestProxy proxyAuthor;
    @GET
    @Operation()
    @RequestBody(
            name = "books",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> findAll() {
        return entityManager.createNamedQuery("encontrarLibros", Book.class).getResultList();
    }

    @GET
    @Path("/{id}")

    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Produces(MediaType.APPLICATION_JSON)
    public Book findById(@PathParam("id") String id) {
        try {
            return entityManager.find(Book.class, Integer.valueOf(id));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("No se pudo encontrar el libro " + id);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteById(@PathParam("id") String id) {
        Book book = findById(id);
        entityManager.remove(book);
    }

    @POST
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void create(Book book) {
        try {
            entityManager.persist(book);
        } catch (Exception e) {
            throw new BadRequestException("No se puedo actualizar el libro " + book.getId());
        }
    }
    @PUT
    @Path("/{id}")
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void update(@PathParam("id") String id, Book book) {
        try {
             Book newBook = findById(id);
             newBook.setAuthor(book.getAuthor());
             newBook.setTitle(book.getTitle());
             newBook.setIsbn(book.getIsbn());
             newBook.setPrice(book.getPrice());
             entityManager.persist(newBook);
        } catch (Exception e) {
                throw new BadRequestException("No se pudo actualizar " + book.getId());
        }
    }

    @GET
    @Path("/all")
    @Operation()
    public List<BookDto> findAllCompleto() throws Exception {
        var books = entityManager.createNamedQuery("encontrarLibros", Book.class).getResultList();
        List<BookDto> ret = books.stream()
                .map(s -> {
                    AuthorsCliente author = proxyAuthor.findById(s.getId().longValue());
                    return new BookDto(
                            s.getId(),
                            s.getIsbn(),
                            s.getTitle(),
                            s.getAuthor(),
                            s.getPrice(),
                            String.format("%s, %s", author.getLastName(), author.getFirstName())
                    );
                })
                .collect(Collectors.toList());

        return ret;
    }
}
