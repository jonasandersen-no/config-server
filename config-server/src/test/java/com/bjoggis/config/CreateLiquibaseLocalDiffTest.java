package com.bjoggis.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import javax.sql.DataSource;
import liquibase.database.Database;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jdbc.core.mapping.schema.LiquibaseChangeSetWriter;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("db-versioning")
@Tag("manual")
class CreateLiquibaseLocalDiffTest {

  @Autowired RelationalMappingContext context;

  @Autowired DataSource ds;

  @Test
  void generateFullSchema() throws IOException {

    // the change set will get appended, so we delete any pre existing file.
    Files.deleteIfExists(Path.of("cs-full.yaml"));

    context.setInitialEntitySet(Collections.singleton(Properties.class));
    LiquibaseChangeSetWriter writer = new LiquibaseChangeSetWriter(context);
    writer.writeChangeSet(new FileSystemResource("cs-full.yaml"));
  }

  @Test
  void diffWithRealDb() throws IOException {

    // the change set will get appended, so we delete any pre-existing file.
    deleteOldDbFiles();

    context.setInitialEntitySet(Collections.singleton(Properties.class));
    LiquibaseChangeSetWriter writer = new LiquibaseChangeSetWriter(context);

    // drop unused columns
    //    writer.setDropColumnFilter((table, column) -> !column.equalsIgnoreCase("special"));

    // for comparison with existing schema
    try (Database db = new PostgresDatabase()) {

      db.setConnection(new JdbcConnection(ds.getConnection()));

      String fileName = "db.X.yaml";
      int nextVersionId = findNextVersionId();
      fileName = fileName.replace("X", String.valueOf(nextVersionId));
      writer.writeChangeSet(new FileSystemResource(fileName), db);

    } catch (IOException | SQLException | LiquibaseException e) {
      throw new RuntimeException("Changeset generation failed", e);
    }
  }

  void deleteOldDbFiles() {
    FileSystemResource fileSystemResource = new FileSystemResource(".");
    File[] files = fileSystemResource.getFile().listFiles();
    if (files != null) {
      Arrays.stream(files)
          .filter(file -> file.getName().startsWith("db.") && file.getName().endsWith(".yaml"))
          .forEach(File::delete);
    }
  }

  int findNextVersionId() throws IOException {
    ClassPathResource classPathResource = new ClassPathResource("/db/changelog/changes");
    File[] files = classPathResource.getFile().listFiles();

    int max = 0;
    if (files != null) {
      max =
          Arrays.stream(files)
              .map(File::getName)
              .map(name -> name.split("\\."))
              .map(split -> split[1])
              .map(Integer::parseInt)
              .max(Integer::compareTo)
              .orElse(0);
    }
    return max + 1;
  }
}
