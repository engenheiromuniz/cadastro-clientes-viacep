import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Cliente } from './cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/clientes';

  listar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  inserir(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  atualizar(id: number, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, cliente);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}