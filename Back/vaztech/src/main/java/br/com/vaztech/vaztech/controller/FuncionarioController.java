package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.FuncionarioAddRequestDTO;
import br.com.vaztech.vaztech.dto.FuncionarioResponseDTO;
import br.com.vaztech.vaztech.dto.FuncionarioUpdateRequestDTO;
import br.com.vaztech.vaztech.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funcionario")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarFuncionarios() {
        List<FuncionarioResponseDTO> response = funcionarioService.listarFuncionarios();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> listarFuncionarioUnico(@PathVariable Integer id) {
        FuncionarioResponseDTO response = funcionarioService.buscarFuncionarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> criarFuncionario(@Valid @RequestBody FuncionarioAddRequestDTO dto) {
        FuncionarioResponseDTO response = funcionarioService.criarFuncionario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable Integer id, @Valid @RequestBody FuncionarioUpdateRequestDTO dto) {
        FuncionarioResponseDTO response = funcionarioService.atualizarFuncionario(id, dto);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("cpf")) {
            return Map.of("erro", "O CPF  fornecido já está cadastrado.");
        }
        return Map.of("erro", "Violação de dados. Um campo único (como código ou CPF) já existe.");
    }
}