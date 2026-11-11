package com.bjoggis.config;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kubernetes")
class KubernetesSecretController {

  private final KubernetesSecretManager manager;

  KubernetesSecretController(KubernetesSecretManager manager) {
    this.manager = manager;
  }

  record SecretRequest(String namespace, String name, String key, String value) {}

  @PostMapping
  void createSecret(@RequestBody SecretRequest body) {

    manager.create(body.namespace(), body.name(), body.key(), body.value());
  }

  @DeleteMapping("/namespace/{namespace}/name/{name}")
  void deleteSecret(@PathVariable String namespace, @PathVariable String name) {
    manager.delete(namespace, name);
  }
}
