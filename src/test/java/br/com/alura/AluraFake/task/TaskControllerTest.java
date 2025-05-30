package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.controller.TaskController;
import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.OptionDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;


    @Test
    void shouldReturn400_whenStatementIsTooShort() throws Exception {
        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "abc", 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("statement: O enunciado deve ter entre 4 e 255 caracteres"));
    }

    @Test
    void shouldThrowException_whenOrderIsZero() throws Exception {
        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "O que é encapsulamento?", 0);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("order: A ordem deve ser um número positivo"));

    }

    @Test
    void shouldThrowException_whenOrderIsNegative() throws Exception {
        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "O que é encapsulamento?", -1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("order: A ordem deve ser um número positivo"));

    }

    @Test
    void shouldPassValidation_whenOpenTextDTOIsValid() throws Exception {
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("Java", true),
                new OptionDTO("Banana", false),
                new OptionDTO("Python", false)
        );

        OpenTextTaskDTO dto = new OpenTextTaskDTO(
                1L,
                "Explique com suas palavras o que é uma classe em Java.",
                1

        );

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldThrowException_whenSingleChoiceTOptionTextTooShort()  throws Exception {
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("abc", true),
                new OptionDTO("Python", false)
        );

        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("options[0].text: Texto da opção deve ter entre 4 e 80 caracteres"));

    }

    @Test
    void shouldThrowException_whenSingleChoiceTOptionTextBlank()  throws Exception {
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO(null, true),
                new OptionDTO("Python", false)
        );

        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("options[0].text: Texto da opção é obrigatório"));

    }



    @Test
    void shouldPassValidation_whenSingleChoiceDTOIsValid() throws Exception {
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("Java", true),
                new OptionDTO("Banana", false),
                new OptionDTO("Python", false)
        );

        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(
                1L,
                "Qual dessas é uma linguagem de programação?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
    @Test
    void shouldReturn400_whenSinglechoiceOptionsLessThanTwo() throws Exception {
        List<OptionDTO> options = List.of(
                new OptionDTO("Java", true)
        );

        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("options: Deve haver no mínimo 2 e no maximo 6 opções"));
    }

    @Test
    void shouldReturn400_whenSinglechoiceOptionsMoreThanFive() throws Exception {
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("Azure", false),
                new OptionDTO("Sql Serve", false),
                new OptionDTO("Mysql", false),
                new OptionDTO("Oracle", false),
                new OptionDTO("Google", false),
                new OptionDTO("Golang", true)
        );

        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem ?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("options: Deve haver no mínimo 2 e no maximo 6 opções"));
    }

//    multiplechoice
    @Test
    void shouldReturn400_whenOptionsAreTooFew_forMultipleChoice() throws Exception {
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(
                1L,
                "Quais são linguagens de programação?",
                1,
                List.of(
                        new OptionDTO("Java", true) // Apenas uma opção (mínimo é 2)
                )
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("options: Deve haver no mínimo 3 e no maximo 5   opções"));
    }

    @Test
    void shouldReturn200_whenMultipleChoiceTaskIsValid() throws Exception {
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(
                1L,
                "Quais são linguagens de programação?",
                1,
                List.of(
                        new OptionDTO("Java", true),
                        new OptionDTO("Python", true),
                        new OptionDTO("Banana", false)
                )
        );

        when(taskService.createMultipleChoiceTask(any())).thenReturn(dto);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statement").value(dto.statement()))
                .andExpect(jsonPath("$.order").value(dto.order()))
                .andExpect(jsonPath("$.options.length()").value(dto.options().size()));
    }

    @Test
    void shouldCreateMultipleChoiceTaskSuccessfully() throws Exception {
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(
                1L,
                "Quais são linguagens de programação?",
                1,
                List.of(
                        new OptionDTO("Java", true),
                        new OptionDTO("Python", true),
                        new OptionDTO("Banana", false)
                )
        );

        when(taskService.createMultipleChoiceTask(any())).thenReturn(dto);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statement").value("Quais são linguagens de programação?"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.options.length()").value(3))
                .andExpect(jsonPath("$.options[0].text").value("Java"))
                .andExpect(jsonPath("$.options[0].isCorrect").value(true))
                .andExpect(jsonPath("$.options[1].text").value("Python"))
                .andExpect(jsonPath("$.options[1].isCorrect").value(true))
                .andExpect(jsonPath("$.options[2].text").value("Banana"))
                .andExpect(jsonPath("$.options[2].isCorrect").value(false));
    }

}
