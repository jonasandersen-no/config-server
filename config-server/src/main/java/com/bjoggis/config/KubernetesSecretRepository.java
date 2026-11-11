package com.bjoggis.config;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

@Repository
public class KubernetesSecretRepository {

  private final JdbcTemplate jdbcTemplate;
  private final JsonMapper jsonMapper;

  public KubernetesSecretRepository(JdbcTemplate jdbcTemplate, JsonMapper jsonMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.jsonMapper = jsonMapper;
  }

  // Upsert (Insert or Update) secret data into Postgres
  public void saveSecret(String namespace, String secretName, Map<String, String> secretData) {
    // Convert Java Map to a JSON string for PostgreSQL jsonb
    String jsonValues = jsonMapper.writeValueAsString(secretData);

    String sql =
        """
            INSERT INTO kubernetes_secrets (namespace, secret_name, secret_data, updated_at)
            VALUES (?, ?, ?::jsonb, CURRENT_TIMESTAMP)
            ON CONFLICT (namespace, secret_name)
            DO UPDATE SET secret_data = EXCLUDED.secret_data, updated_at = CURRENT_TIMESTAMP
        """;

    jdbcTemplate.update(sql, namespace, secretName, jsonValues);
  }

  public List<KubernetesSecret> allSecrets() {
    String sql = "SELECT namespace, secret_name, secret_data FROM kubernetes_secrets";

    return jdbcTemplate.query(
        sql,
        (rs, _) -> {
          String json = rs.getString("secret_data");
          try {
            Map map = jsonMapper.readValue(json, Map.class);
            return new KubernetesSecret(
                rs.getString("namespace"), rs.getString("secret_name"), map);
          } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize secret JSON from database", e);
          }
        });
  }

  // Retrieve secret map by namespace and name
  public Map<String, String> getSecretData(String namespace, String secretName) {
    String sql =
        "SELECT secret_data FROM kubernetes_secrets WHERE namespace = ? AND secret_name = ?";

    return jdbcTemplate.queryForObject(
        sql,
        (rs, _) -> {
          String json = rs.getString("secret_data");
          try {
            return jsonMapper.readValue(json, Map.class);
          } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize secret JSON from database", e);
          }
        },
        namespace,
        secretName);
  }

  public void delete(String namespace, String name) {
    String sql = "DELETE FROM kubernetes_secrets WHERE  namespace = ? and secret_name = ?";

    jdbcTemplate.update(sql, namespace, name);
  }
}
