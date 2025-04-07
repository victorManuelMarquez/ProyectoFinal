package ar.com.elbaden.connection;

@Deprecated
public final class CreateTableUsuarios implements StringQuery {

    @Override
    public String sql() {
        return String.format("""
                CREATE TABLE IF NOT EXISTS %s.%s (
                	id INT auto_increment NOT NULL,
                	nombre varchar(30) NOT NULL,
                	clave varchar(8) NOT NULL,
                	CONSTRAINT %s_PK PRIMARY KEY (id),
                	CONSTRAINT %s_UNIQUE UNIQUE KEY (nombre)
                )
                ENGINE=InnoDB
                DEFAULT CHARSET=utf8mb4;
                """,
                DataBank.DATABASE_NAME,
                DataBank.USERS_TABLE_NAME,
                DataBank.USERS_TABLE_NAME,
                DataBank.USERS_TABLE_NAME
        );
    }

}
