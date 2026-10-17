export interface Endereco {
  cep: string;
  logradouro?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
}

export interface Cliente {
  id?: number;
  nome: string;
  endereco: Endereco;
}