# meutudo-avaliacao

# Bibliotecas para DEV
- _Java 11 OpenJdk_
- _Lombok_ instalar em sua IDE
- _Maven Project_
# Dependências Utilizadas
  - _Spring Boot DevTools_ para LiveReload
  - _Lombok_ para codificações mais CLEANS
  - _FlyWay_ para migrations. Versionamento do "banco"
  - _H2Database_ para desacoplar a necessidade de um banco local
  - _junit_ para implementações de TESTES

# Desafio
O projeto consiste em desenvolver uma aplicação semelhante a um sistema bancário. O projeto deve permitir as seguintes operações:

	- Consulta de saldo;
	- Transferência entre contas;
	- Reverter uma transferência;
	- Programar uma transferência futura parcelada

# CD - ContinuosDelivery com Heroku
  - https://simulation-bank.herokuapp.com/swagger-ui/index.html (link de acesso)
# Testes implementados
    
*Transferência*
  - Não Deve Realizar Transferencia Com Saldo Insuficiente
  - Nao Deve Realizar Transferencia Com Valor Zero Ou Menor
  - Nao Deve Realizar Transferencia Quando Origem Nao Encontrada
  - Nao Deve Realizar Transferencia Quando Destino Nao Encontrado
  - Deve Realizar Transferencia entre duas contas

*Reverter transferência*
  - Não Deve Reverter uma tranferência realizada quando não encontrada
  - Não Deve Reverter uma tranferência realizada quando ja tiver sido revertida
  - Não Deve Reverter uma tranferência realizada quando não houver SALDO
  - Deve Reverter uma tranferência realizada

*Transferência futura*
  - Não Deve realizar transferência futura quando quantidade for menor ou igual a zero
  - Não Deve realizar transferência futura com valor menor que um
  - Não Deve realizar transferência futura quando origem não encontrada
  - Não Deve realizar transferência futura quando destino não encontrada
  - Não Deve realizar transferência futura quando não partir do dia seguinte
  - Deve realizar transferência futura quando resultado da divisao não gerar dizma periodica
  - Deve realizar transferência futura quando resultado da divisao gerar dizma periodica colocando a diferença na ultima parcela

*Concorrência*
  - Programar 2 transferências simultâneas com threads para simular concorrência e validar o uso do LockOtimista no fluxo da transferência

