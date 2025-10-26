import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { MultiSelectModule } from 'primeng/multiselect';
import { FormsModule } from '@angular/forms';

interface Pessoa {
    name: string,
    code: string
}

@Component({
  selector: 'app-pessoas-tabs',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule, MultiSelectModule, FormsModule],
  templateUrl: './pessoas-tabs.html',
})
export class PessoasTabsComponent {
  funcao!:  Pessoa[];

    selectedCities!: Pessoa[];

    ngOnInit() {
        this.funcao = [
            {name: 'Cliente', code: 'CLI'},
            {name: 'Fornecedor', code: 'FOR'},
            {name: 'Funcionário', code: 'FUN'},
        ];
}
}
