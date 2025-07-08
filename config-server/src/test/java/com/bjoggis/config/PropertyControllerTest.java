package com.bjoggis.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

class PropertyControllerTest {

  @Nested
  class Create {

    @Test
    void ok() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();

      mvc.post()
          .uri("/properties")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
              """
              {
              "application": "sample-app",
              "profile": "dev",
              "label": "master",
              "key": "property.length",
              "value": "50",
              "secret": false
              }
              """)
          .assertThat()
          .hasStatusOk()
          .bodyJson()
          .convertTo(PropertiesView.class)
          .extracting(PropertiesView::id)
          .matches(repository::existsById);
    }

    @Test
    void failOnLargeValue() throws JsonProcessingException {
      MockMvcTester mvc = createFixture().mvc();

      mvc.post()
          .uri("/properties")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
              """
              {
              "application": "sample-app",
              "profile": "dev",
              "label": "master",
              "key": "property.length",
              "value": "%s",
              "secret": false
              }
              """
                  .formatted("a".repeat(10001)))
          .assertThat()
          .hasStatus4xxClientError();
    }
  }

  @Nested
  class Get {

    @Test
    void getOne() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      repository.save(new Properties("key", "value"));

      mvc.get()
          .uri("/properties/1")
          .assertThat()
          .bodyJson()
          .convertTo(PropertiesView.class)
          .extracting(PropertiesView::id)
          .isEqualTo(1L);
    }

    @Test
    void getMultiple() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      repository.save(new Properties("key", "value"));
      repository.save(new Properties("key2", "value2"));

      mvc.get()
          .uri("/properties")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(PropertiesView.class))
          .contains(
              new PropertiesView(1L, "application", "default", "key", "value", false),
              new PropertiesView(2L, "application", "default", "key2", "value2", false));
    }

    @Test
    void findByApplication() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      Properties properties = new Properties();
      properties.setApplication("sample-app");
      properties.setName("key2");
      properties.setValue("value");
      repository.save(properties);
      repository.save(new Properties("key", "value"));

      PropertiesView expected =
          new PropertiesView(1L, "sample-app", "default", "key2", "value", false);

      mvc.get()
          .uri("/properties/application/{application}", "sample-app")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(PropertiesView.class))
          .containsExactly(expected);
    }

    @Test
    void findByKey() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      repository.save(new Properties("someKey", "value"));
      repository.save(new Properties("other", "value2"));

      mvc.get()
          .uri("/properties/key/{key}", "key")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(PropertiesView.class))
          .containsExactly(
              new PropertiesView(1L, "application", "default", "someKey", "value", false));
    }

    @Test
    void findByApplicationAndProfile() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      Properties property = new Properties("key", "value");
      property.setApplication("sample-app");
      property.setProfile("dev");
      repository.save(property);
      Properties property2 = new Properties("key", "value2");
      property2.setApplication("sample-app");
      property2.setProfile("test");
      repository.save(property2);

      PropertiesView expected = new PropertiesView(1L, "sample-app", "dev", "key", "value", false);

      mvc.get()
          .uri("/properties/application/{application}/profile/{profile}", "sample-app", "dev")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(PropertiesView.class))
          .containsExactly(expected);
    }

    @Nested
    class Delete {

      @Test
      void deleteOne() {
        Fixture fixture = createFixture();
        MockMvcTester mvc = fixture.mvc();
        InMemoryPropertiesRepository repository = fixture.repository();
        Properties saved = repository.save(new Properties("key", "value"));

        mvc.delete()
            .uri("/properties/%s".formatted(saved.getId()))
            .assertThat()
            .hasStatus2xxSuccessful();
      }

      @Test
      void deleteNonExistingGivesNotFound() {
        Fixture fixture = createFixture();
        MockMvcTester mvc = fixture.mvc();

        mvc.delete()
            .uri("/properties/%s".formatted("1"))
            .assertThat()
            .hasStatus(HttpStatus.NOT_FOUND);
      }
    }
  }

  private Fixture createFixture() {
    InMemoryPropertiesRepository repository = new InMemoryPropertiesRepository();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    MockMvcTester mvc =
        MockMvcTester.of(new PropertyController(repository))
            .withHttpMessageConverters(List.of(converter));

    return new Fixture(mvc, repository);
  }

  private record Fixture(MockMvcTester mvc, InMemoryPropertiesRepository repository) {}
}
