# Literatura App - Backend en Consola con Spring Boot

Aplicación de consola en Java con Spring Boot para consumir información de libros y autores desde una API externa y guardarla en PostgreSQL.

---

## Características

- Buscar libros por título y registrarlos.
- Listar libros y autores.
- Consultar autores vivos en un año específico.
- Filtrar libros por idioma (`es`, `en`, `fr`, `pt`).
- Persistencia con PostgreSQL y JPA/Hibernate.

---

## Tecnologías

- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Jackson (JSON)
- Maven

---

## Configuración de PostgreSQL

```properties
spring.datasource.url=jdbc:postgresql://tu_host/literatura
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```
## Uso

mvn spring-boot:run

## Menú de consola

1 - Buscar libro por titulos
2 - Listar libros registrados
3 - Listar autores registrados
4 - Listar autores vivos en un año
5 - Listar libros por idioma
0 - Salir

## Ejemplo de salida

--------------LIBRO--------------
Titulo: Don Quijote
Autor: Cervantes Saavedra, Miguel de
Idioma: es
Número de descargas: 26848


