package microservices.book.multiplication.controller;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.service.MultiplicationService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(MultiplicationController.class)
@AutoConfigureJsonTesters
public class MultiplicationControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private MultiplicationService multiplicationService;

  @Autowired
  private JacksonTester<Multiplication> json;

  @Test
  public void getRandomMultiplicationTest() throws Exception {
    // given
    given(multiplicationService.createRandomMultiplication()).willReturn(new Multiplication(70,
        20));

    // when
    MockHttpServletResponse response = mvc.perform(
            get("/multiplications/random")
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString())
        .isEqualTo(json.write(new Multiplication(70, 20)).getJson());
  }
}
