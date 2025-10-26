package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.FuncionarioAddRequestDTO;
import br.com.vaztech.vaztech.dto.FuncionarioResponseDTO;
import br.com.vaztech.vaztech.dto.FuncionarioUpdateRequestDTO;

import java.util.List;

public interface FuncionarioService {
    List<FuncionarioResponseDTO> listarFuncionarios();
    FuncionarioResponseDTO buscarFuncionarioPorId(Integer id);
    FuncionarioResponseDTO criarFuncionario(FuncionarioAddRequestDTO dto);
    FuncionarioResponseDTO atualizarFuncionario(Integer id, FuncionarioUpdateRequestDTO dto);
}