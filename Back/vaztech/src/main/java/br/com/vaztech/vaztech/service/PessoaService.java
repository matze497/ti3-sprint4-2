package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.PessoaAddRequestDTO;
import br.com.vaztech.vaztech.dto.PessoaResponseDTO;
import br.com.vaztech.vaztech.dto.PessoaUpdateRequestDTO;
import java.util.List;

public interface PessoaService {
    PessoaResponseDTO criarPessoa(PessoaAddRequestDTO dto);
    PessoaResponseDTO atualizarPessoa(Integer id, PessoaUpdateRequestDTO dto);
    List<PessoaResponseDTO> buscarTodasPessoas();
}