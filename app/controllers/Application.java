package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result addBizUser() {
        return Results.TODO;
    }

    public static Result addUser() {
        return Results.TODO;
    }
}
