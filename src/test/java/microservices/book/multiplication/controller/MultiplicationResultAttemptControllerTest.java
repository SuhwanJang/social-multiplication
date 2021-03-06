package microservices.book.multiplication.controller;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.service.MultiplicationService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@WebMvcTest(MultiplicationResultAttemptController.class)
public class MultiplicationResultAttemptControllerTest {

  @MockBean
  private MultiplicationService multiplicationService;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<MultiplicationResultAttempt> jsonResult;

  @Autowired
  private JacksonTester<MultiplicationResultAttempt> jsonResponse;

  @Autowired
  private JacksonTester<List<MultiplicationResultAttempt>> jsonResultAttemptList;

  @Test
  public void postResultReturnCorrect() throws Exception {
    genericParameterizedTest(true);
  }

  @Test
  public void postResultReturnWrong() throws Exception {
    genericParameterizedTest(false);
  }

  private void genericParameterizedTest(final boolean correct) throws Exception {
    // given
    given(multiplicationService
            .checkAttempt(any(MultiplicationResultAttempt.class)))
            .willReturn(correct);

    User user = new User("john");
    Multiplication multiplication = new Multiplication(50, 70);
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user,
        multiplication, 3500, correct);

    // when
    MockHttpServletResponse response = mvc.perform(post("/results")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonResult.write(attempt).getJson()))
        .andReturn().getResponse();

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString())
        .isEqualTo(jsonResponse.write(
                new MultiplicationResultAttempt(
                        attempt.getUser(),
                        attempt.getMultiplication(),
                        attempt.getResultAttempt(), correct
                )
        ).getJson());
  }


  @Test
  public void getUserStats() throws Exception {
    // given
    final String username = "John_doe";
    User user = new User(username);
    Multiplication multiplication = new Multiplication(50, 70);
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3500, true);
    List<MultiplicationResultAttempt> recentAttempts = Lists.newArrayList(attempt, attempt);
    given(multiplicationService.getStatsForUser(username)).willReturn(recentAttempts);

    // when
    MockHttpServletResponse response = mvc.perform(
                    get("/results")
                            .param("alias", username))
            .andReturn().getResponse();

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString())
            .isEqualTo(jsonResultAttemptList.write(recentAttempts).getJson());
  }

}
