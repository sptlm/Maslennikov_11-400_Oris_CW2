package kfu.itis.maslennikov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest {

    @Test
    void indexShouldPopulateModelForAnonymousUser() {
        IndexController controller = new IndexController();
        Model model = new ConcurrentModel();

        String view = controller.index(model, null);

        assertThat(view).isEqualTo("index");
        assertThat(model.getAttribute("isAuthenticated")).isEqualTo(false);
        assertThat(model.getAttribute("isUser")).isEqualTo(false);
        assertThat(model.getAttribute("isAdmin")).isEqualTo(false);
        assertThat(model.getAttribute("username")).isNull();
    }

    @Test
    void indexShouldPopulateModelForAuthenticatedUserAndAdmin() {
        IndexController controller = new IndexController();
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "ivan", "pwd", "ROLE_USER", "ROLE_ADMIN"
        );
        authentication.setAuthenticated(true);
        Model model = new ConcurrentModel();

        String view = controller.index(model, authentication);

        assertThat(view).isEqualTo("index");
        assertThat(model.getAttribute("isAuthenticated")).isEqualTo(true);
        assertThat(model.getAttribute("isUser")).isEqualTo(true);
        assertThat(model.getAttribute("isAdmin")).isEqualTo(true);
        assertThat(model.getAttribute("username")).isEqualTo("ivan");
    }
}