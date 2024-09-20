package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;
  private PostRepository repository;
  private PostService service;
  private String path;
  private String method;
  private String param;

  @Override
  public void init() {
    final var context = new AnnotationConfigApplicationContext("ru.netology");
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого

    try {
      path = req.getRequestURI();
      method = req.getMethod();
      param = req.getParameter("id");
      // primitive routing
      if (method.equals("GET") && path.equals("/api/posts") && param == null) {
        controller.all(resp);
        return;
      }
      if (method.equals("GET") && path.matches("/api/posts") && !(param == null)) {
        // easy way
        final var id = Long.parseLong(param);
        controller.getById(id, resp);
        return;
      }
      if (method.equals("POST") && path.equals("/api/posts")) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals("DELETE") && path.matches("/api/posts")) {
        // easy way
        final var id = Long.parseLong(param);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}

