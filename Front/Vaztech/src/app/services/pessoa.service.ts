import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { AlterarPessoaBody, CadastrarPessoaBody, PessoaResponse } from '../models/pessoa.model';

@Injectable({
  providedIn: 'root',
})
export class PessoaService {
  http = inject(HttpClient);
  apiRoute = 'api/pessoa';

  buscarPessoas(): Observable<PessoaResponse[]> {
    return this.http.get<PessoaResponse[]>(`${environment.apiURL}/${this.apiRoute}`);
  }

  cadastrarPessoa(pessoa: CadastrarPessoaBody): Observable<PessoaResponse> {
    return this.http.post<PessoaResponse>(`${environment.apiURL}/${this.apiRoute}`, pessoa);
  }

  editarPessoa(pessoa: AlterarPessoaBody): Observable<PessoaResponse> {
    return this.http.put<PessoaResponse>(
      `${environment.apiURL}/${this.apiRoute}/${pessoa.id}`,
      pessoa
    );
  }
}
