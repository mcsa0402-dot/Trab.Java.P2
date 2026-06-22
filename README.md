**🎟️ Sistema de Eventos Comunitários**

Trabalho de Programação Orientada a Objetos 2 (P2) — CEFET-RJ

Aplicação Java com persistência em MySQL, arquitetura em camadas e regras de negócio implementadas.


**📋 Índice**

- Sobre o Projeto

- Tecnologias Utilizadas

- Estrutura do Projeto

- Modelo de Dados

- Regras de Negócio

- Como Executar

- Dados de Teste


**Sobre o Projeto**

Sistema de gerenciamento de eventos comunitários que permite o cadastro de usuários, locais, eventos e inscrições. A aplicação foi desenvolvida em Java puro com acesso direto ao banco de dados MySQL via JDBC, seguindo o padrão de arquitetura em camadas (Model → DAO → Service).


**Tecnologias Utilizadas**

Java (sem frameworks)

MySQL — banco de dados relacional

JDBC — acesso ao banco via mysql-connector-java

SHA-256 — hash de senhas (via java.security.MessageDigest)


**Estrutura do Projeto**

<img width="671" height="441" alt="image" src="https://github.com/user-attachments/assets/0d81f6f5-6e2b-442d-a038-9f7faefc2f6d" />


                
**Modelo de Dados**

O banco de dados eventos_comunitarios possui as seguintes tabelas principais:

Tabela	Descrição

usuario	Usuários do sistema com papel (ORGANIZADOR/VOLUNTARIO/PUBLICO)

local_evento	Locais disponíveis para realização de eventos

evento	Eventos cadastrados, vinculados a um local e organizador

inscricao	Inscrições de usuários em eventos

recurso	Recursos disponíveis (projetores, cadeiras, etc.)

evento_recurso	Tabela associativa entre eventos e recursos


**Regras de Negócio**

O EventoService implementa três regras de negócio obrigatórias:

RN-1 — Verificação de Lotação

Ao tentar se inscrever em um evento lotado, o usuário é automaticamente adicionado à lista de espera (LISTA_ESPERA) em vez de receber uma confirmação.

RN-2 — Conflito de Horário/Local

Não é permitido cadastrar dois eventos no mesmo local com horários sobrepostos. A validação ocorre tanto na criação quanto na atualização de um evento.

RN-3 — Reserva Duplicada

Um usuário não pode se inscrever duas vezes no mesmo evento ativo. Qualquer tentativa duplicada lança uma exceção com mensagem explicativa.


**Como Executar**

Pré-requisitos

JDK 11 ou superior

MySQL 8.x rodando localmente

Driver JDBC mysql-connector-java no classpath

1. Criar o banco de dados

Execute o script SQL no seu MySQL:

source TrabP2JAVA/schema.sql;

Ou via linha de comando:

mysql -u root -p < TrabP2JAVA/schema.sql

2. Configurar a conexão
   
As credenciais do banco estão em DatabaseConnection.java. Altere se necessário:

private static final String URL      = "jdbc:mysql://localhost:3306/eventos_comunitarios?...";

private static final String USER     = "root";

private static final String PASSWORD = "root";

3. Compilar e executar
   
Adicione o mysql-connector-java.jar ao classpath e compile a partir de src/:

javac -cp .:mysql-connector-java.jar -d out $(find src -name "*.java")

java -cp out:mysql-connector-java.jar com.eventos.Main

Se a conexão for bem-sucedida, você verá:

Conexão com o banco estabelecida com sucesso!


**Dados de Teste**

O schema.sql já inclui dados iniciais para facilitar os testes:

Usuários (senha padrão para todos: senha123)

Nome	Email	Papel

Admin Sistema	admin@eventos.com	ORGANIZADOR

Maria Silva	maria@eventos.com	ORGANIZADOR

João Voluntário	joao@eventos.com	VOLUNTARIO

Ana Paula	ana@eventos.com	PUBLICO

Carlos Santos	carlos@eventos.com	PUBLICO

Fernanda Lima	fernanda@eventos.com	VOLUNTARIO

Locais pré-cadastrados: Centro Comunitário Norte, Praça da Cultura, Biblioteca Municipal, Escola Estadual Dom Pedro.

Eventos iniciais: Palestra sobre Sustentabilidade Urbana, Oficina de Artesanato Reciclado, Feira de Agricultura Familiar.
