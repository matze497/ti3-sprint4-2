export type PessoaResponse = {
  id: number;
  nome: string;
  cpfCnpj: string;
  dataNascimento: Date | null;
  origem: string | null;
  funcao: string | null;
  observacao: string | null;
};

export type CadastrarPessoaBody = {
  nome: string;
  cpfCnpj: string;
  dataNascimento: Date | null;
  origem: string | null;
  funcao: string | null;
  observacao: string | null;
};

export type AlterarPessoaBody = {
  id: number;
  nome: string;
  cpfCnpj: string;
  dataNascimento: Date | null;
  origem: string | null;
  funcao: string | null;
  observacao: string | null;
};
