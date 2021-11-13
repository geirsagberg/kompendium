package io.bkbn.kompendium.auth

import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.auth.BasicAuthenticationProvider
import io.ktor.auth.jwt.jwt
import io.ktor.auth.jwt.JWTAuthenticationProvider
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.oas.schema.SecuritySchema
import io.ktor.auth.AuthenticationRouteSelector

object KompendiumAuth {

  init {
    Kompendium.addCustomRouteHandler(AuthenticationRouteSelector::class) { route, tail ->
      calculate(route.parent, tail)
    }
  }

  fun Authentication.Configuration.notarizedBasic(
    name: String? = null,
    configure: BasicAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = SecuritySchema(
      type = "http",
      scheme = "basic"
    )
    basic(name, configure)
  }

  fun Authentication.Configuration.notarizedJwt(
    name: String? = null,
    header: String? = null,
    scheme: String? = null,
    configure: JWTAuthenticationProvider.Configuration.() -> Unit
  ) {
    if (header == null || header == "Authorization") {
      Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = SecuritySchema(
        type = "http",
        scheme = scheme ?: "bearer"
      )
    } else {
      Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = SecuritySchema(
        type = "apiKey",
        name = header,
        `in` = "header"
      )
    }
    jwt(name, configure)
  }

  // TODO support other authentication providers (e.g., oAuth)?
}
