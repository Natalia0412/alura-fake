package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}