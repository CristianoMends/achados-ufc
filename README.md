# Achados UFC

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Badge do Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Badge do Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Badge do Jetpack Compose"/>
  <img src="https://img.shields.io/badge/NestJS-E0234E?style=for-the-badge&logo=nestjs&logoColor=white" alt="Badge do NestJS"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Badge do Firebase"/>
</p>

<p align="center">
  <img src="https://github.com/CristianoMends/achados-ufc/blob/main/Screenshot_20250725_112952.png?raw=true" height="450px"/>
</p>

## 🎯 Sobre o Projeto

**Achados UFC** é uma solução completa, desenvolvida como projeto final para a disciplina de Desenvolvimento de Software para Dispositivos Móveis da **Universidade Federal do Ceará**.

O objetivo é centralizar e simplificar o processo de busca e devolução de itens perdidos dentro do campus, conectando a comunidade acadêmica através de uma plataforma moderna, intuitiva e em tempo real.

---

## ✨ Funcionalidades Principais

* **Publicação de Itens:** Usuários podem cadastrar itens que encontraram ou perderam, adicionando título, descrição, local e uma foto.
* **Chat em Tempo Real:** Um sistema de mensagens integrado com **Firebase Firestore** permite que os usuários conversem diretamente sobre os itens.
* **Notificações Inteligentes:** Receba notificações no app quando uma nova mensagem chegar.
* **Upload Robusto:** O envio de imagens funciona em segundo plano com **WorkManager**, garantindo que o upload seja concluído mesmo que o app seja fechado ou a conexão falhe, com uma notificação de progresso.
* **Autenticação Segura:** Login com Google e e-mail/senha através do **Firebase Authentication**, com gerenciamento de sessão via tokens JWT na API.
* **Busca e Filtros:** Encontre itens facilmente com uma interface limpa e organizada.

---

## 🛠️ Tecnologias Utilizadas

O projeto é dividido em duas partes principais: o aplicativo Android nativo e a API backend.

### 📱 Frontend (Android)

* **Linguagem:** 100% [Kotlin](https://kotlinlang.org/), seguindo as melhores práticas.
* **Interface Gráfica:** [Jetpack Compose](https://developer.android.com/jetpack/compose) para uma UI declarativa, moderna e reativa.
* **Arquitetura:** MVVM (Model-View-ViewModel) para uma clara separação de responsabilidades.
* **Gerenciamento de Dependências:** [Koin](https://insert-koin.io/) para uma injeção de dependências simples e eficiente.
* **Comunicação com API:** [Retrofit](https://square.github.io/retrofit/) para chamadas de rede seguras e eficientes.
* **Tempo Real e Banco de Dados:** [Firebase Firestore](https://firebase.google.com/docs/firestore) para o chat e [Room](https://developer.android.com/training/data-storage/room) para cache local.
* **Tarefas em Segundo Plano:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) para agendar e executar tarefas robustas, como o upload de imagens.

### ⚙️ Backend (API)

* **Framework:** [NestJS](https://nestjs.com/) (Node.js/TypeScript) para uma API robusta, escalável e organizada.
* **Banco de Dados:** [PostgreSQL](https://www.postgresql.org/) para armazenamento relacional dos dados de usuários e itens.
* **Autenticação:** [JWT (JSON Web Tokens)](https://jwt.io/) para proteger os endpoints e gerenciar as sessões dos usuários.
* **Armazenamento de Mídia:** Estratégia de upload flexível, com suporte a armazenamento local e na nuvem via [Vercel Blob](https://vercel.com/storage/blob).

---

## 🚀 Como Começar

Para executar o projeto localmente, siga estes passos:

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/CristianoMends/achados-ufc.git
    ```

2.  **Configure o Backend:**
    * Navegue até a pasta da API.
    * Instale as dependências: `npm install`
    * Configure suas variáveis de ambiente em um arquivo `.env`.
    * Inicie o servidor: `npm run start:dev`

3.  **Configure o App Android:**
    * Abra o projeto no Android Studio.
    * Crie um arquivo `local.properties` na raiz do projeto.
    * Adicione a URL base da sua API neste arquivo:
        ```properties
        BASE_URL="http://SEU_IP_LOCAL:3000/"
        ```
    * Sincronize o projeto com o Gradle e execute no seu emulador ou dispositivo.


---

## 🖼 Galeria

<table>
  <tr>
    <td align="center"><b>Tela de Login</b></td>
    <td align="center"><b>Feed</b></td>
    <td align="center"><b>Detalhes do Item</b></td>
  </tr>
  <tr>
    <td><img width="250" alt="Screenshot da tela de login" src="https://github.com/user-attachments/assets/ea7d9b94-20b9-464f-9afc-70484f3ef248"/></td>
    <td><img width="250" alt="Screenshot do feed de itens" src="https://github.com/user-attachments/assets/99bb5a37-4bd9-40b7-9e0e-13a5521f857b"/></td>
    <td><img width="250" alt="Screenshot_20250725_113901" src="https://github.com/user-attachments/assets/9c7bb03e-8070-4218-9d68-134e3f274cf4"/></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><b>Chat em Tempo Real</b></td>
    <td align="center"><b>Lista de Conversas</b></td>
    <td align="center"><b>Perfil de usuário</b></td>
  </tr>
  <tr>
    <td><img width="250" alt="Screenshot da tela de chat" src="https://github.com/user-attachments/assets/966d6361-3539-4d5d-a1bc-79da57306ae8"/></td>
    <td><img width="250" alt="Screenshot_20250725_115051" src="https://github.com/user-attachments/assets/50096479-171b-46bb-b531-d654045843d8"/></td>
    <td><img width="250" alt="Screenshot_20250725_114103" src="https://github.com/user-attachments/assets/fb764592-242b-434a-9fea-2c8a350a2bfc" /></td>
  </tr>
</table>

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
