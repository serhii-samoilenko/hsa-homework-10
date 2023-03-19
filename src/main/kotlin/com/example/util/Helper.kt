package com.example.util

import io.quarkus.runtime.Startup
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@Startup
class Helper(config: AppConfig) {
    internal val mysqlConnectionPool = ConnectionPool(
        config.mysql().jdbcUrl(),
        config.mysql().username(),
        config.mysql().password(),
        config.mysql().connectionPoolSize(),
    )

    internal val postgresConnectionPool = ConnectionPool(
        config.postgres().jdbcUrl(),
        config.postgres().username(),
        config.postgres().password(),
        config.postgres().connectionPoolSize(),
    )
}
