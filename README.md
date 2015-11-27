# Maykot Tracker (version 1.3)

Desenvolvedor: Rodrigo Mateus (RA 515078)

Projeto para disciplina Implementação de Sistemas de Informação II - 4º ano BSI Univem  

O Maykot Tracker é um aplicativo móvel para Android. Foi desenvolvido com o objetivo de testar os recursos da biblioteca RadioLibrary.  Suas principais funcionalidades são:

- Registrar coordenadas GPS e envia-las em uma requisição HTTP POST.
- Registrar imagem (foto) e envia-las em uma requisição HTTP POST.
- Executar uma requisição HTTP GET.

A biblioteca RadioLibrary é uma interface entre um aplicativo móvel e um servidor MQTT. Suas principais funcionalidades são:

- Publicar e Assinar tópicos de mensagens em servidor MQTT.
- Publicar dados gerados pelo aplicativo móvel para um servidor MQTT.
- Receber mensagem MQTT e encaminhar os dados para o aplicativo móvel.

## Nota de Liberação Maykot Tracker e biblioteca RadioLibrary versão 1.3

### Introdução
Este documento provê uma visão geral da versão do aplicativo Maykot Tracker e da biblioteca RadioLibrary que está sendo liberada. Aqui descreveremos suas funcionalidades, bem como seus problemas e limitações conhecidos. Por último são descritas as demandas e os problemas que foram resolvidos para liberação da versão atual.

### 1. Nota de release a ser publicado
#### Maykot Tracker
- Monitor de conexão com servidor MQTT.
- Comando para reiniciar o aplicativo [Router] (https://github.com/RodrigoMateus/MQTT_to_DigiMesh_Router)
- Comando para reiniciar o rádio-modem.

#### RadioLibrary
- Buffer circular de requisições.
- Timeout para requisições não respondidas.
- Envio de comandos SSH para o MiniPC.
- Envio de comandos Java para o aplicativo [Router] (https://github.com/RodrigoMateus/MQTT_to_DigiMesh_Router)

### 2. Problemas conhecidos e limitações
#### Limitação
- O aplicativo não verifica possível travamento do radio-modem.

### 3. Datas Importantes
Segue abaixo as datas importantes do desenvolvimento:

Data  | Evento
------------- | -------------
20/09/15  | Início do desenvolvimento
15/10/15  | Primeira versão para teste a campo
06/11/15  | Segunda versão para teste a campo
15/11/15  | Separação da biblioteca RadioLibrary em modulo independende.
20/11/15  | Terceira versão para teste a campo

### 4. Compatibilidade
Segue abaixo os requisitos:

Requisitos  | Ferramentas
------------- | -------------
Sistema operacional  | Android 4.4 (KitKat) ou superior

#### Tecnologias
- Android
- MQTT
- HTTP
