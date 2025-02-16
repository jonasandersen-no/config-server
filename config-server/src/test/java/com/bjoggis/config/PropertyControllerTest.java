package com.bjoggis.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

class PropertyControllerTest {

  private static final Logger log = LoggerFactory.getLogger(PropertyControllerTest.class);

  @Nested
  class Create {

    @Test
    void ok() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();

      mvc.post().uri("/properties")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
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
          .convertTo(Properties.class)
          .extracting(Properties::getId)
          .matches(repository::existsById);
    }

    @Test
    void tooLargeValue() throws JsonProcessingException {
      MockMvcTester mvc = createFixture().mvc();

      mvc.post().uri("/properties")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
              {
              "application": "sample-app",
              "profile": "dev",
              "label": "master",
              "key": "property.length",
              "value": "%s",
              "secret": false
              }
              """.formatted("a".repeat(10001)))
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

      mvc.get().uri("/properties/1")
          .assertThat()
          .bodyJson()
          .convertTo(Properties.class)
          .extracting(Properties::getId)
          .isEqualTo(1L);
    }

    @Test
    void getMultiple() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      repository.save(new Properties("key", "value"));
      repository.save(new Properties("key2", "value2"));

      ListAssert<Properties> propertiesListAssert = mvc.get().uri("/properties")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(Properties.class))
          .contains(new Properties(1L, "key", "value"),
              new Properties(2L, "key2", "value2"));
    }

    @Test
    void findByApplication() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      Properties properties = new Properties();
      properties.setApplication("sample-app");
      repository.save(properties);
      repository.save(new Properties("key", "value"));

      Properties expected = new Properties(1L);
      expected.setApplication("sample-app");

      mvc.get().uri("/properties/application/{application}", "sample-app")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(Properties.class))
          .containsExactly(expected);
    }

    @Test
    void findByKey() {
      Fixture fixture = createFixture();
      MockMvcTester mvc = fixture.mvc();
      InMemoryPropertiesRepository repository = fixture.repository();
      repository.save(new Properties("someKey", "value"));
      repository.save(new Properties("other", "value2"));

      mvc.get().uri("/properties/key/{key}", "key")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(Properties.class))
          .containsExactly(new Properties(1L, "someKey", "value"));
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

      Properties expected = new Properties(1L, "key", "value");
      expected.setApplication("sample-app");
      expected.setProfile("dev");

      mvc.get().uri("/properties/application/{application}/profile/{profile}", "sample-app", "dev")
          .assertThat()
          .bodyJson()
          .convertTo(InstanceOfAssertFactories.list(Properties.class))
          .containsExactly(expected);

    }
  }

  private Fixture createFixture() {
    InMemoryPropertiesRepository repository = new InMemoryPropertiesRepository();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    MockMvcTester mvc = MockMvcTester.of(new PropertyController(repository))
        .withHttpMessageConverters(List.of(converter));

    return new Fixture(mvc, repository);
  }

  private record Fixture(MockMvcTester mvc, InMemoryPropertiesRepository repository) {

  }
}