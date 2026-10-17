import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Cliente } from './cliente.model';
import { ClienteService } from './cliente.service';

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private clienteService = inject(ClienteService);

  clientes: Cliente[] = [];
  erro = '';

  // Campos do formulário
  nome = '';
  cep = '';
  idEmEdicao: number | null = null;

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.clienteService.listar().subscribe({
      next: (dados) => (this.clientes = dados),
      error: () => (this.erro = 'Não foi possível carregar os clientes. O backend está rodando?')
    });
  }

  salvar(): void {
    this.erro = '';
    const cliente: Cliente = { nome: this.nome, endereco: { cep: this.cep } };

    // Se há um id em edição, atualiza (PUT); senão, insere (POST)
    const requisicao = this.idEmEdicao
      ? this.clienteService.atualizar(this.idEmEdicao, cliente)
      : this.clienteService.inserir(cliente);

    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      // Mostra a mensagem que o backend devolve no ProblemDetail (ex.: "CEP não encontrado")
      error: (e: HttpErrorResponse) => (this.erro = e.error?.detail ?? 'Erro ao salvar o cliente.')
    });
  }

  editar(cliente: Cliente): void {
    this.erro = '';
    this.idEmEdicao = cliente.id ?? null;
    this.nome = cliente.nome;
    this.cep = cliente.endereco.cep;
  }

  limpar(): void {
    this.idEmEdicao = null;
    this.nome = '';
    this.cep = '';
  }
}