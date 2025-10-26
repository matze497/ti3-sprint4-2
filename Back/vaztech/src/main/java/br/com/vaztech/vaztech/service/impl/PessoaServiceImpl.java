package br.com.vaztech.vaztech.service.impl;

import br.com.vaztech.vaztech.dto.PessoaAddRequestDTO;
import br.com.vaztech.vaztech.dto.PessoaResponseDTO;
import br.com.vaztech.vaztech.dto.PessoaUpdateRequestDTO;
import br.com.vaztech.vaztech.entity.Pessoa;
import br.com.vaztech.vaztech.repository.PessoaRepository;
import br.com.vaztech.vaztech.service.PessoaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;

    @Override
    @Transactional
    public PessoaResponseDTO criarPessoa(PessoaAddRequestDTO dto) {
        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setCpfCnpj(dto.cpfCnpj());
        novaPessoa.setDataNascimento(dto.dataNascimento());
        novaPessoa.setOrigem(dto.origem());
        novaPessoa.setFuncao(dto.funcao());
        novaPessoa.setObservacao(dto.observacao());

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        return new PessoaResponseDTO(pessoaSalva);
    }

    @Override
    @Transactional
    public PessoaResponseDTO atualizarPessoa(Integer id, PessoaUpdateRequestDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada com ID: " + id));

        if (dto.nome() != null) {
            pessoa.setNome(dto.nome());
        }
        if (dto.cpfCnpj() != null) {
            pessoa.setCpfCnpj(dto.cpfCnpj());
        }
        if (dto.dataNascimento() != null) {
            pessoa.setDataNascimento(dto.dataNascimento());
        }
        if (dto.origem() != null) {
            pessoa.setOrigem(dto.origem());
        }
        if (dto.funcao() != null) {
            pessoa.setFuncao(dto.funcao());
        }
        if (dto.observacao() != null) {
            pessoa.setObservacao(dto.observacao());
        }

        Pessoa pessoaAtualizada = pessoaRepository.save(pessoa);

        return new PessoaResponseDTO(pessoaAtualizada);
    }

    @Override
    public List<PessoaResponseDTO> buscarTodasPessoas() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return pessoas.stream()
                .map(PessoaResponseDTO::new)
                .collect(Collectors.toList());
    }
}