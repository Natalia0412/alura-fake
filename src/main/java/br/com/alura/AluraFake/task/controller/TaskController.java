package br.com.alura.AluraFake.task.controller;

import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

public class TaskController {
    private final TaskService taskService;

    @PostMapping("/task/new/opentext")
    public ResponseEntity<OpenTextTaskDTO> newOpenTextExercise(@RequestBody @Valid OpenTextTaskDTO dto) {
        OpenTextTaskDTO response = taskService.createOpenTextTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity<SingleChoiceTaskDTO> newSingleChoice(@RequestBody @Valid SingleChoiceTaskDTO dto) {
        SingleChoiceTaskDTO response = taskService.createSingleChoiceTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity<MultipleChoiceTaskDTO> newMultipleChoice(@RequestBody @Valid MultipleChoiceTaskDTO dto) {
        MultipleChoiceTaskDTO response = taskService.createMultipleChoiceTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}