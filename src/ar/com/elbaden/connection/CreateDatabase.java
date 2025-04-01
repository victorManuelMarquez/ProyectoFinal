package ar.com.elbaden.connection;

import static ar.com.elbaden.connection.DataBank.DATABASE_NAME;

public final class CreateDatabase implements StringQuery {

    @Override
    public String sql() {
        return "CREATE DATABASE IF NOT EXISTS `" + DATABASE_NAME + "`;";
    }

}
