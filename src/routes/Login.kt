package fan.zheyuan.routes

import fan.zheyuan.Index
import fan.zheyuan.Login
import fan.zheyuan.YouKubeSession
import fan.zheyuan.extends.*
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.form
import io.ktor.http.CacheControl
import io.ktor.http.HttpMethod
import io.ktor.locations.handle
import io.ktor.locations.location
import io.ktor.locations.url
import io.ktor.routing.*
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.html.*

fun Route.login(users: UserHashedTableAuth) {
    val myFormAuthentication = "myFormAuthentication"
    application.install(Authentication) {
        form(myFormAuthentication) {
            userParamName = Login::userName.name
            passwordParamName = Login::password.name
            challenge { call.respondRedirect(call.url(Login(it?.name ?: ""))) }
            validate { users.authenticate(it) }
        }
    }

    location<Login> {
        authenticate(myFormAuthentication) {
            post {
                val principal = call.principal<UserIdPrincipal>()
                call.sessions.set(YouKubeSession(principal!!.name))
                call.respondRedirect(Index())
            }
        }

        method(HttpMethod.Get) {
            handle<Login> {
                call.respondDefaultHtml(emptyList(), CacheControl.Visibility.Public) {
                    h2 { +"Login" }
                    form(
                        call.url(Login()) { parameters.clear() },
                        classes = "pure-form-stacked",
                        encType = FormEncType.applicationXWwwFormUrlEncoded,
                        method = FormMethod.post
                    ) {
                        acceptCharset = "utf-8"

                        label {
                            +"Username: "
                            textInput {
                                name = Login::userName.name
                                value = it.userName
                            }
                        }
                        label {
                            +"Password: "
                            passwordInput {
                                name = Login::password.name
                            }
                        }
                        submitInput(classes = "pure-button pure-button-primary") {
                            value = "Login"
                        }
                    }
                }
            }
        }
    }
}