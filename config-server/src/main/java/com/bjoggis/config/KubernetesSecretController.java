package com.bjoggis.config;

import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kubernetes")
class KubernetesSecretController {

  private final KubernetesSecretRepository repository;

  KubernetesSecretController(KubernetesSecretRepository repository) {
    this.repository = repository;
  }

  record SecretRequest(String namespace, String name, String key, String value) {}

  @PostMapping
  void createSecret(@RequestBody SecretRequest body) {
    repository.saveSecret(body.namespace, body.name, Map.of(body.key, body.value));
  }

  @GetMapping("/namespace/{namespace}/name/{name}")
  Map<String, String> getSecret(@PathVariable String namespace, @PathVariable String name) {
    return repository.getSecretData(namespace, name);
  }

  @DeleteMapping("/namespace/{namespace}/name/{name}")
  void deleteSecret(@PathVariable String namespace, @PathVariable String name) {
    repository.delete(namespace, name);
  }
}
