package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.exception.ConflictException;
import com.openclassrooms.mddapi.exception.NotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Parcours complet de l'API sur une base H2 en mémoire. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    private static final String PASSWORD = "Passw0rd!";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Topic java;
    private Topic angular;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        java = topicRepository.save(topic("Java", "Le langage Java"));
        angular = topicRepository.save(topic("Angular", "Le framework Angular"));
    }

    @Test
    void loginWithEmailOrUsername() throws Exception {
        createUser("alice", "alice@mail.com");

        login("alice@mail.com");
        login("alice");

        mockMvc.perform(json(post("/api/auth/login"), Map.of("identifier", "alice", "password", "Wrong1234!")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerValidatesPasswordAndUniqueness() throws Exception {
        mockMvc.perform(json(post("/api/auth/register"),
                        Map.of("username", "alice", "email", "alice@mail.com", "password", "weak")))
                .andExpect(status().isBadRequest());

        createUser("alice", "alice@mail.com");

        mockMvc.perform(json(post("/api/auth/register"),
                        Map.of("username", "other", "email", "ALICE@mail.com", "password", PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Email déjà utilisé")));
        mockMvc.perform(json(post("/api/auth/register"),
                        Map.of("username", "alice", "email", "other@mail.com", "password", PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Nom d'utilisateur déjà utilisé")));
    }

    @Test
    void protectedRoutesRequireValidToken() throws Exception {
        mockMvc.perform(get("/api/feed")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentification requise"));
        mockMvc.perform(get("/api/feed").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profileCanBeReadAndUpdated() throws Exception {
        String token = createUserAndLogin("alice", "alice@mail.com");
        createUser("bob", "bob@mail.com");

        mockMvc.perform(auth(get("/api/users/me"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.password").doesNotExist());

        assertThatThrownBy(() -> mockMvc.perform(json(auth(put("/api/users/me"), token),
                        Map.of("username", "bob", "email", "alice@mail.com"))))
                .hasRootCauseInstanceOf(ConflictException.class);

        mockMvc.perform(json(auth(put("/api/users/me"), token),
                        Map.of("username", "alice2", "email", "alice2@mail.com", "password", "NewPassw0rd!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("alice2"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        mockMvc.perform(json(post("/api/auth/login"), Map.of("identifier", "alice2", "password", "NewPassw0rd!")))
                .andExpect(status().isOk());
    }

    @Test
    void subscriptionsDriveTheFeed() throws Exception {
        String alice = createUserAndLogin("alice", "alice@mail.com");
        String bob = createUserAndLogin("bob", "bob@mail.com");

        mockMvc.perform(auth(post("/api/topics/{id}/subscribe", java.getId()), alice))
                .andExpect(status().isCreated());
        mockMvc.perform(auth(post("/api/topics/{id}/subscribe", java.getId()), alice))
                .andExpect(status().isCreated());
        assertThatThrownBy(() -> mockMvc.perform(auth(post("/api/topics/{id}/subscribe", 9999), alice)))
                .hasRootCauseInstanceOf(NotFoundException.class);

        mockMvc.perform(auth(get("/api/topics"), alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.title == 'Java')].subscribed").value(contains(true)))
                .andExpect(jsonPath("$[?(@.title == 'Angular')].subscribed").value(contains(false)));
        mockMvc.perform(auth(get("/api/users/me/subscriptions"), alice))
                .andExpect(jsonPath("$[*].title").value(contains("Java")));

        createArticle(bob, java.getId(), "Premier article Java");
        createArticle(bob, java.getId(), "Second article Java");
        createArticle(bob, angular.getId(), "Article Angular");

        mockMvc.perform(auth(get("/api/feed"), alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title").value(contains("Second article Java", "Premier article Java")))
                .andExpect(jsonPath("$[0].authorUsername").value("bob"))
                .andExpect(jsonPath("$[0].topicTitle").value("Java"));
        mockMvc.perform(auth(get("/api/feed").param("sort", "asc"), alice))
                .andExpect(jsonPath("$[*].title").value(contains("Premier article Java", "Second article Java")));

        mockMvc.perform(auth(delete("/api/topics/{id}/subscribe", java.getId()), alice))
                .andExpect(status().isNoContent());
        mockMvc.perform(auth(get("/api/feed"), alice))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void articlesCanBeReadAndCommented() throws Exception {
        String alice = createUserAndLogin("alice", "alice@mail.com");
        String bob = createUserAndLogin("bob", "bob@mail.com");
        long articleId = createArticle(alice, java.getId(), "Mon article");

        mockMvc.perform(json(auth(post("/api/articles"), alice), Map.of("topicId", java.getId(), "title", "")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(auth(get("/api/articles/{id}", articleId), bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mon article"))
                .andExpect(jsonPath("$.authorUsername").value("alice"))
                .andExpect(jsonPath("$.topicTitle").value("Java"));
        assertThatThrownBy(() -> mockMvc.perform(auth(get("/api/articles/{id}", 9999), bob)))
                .hasRootCauseInstanceOf(NotFoundException.class);

        mockMvc.perform(json(auth(post("/api/articles/{id}/comments", articleId), bob), Map.of("content", "Bravo")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorUsername").value("bob"));
        mockMvc.perform(json(auth(post("/api/articles/{id}/comments", articleId), alice), Map.of("content", "Merci")))
                .andExpect(status().isCreated());
        mockMvc.perform(json(auth(post("/api/articles/{id}/comments", articleId), alice), Map.of("content", " ")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(auth(get("/api/articles/{id}/comments", articleId), alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].content").value(contains("Bravo", "Merci")));
    }

    /** Crée l'utilisateur directement en base (mot de passe haché comme le ferait l'inscription). */
    private void createUser(String username, String email) {
        userRepository.save(new User(username, email, passwordEncoder.encode(PASSWORD)));
    }

    private String createUserAndLogin(String username, String email) throws Exception {
        createUser(username, email);
        return login(username);
    }

    private String login(String identifier) throws Exception {
        String body = mockMvc.perform(json(post("/api/auth/login"), Map.of("identifier", identifier, "password", PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asString();
    }

    private long createArticle(String token, Long topicId, String title) throws Exception {
        String body = mockMvc.perform(json(auth(post("/api/articles"), token),
                        Map.of("topicId", topicId, "title", title, "content", "Contenu de " + title)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, Object body) {
        return request.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
    }

    private static Topic topic(String title, String description) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDescription(description);
        return topic;
    }
}
