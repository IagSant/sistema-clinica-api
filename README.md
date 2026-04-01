  Sistema de Clínica (Full Stack)

Aplicação completa para gerenciamento de clínica, desenvolvida com Spring Boot no backend e HTML, CSS e JavaScript no frontend.
O sistema permite controle de pacientes, agendamentos, agenda com calendário próprio e envio de notificações via WhatsApp.

  Funcionalidades

✔ Cadastro de pacientes (CRUD)
✔ Agendamento e reagendamento de consultas
✔ Visualização de agenda diária e semanal
✔ Gerenciamento de eventos
✔ Captação de leads via formulário
✔ Envio de notificações via WhatsApp (Twilio)

  Tecnologias utilizadas
  
  Backend
Java
Spring Boot
Spring Data JPA
Hibernate
MySQL

  Frontend
HTML5
CSS3
JavaScript

  Ferramentas
Postman
Git & GitHub

  Estrutura do projeto
sistema-clinica
 ├── backend/
 │    └── psicologa/
 │
 ├── frontend/
 │    ├── assets/
 │    ├── pages/
 │    ├── index.html
 │    └── script.js
 │
 ├── README.md
 └── .gitignore
 
  Como rodar o projeto
  
  Backend
Acesse a pasta:
cd backend/psicologa
Configure as variáveis de ambiente:
DB_URL
DB_USER
DB_PASSWORD
TWILIO_SID
TWILIO_TOKEN
Execute o projeto:
mvn spring-boot:run

  Frontend
Acesse a pasta:
cd frontend
Abra o arquivo:
index.html

  Principais endpoints
Método	Endpoint	Descrição
GET	/agenda/dia	Retorna agenda do dia
GET	/agenda/semana	Retorna agenda semanal
GET	/agenda/disponivel	Horários disponíveis
POST	/pacientes	Cadastrar paciente
GET	/pacientes	Listar pacientes

  Observações
Este projeto utiliza dados fictícios para fins de estudo
Credenciais sensíveis não estão incluídas
Necessário configurar variáveis de ambiente
  Diferenciais do projeto
Arquitetura em camadas (Controller, Service, Repository)
Integração com API externa (Twilio)
Sistema completo (frontend + backend)
Calendário próprio para gerenciamento de agenda


  Autor:
Desenvolvido e criado integralmente por Iago Santos
