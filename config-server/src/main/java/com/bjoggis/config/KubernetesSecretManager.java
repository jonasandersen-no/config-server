package com.bjoggis.config;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!intellij")
class KubernetesSecretManager {

  private static final Logger log = LoggerFactory.getLogger(KubernetesSecretManager.class);

  private final KubernetesSecretRepository repository;

  KubernetesSecretManager(KubernetesSecretRepository repository) {
    this.repository = repository;
  }

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void sync() {

    log.info("Syncing kubernetes secrets");

    List<KubernetesSecret> secrets = repository.allSecrets();

    for (KubernetesSecret secret : secrets) {
      createOrUpdate(secret);
    }

    deleteNotPresent(secrets);

    log.info("Done with sync");
  }

  public void createOrUpdate(KubernetesSecret secret) {
    log.info("Creating or updating secret {}/{}", secret.namespace(), secret.name());
    try (KubernetesClient client = new KubernetesClientBuilder().build()) {

      HashMap<String, String> mutableData = new HashMap<>(secret.data());
      mutableData.replaceAll(
          (_, value) -> Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)));

      Secret kubernetesSecret =
          new SecretBuilder()
              .withNewMetadata()
              .withName(secret.name())
              .withLabels(
                  Map.of(
                      "app.kubernetes.io/managed-by", "config-server",
                      "app.kubernetes.io/component", "secret-sync"))
              // Add annotations for tracking creation time and source app details
              .withAnnotations(
                  Map.of(
                      "config-server.io/generator",
                      "spring-boot-config-server",
                      "config-server.io/last-synced",
                      Instant.now().toString()))
              .withNamespace(secret.namespace())
              .endMetadata()
              .withType("Opaque")
              .withData(mutableData)
              .build();

      client
          .secrets()
          .inNamespace(secret.namespace())
          .resource(kubernetesSecret)
          .forceConflicts()
          .serverSideApply();
    } catch (Exception e) {
      log.error("Failed with creating secret", e);
    }
  }

  public void deleteNotPresent(List<KubernetesSecret> desired) {
    Set<String> desiredKeys =
        desired.stream().map(s -> s.namespace() + "/" + s.name()).collect(Collectors.toSet());

    try (KubernetesClient client = new KubernetesClientBuilder().build()) {

      List<Secret> actual =
          client
              .secrets()
              .inAnyNamespace()
              .withLabel("app.kubernetes.io/managed-by", "config-server")
              .list()
              .getItems();

      for (Secret secret : actual) {
        String name = secret.getMetadata().getName();
        String namespace = secret.getMetadata().getNamespace();
        String key = namespace + "/" + name;

        if (desiredKeys.contains(key)) {
          log.debug("Found desired secret: {} in namespace {}", name, namespace);
          continue;
        }

        try {
          log.info("Orphaned secret found: {} in namespace {}. Deleting...", name, namespace);
          client
              .secrets()
              .inNamespace(namespace)
              .withName(name)
              .withTimeout(10, TimeUnit.SECONDS)
              .delete();
        } catch (Exception e) {
          log.error(
              "Failed to delete orphaned secret {} in namespace {}: {}",
              name,
              namespace,
              e.getMessage());
        }
      }
    }
  }

  public void delete(String namespace, String name) {
    try (KubernetesClient client = new KubernetesClientBuilder().build()) {
      List<StatusDetails> delete =
          client
              .secrets()
              .inNamespace(namespace)
              .withName(name)
              .withTimeout(10, TimeUnit.SECONDS)
              .delete();

      log.info("Deleted secret {}", name);
      log.info("Details: {}", delete);

    } catch (Exception e) {
      log.error("Failed to delete secret", e);
    }
  }
}
