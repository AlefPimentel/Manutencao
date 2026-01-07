Desenvolvi e finalizei um sistema interno que já está instalado e em fase de implantação na empresa onde trabalho.
O sistema foi criado para resolver problemas reais do dia a dia industrial, principalmente:
Controle de estoque
Registro de manutenções preventivas
Centralização de informações técnicas dos maquinários
A proposta foi eliminar controles manuais e informações dispersas, criando um histórico confiável, rastreável e organizado.
O desenvolvimento seguiu uma estrutura sólida, com padrão MVC, uso de DAOs e foco em organização, manutenibilidade e usabilidade.

Não atuo como programador na empresa — o sistema surgiu de uma necessidade real do ambiente de trabalho e foi desenvolvido para uso prático.


Tecnologias Utilizadas


Linguagem: Java 21 (LTS)
Interface Gráfica: JavaFX (FXML)
Estilização: CSS customizado
Banco de Dados: SQLite (embedded via JDBC)
Gerenciamento de Dependências: Maven
Distribuição: Empacotamento nativo Windows (.exe) via jpackage
Arquitetura e Padrões
MVC (Model-View-Controller)
DAO (Data Access Object)
Service Layer para centralização das regras de negócio
Singleton e Factory aplicados de forma pontual
Funcionalidades Principais
📦 Estoque
Organização hierárquica por partições
Histórico completo de entradas e saídas
Suporte a imagens e identificação visual de itens
🔧 Manutenção
Monitoramento de vida útil baseado em horímetro
Alertas preditivos por status (🟢 🟡 🔴)
Controller genérico para múltiplos modelos de equipamentos
Ficha técnica dinâmica por máquina