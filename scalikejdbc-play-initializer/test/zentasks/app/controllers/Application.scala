package controllers

import javax.inject.{ Inject, Singleton }

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import views._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class Application @Inject() (controllerComponents: ControllerComponents)
  extends AbstractController(controllerComponents) {

  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result =>
      result match {
        case (email, password) => User.authenticate(email, password).isDefined
      })
  )

  /**
   * Login page.
   */
  def login = Action.async { implicit request =>
    Future.successful(Ok(html.login(loginForm)))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action.async { implicit request =>
    Future.successful(
      loginForm
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(html.login(formWithErrors)),
          user =>
            Redirect(routes.Projects.index()).withSession("email" -> user._1)
        )
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action.async {
    Future.successful(
      Redirect(routes.Application.login()).withNewSession
        .flashing("success" -> "You've been logged out")
    )
  }

  // -- Javascript routing

  def javascriptRoutes = Action.async { implicit request =>
    import routes.javascript._
    Future.successful(
      Ok(
        JavaScriptReverseRouter("jsRoutes")(
          Projects.add,
          Projects.delete,
          Projects.rename,
          Projects.addGroup,
          Projects.deleteGroup,
          Projects.renameGroup,
          Projects.addUser,
          Projects.removeUser,
          Tasks.addFolder,
          Tasks.renameFolder,
          Tasks.deleteFolder,
          Tasks.index,
          Tasks.add,
          Tasks.update,
          Tasks.delete
        )
      ).as("text/javascript")
    )
  }

}

/**
 * Provide security features
 */
trait Secured { self: BaseController =>

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) =
    Results.Redirect(routes.Application.login())

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }

  /**
   * Check if the connected user is a member of this project.
   */
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) =
    IsAuthenticated { user => request =>
      if (Project.isMember(project, user)) {
        f(user)(request)
      } else {
        Results.Forbidden
      }
    }

  /**
   * Check if the connected user is a owner of this task.
   */
  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) =
    IsAuthenticated { user => request =>
      if (Task.isOwner(task, user)) {
        f(user)(request)
      } else {
        Results.Forbidden
      }
    }

}
