import { Component, OnInit, inject } from '@angular/core';
import { Cliente } from './cliente.model';
import { ClienteService } from './cliente.service';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private clienteService = inject(ClienteService);

  clientes: Cliente[] = [];
  erro = '';

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.clienteService.listar().subscribe({
      next: (dados) => (this.clientes = dados),
      error: () => (this.erro = 'Não foi possível carregar os clientes. O backend está rodando?')
    });
  }
}