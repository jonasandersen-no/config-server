package com.bjoggis.config;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.FragmentsRendering;

@Controller
@RequestMapping("/")
class IndexController {

  @GetMapping()
  String index(Model model) {
    model.addAttribute("title", "SOMETHING");
    return "index";
  }

  @GetMapping("/new-title")
  @HxRequest
  FragmentsRendering get() {
    return FragmentsRendering.with(
            "fragments/myfragments :: myfragment", Map.of("data", UUID.randomUUID().toString()))
        .build();
  }
}
