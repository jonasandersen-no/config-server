package com.bjoggis.config;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class KubernetesSecretManager {

  private static final Logger log = LoggerFactory.getLogger(KubernetesSecretManager.class);
  private final PropertiesRepository propertiesRepository;

  KubernetesSecretManager(PropertiesRepository propertiesRepository) {
    this.propertiesRepository = propertiesRepository;
  }

  public void create(String namespace, String name, String key, String value) {
    log.info("KubernetesSecretManager");

    try (KubernetesClient client = new KubernetesClientBuilder().build()) {

      String hiddenValue =
          Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

      Secret secret =
          new SecretBuilder()
              .withNewMetadata()
              .withName(name)
              .withNamespace(namespace)
              .endMetadata()
              .withType("Opaque")
              .withData(Map.of(key, hiddenValue))
              .build();

      client.secrets().inNamespace(namespace).resource(secret).create();
    } catch (Exception e) {
      log.error("Failed with creating secret", e);
    }
  }
}
