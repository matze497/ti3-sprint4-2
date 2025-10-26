package br.com.vaztech.vaztech.service.impl;

import br.com.vaztech.vaztech.dto.FuncionarioAddRequestDTO;
import br.com.vaztech.vaztech.dto.FuncionarioResponseDTO;
import br.com.vaztech.vaztech.dto.FuncionarioUpdateRequestDTO;
import br.com.vaztech.vaztech.entity.Funcionario;
import br.com.vaztech.vaztech.entity.Usuario;
import br.com.vaztech.vaztech.repository.FuncionarioRepository;
import br.com.vaztech.vaztech.service.FuncionarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionarioServiceImpl implements FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    private static final Integer STATUS_ATIVO = 1;
    private static final Integer ADMIN_ID = 1;

    @Override
    public List<FuncionarioResponseDTO> listarFuncionarios() {
        validarPermissaoAdmin();
        List<Funcionario> funcionarios = funcionarioRepository.findAll();

        return funcionarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FuncionarioResponseDTO buscarFuncionarioPorId(Integer id) {
        validarPermissaoAdmin();
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado com ID: " + id));

        return toDTO(funcionario);
    }

    @Override
    @Transactional
    public FuncionarioResponseDTO criarFuncionario(FuncionarioAddRequestDTO dto) {
        validarPermissaoAdmin();

        if (funcionarioRepository.existsByCodFuncionario(dto.codFuncionario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Código de funcionário já cadastrado.");
        }

        if (funcionarioRepository.existsByCpf(dto.cpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
        }

        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setCodFuncionario(dto.codFuncionario());
        novoFuncionario.setNome(dto.nome());
        novoFuncionario.setCpf(dto.cpf());
        novoFuncionario.setDataNascimento(dto.dataNascimento());
        novoFuncionario.setStatus(STATUS_ATIVO);

        Funcionario funcionarioSalvo = funcionarioRepository.save(novoFuncionario);
        return new FuncionarioResponseDTO(funcionarioSalvo);
    }

    @Override
    public FuncionarioResponseDTO atualizarFuncionario(Integer id, FuncionarioUpdateRequestDTO dto) {
        validarPermissaoAdmin();
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado com ID: " + id));

        validarDuplicidadeFuncionario(id, dto);

        if (dto.codFuncionario() != null) {
            funcionario.setCodFuncionario(dto.codFuncionario());
        }
        if (dto.nome() != null) {
            funcionario.setNome(dto.nome());
        }
        if (dto.cpf() != null) {
            funcionario.setCpf(dto.cpf());
        }
        if (dto.dataNascimento() != null) {
            funcionario.setDataNascimento(dto.dataNascimento());
        }
        if (dto.status() != null) {
            funcionario.setStatus(dto.status());
        }

        Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
        return new FuncionarioResponseDTO(funcionarioAtualizado);
    }

    private FuncionarioResponseDTO toDTO(Funcionario funcionario) {
        return new FuncionarioResponseDTO(
                funcionario.getId(),
                funcionario.getCodFuncionario(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getDataNascimento(),
                funcionario.getStatus()
        );
    }

    private void validarDuplicidadeFuncionario(Integer id, FuncionarioUpdateRequestDTO dto) {
        if (dto.codFuncionario() != null) {
            boolean codJaExiste = funcionarioRepository.existsByCodFuncionarioAndIdNot(dto.codFuncionario(), id);
            if (codJaExiste) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um funcionário com o código informado.");
            }
        }

        if (dto.cpf() != null) {
            boolean cpfJaExiste = funcionarioRepository.existsByCpfAndIdNot(dto.cpf(), id);
            if (cpfJaExiste) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um funcionário com o CPF informado.");
            }
        }
    }

    private void validarPermissaoAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuarioLogado)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        if (!usuarioLogado.getId().equals(ADMIN_ID)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado. Esta operação é restrita a administradores.");
        }
    }
}
