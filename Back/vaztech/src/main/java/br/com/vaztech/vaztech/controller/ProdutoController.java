package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.ProdutoResponseDTO; // CORREÇÃO: Importado DTO correto
import br.com.vaztech.vaztech.dto.ProdutoAddRequestDTO;
import br.com.vaztech.vaztech.dto.ProdutoAddResponseDTO;
import br.com.vaztech.vaztech.dto.ProdutoUpdateRequestDTO;
import br.com.vaztech.vaztech.dto.ProdutoUpdateResponseDTO;
import br.com.vaztech.vaztech.dto.StatusProdutoDTO;
import br.com.vaztech.vaztech.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoAddResponseDTO> adicionarProduto(@RequestBody ProdutoAddRequestDTO dto) throws RuntimeException{
        ProdutoAddResponseDTO response = produtoService.produtoAdd(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<List<StatusProdutoDTO>> listarProdutoStatus() {
        List<StatusProdutoDTO> response = produtoService.listarStatus();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ProdutoResponseDTO> listarProduto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ProdutoResponseDTO response = produtoService.listarProdutosComPaginacao(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{numeroSerie}")
    public ResponseEntity<ProdutoUpdateResponseDTO> updateProduto(
            @PathVariable String numeroSerie,
            @RequestBody ProdutoUpdateRequestDTO dto) throws RuntimeException {
        ProdutoUpdateResponseDTO response = produtoService.produtoUpdate(numeroSerie, dto);
        return ResponseEntity.ok(response);
    }

}