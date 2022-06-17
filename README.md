# PjeOffice PRO
Uma alternativa ao assinador [PjeOffice](http://www.pje.jus.br/wiki/index.php/PJeOffice). Esta **NÃO** é a implementação oficial do CNJ - trata-se de um exercício prático de programação de um assinador de documentos com as mesmas características e recursos daquele mantido pelo CNJ mas enriquecido com funcionalidades de uso e segurança comumentes demandadas por usuários do Pje.

## Funcionalidades Adicionais

### Integração com Windows Explorer
Tarefas repetitivas precisam ser práticas para minimizar o estresse e dedicar tempo/energia em atividades mais relevantes. O PjeOffice PRO atalha algumas tarefas comuns dos usuários através do menu de contexto do Windows e dispensa alguns recursos de ferramentas externas para manuseio de arquivos do Pje, especialmente PDF's e vídeos (mp4). Neste cenário o PJeOffice PRO foi escrito para ganhar mais "funções de escritório" e otimizado para conseguir lidar com arquivos PDF's de tamanhos significativamente incomuns (da ordem de gigabytes) sem sujeitar (ou minimizar) aos usuários erros de falta de memória ou criação "descontrolada" de arquivos temporários em seu ambiente pois, ainda que existam ferramentas online para lidar com estes problemas, o uso delas pode se tornar improdutivo por exigir que sejam feitos upload's e download's dos arquivos manuseados, além de eventualmente impor ao usuário algum tipo de cotas para uso ou limitações de banda para envio/recebimento de documentos consideravelmente grandes.

### Tratamento de arquivos PDF

> Contexto
  ![Contexto PDF](https://github.com/l3onardo-oliv3ira/signer4j-pje/blob/main/screen/context-pdf.png)

### Tratamento de arquivos MP4

> Contexto
  ![Contexto MP4](https://github.com/l3onardo-oliv3ira/signer4j-pje/blob/main/screen/context-mp4.png)


### Assinar e salvar na mesma pasta

A forma mais rápida e simples de assinar um documento,  _assinar e salvar na mesma pasta_  minimiza a quantidade de cliques para acesso ao documento assinado.

<details>
<summary>Ver demonstração</summary>
  
https://user-images.githubusercontent.com/12123680/172888091-fbc38b50-117d-441c-b929-0bed8e5f314f.mp4

</details>



### Assinar e salvar em nova pasta

A forma mais rápida e simples de assinar uma **coleção de documentos**,  _assinar e salvar em nova pasta_  minimiza a quantidade de cliques e agrupa a coleção de documentos assinados em uma nova pasta nomeada com o dia e horário da assinatura. Isto evita dificuldades em encontrar os documentos assinados em meios a uma quantidade significativa de outros documentos. 

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888241-d154295d-f359-4647-af5e-a9ceef8f4d89.mp4

</details>

### Assinar e salvar em pasta específica

Se você já sabe previamente onde a coleção de documentos assinados precisarão ser armazenados, esta é a sua melhor opção para manter-se organizado. Esta função permite que você informe explicitamente o local onde os arquivos assinados serão gravados.

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888448-b1ea453b-62f8-46a7-a46f-19566506063b.mp4

</details>

### Gerar 1 PDF a cada 'n' MB

Você já deve saber que o tamanho dos arquivos a serem enviados ao Pje estão sujeitos a um limite superior. O propósito desta função é dividir o(s) arquivo(s) PDF's em uma coleção de outros arquivos de modo que individualmente cada um tenha seu tamanho máximo mais próximo de 'n' MB (você informa explicitamente o valor de 'n'). Na demonstração abaixo um arquivo de aproximadamente 29MB ( _Processo.pdf_ ) foi dividido em outros 3 volumes com tamanhos não superiores a 15MB. 

<details>
<summary>Ver demonstração</summary>
  
https://user-images.githubusercontent.com/12123680/172888514-72c97fdf-fde9-4c73-9a76-33999bdd8b05.mp4

</details>

> **Note**
> Perceba que os arquivos de volumes finais marcam em seus nomes a página do arquivo original onde iniciou a divisão que permitiu limitar seu tamanho a 'n'MB (na demonstração, páginas 1, 265 e 644).

> **Warning**
> Vale destacar que este recurso NÃO se confunde com funções de compactação/otimização de PDF's comumente encontradas em ferramentas especializadas para este fim[^1].

[^1]: O PjeOffice PRO faz uso apenas da compactação padrão de PDF's juntamente com uma estimativa da contribuição da página no tamanho total do arquivo para calcular quais páginas marcam a divisão do arquivo original de modo e submeter o tamanho dos volumes ao limite superior desejado ('n'MB). Em virtude da diversidade de formatações, versões, fontes, imagens, etc que podem ser incorporados em arquivos PDF's, **podem ocorrer exceções** das quais a tolerância desejada não seja alcançada embora o PjeOffice PRO tenha feito o seu melhor esforço pra assegurar que o tamanho informado não seja ultrapassado. Neste caso específico, ainda que ultrapasse o valor informado, a solução deixará o tamanho dos volumes o mais próximo possível do desejado. Alternativamente você pode tentar diminuir explicitamente o valor de 'n' de modo a possibilitar ao PjeOffice PRO o reequilibrio dos pesos das páginas ao resubmeter os volumes a um novo limite superior.


### Gerar 1 PDF a cada 10MB (Pje)

Opera como a função anterior porém predefine 'n' igual a 10MB, minimizando a quantidade de cliques para o caso comum.


### Gerar 1 PDF a cada 'n' páginas

Divide o(s) arquivo(s) PDF's em uma coleção de outros arquivos (volumes) de modo que individualmente cada um tenha seu total de páginas igual a 'n' (você informa explicitamente o valor de 'n'). Na demonstração abaixo arquivos são divididos em outros 3 volumes com 5 páginas cada. 
<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888596-8254523b-2db7-48e9-a07c-fdd65a53e6bf.mp4

</details>

>**Note**
>Perceba que os arquivos de volumes finais marcam em seus nomes a página do arquivo original onde iniciou a divisão que permitiu limitar seu volume a 'n' páginas (na demonstração, páginas 1, 6 e 11).

### Gerar 1 PDF por página

Opera como a função anterior porém predefine 'n' igual a 1, minimizando a quantidade de cliques para o caso unitário.

### Gerar 1 PDF com as páginas ÍMPARES/PARES

Há cenários em que um volume considerável de folhas são digitalizadas mas a configuração do scanner conduziu à digitalização das folhas em modo frente e verso. No caso infeliz do volume ter sido composto por folhas com informações em apenas um dos lados, o PDF gerado pela digitalização possuirá 50% das páginas com conteúdo vazio (páginas em branco). Para evitar uma nova digitalização ou reconfiguração do scanner, esta função permite que a partir do documento digitalizado seja gerado rapidamente um novo documento formado apenas pelas páginas ímpares/pares, conforme o caso.

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888694-fc8bd75c-db7c-4b7b-b215-5b36d44e57e0.mp4

</details>  
  
### Gerar PDF's com páginas específicas

Se o seu desejo é dividir um arquivo PDF em outros arquivos individuais, esta função é sua melhor alternativa. Utilizando um texto descritivo em formato comum de impressão, esta função permite desmembrar o arquivo em partes do seu interesse. 

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888746-9532802f-e517-41dd-93c9-1faab9578013.mp4
  
</details>

Na demonstração o arquivo  _Processo.pdf_  composto por 648 páginas é dividido em 3 volumes (separados por ponto e vírgula): O primeiro com apenas a primeira página, o segundo com as páginas 10ª até a 20ª e o terceiro com as páginas 200ª até a última página (* é um sinônimo para  _última página_  ). 

>**Note**
>Perceba que os arquivos de volumes finais marcam em seus nomes a página do arquivo original onde iniciou a divisão limitadas às páginas do seu desejo.

### Unir PDF's selecionados

A forma mais rápida e simples de unir dois ou mais documentos PDF's em um único arquivo. A função  _unir PDF's selecionados_  permite definir a ordem de mesclagem dos documentos em um arquivo final. Na demonstração que segue, partes de um livro são unidos de modo a gerar um único documento (  _LIVRO COMPLETO.pdf_  ).

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888794-1b334e23-400e-4383-a7b2-4e343584006a.mp4

</details>

### Gerar 1 vídeo a cada 'n' MB

Você já deve saber que o tamanho dos arquivos a serem enviados ao Pje estão sujeitos a um limite superior. O propósito desta função é dividir o(s) arquivo(s) de vídeo MP4 em uma coleção de outros arquivos de modo que individualmente cada um tenha seu tamanho máximo mais próximo de 'n' MB (você informa explicitamente o valor de 'n'). Na demonstração abaixo um arquivo de aproximadamente 1GB ( _Audiência.mp4_ ) é dividido em vários volumes com tamanhos não superiores a 100MB. 

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888847-5b5472e4-8fdb-4186-b7a8-7ba06e776f15.mp4

</details>

> **Note**
> Perceba que os arquivos de vídeo finais marcam em seus nomes os intervalos de tempo (relativo ao arquivo original) que iniciou a divisão e que permitiram limitar seus tamanhos a 'n'MB. 

> **Warning**
> Vale destacar que os arquivos gerados são mantidos com a mesma qualidade do arquivo original, ou seja, esta função NÃO deve ser entendida como formas de otimização e/ou compressão de vídeos.

### Gerar 1 vídeo a cada 90MB (Pje)

Opera como a função anterior porém predefine 'n' igual a 90, minimizando a quantidade de cliques para o caso comum.

### Gerar 1 vídeo a cada 'n' minutos

A forma mais rápida e simples de dividir um vídeo em fatias fixas de tempo. Na demonstração, um vídeo com mais de 2 horas é dividido em vídeos menores com duração não maior que 30 minutos:

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888906-4da490c1-472b-478b-8bf2-0aec5ebf533d.mp4
  
</details>

>**Note**
>Perceba que os arquivos de vídeo finais marcam em seus nomes os intervalos de tempo (relativo ao arquivo original) que iniciou a divisão e que permitiram limitar suas durações a 'n' minutos. 

> **Warning**
>Importante destacar que os arquivos gerados são mantidos com a mesma qualidade do arquivo original, ou seja, esta função NÃO deve ser entendida como formas de otimização e/ou compressão de vídeos.

### Gerar cortes específicos

Se o seu desejo é criar recortes personalizados, esta função lhe permite informar os trechos do vídeo que serão do seu interesse. Na demonstração que segue, dois cortes são realizados: um marcando o início da audiência e outro a sentença.

<details>
<summary>Ver demonstração</summary>

https://user-images.githubusercontent.com/12123680/172888950-2b49d2d7-f609-472c-ad6c-c5e301e8f829.mp4
  
</details>

### Extrair audio

Se o seu desejo é obter apenas o audio de determinado vídeo, esta função lhe permite extraí-lo para o format MP3 ou OGG.

<details>
<summary>Ver demonstração</summary>

Em breve...
  
</details>

### Converter para WEBM

Algumas aplicações do judiciário fazem uso do formato de vídeo WEBM (por exemplo, SEEU - Sistema Eletrônico de Execução Unificado). O propósito desta função é oferecer um meio rápido e prático para converter vídeos MP4 para WEBM.

<details>
<summary>Ver demonstração</summary>

Em breve...
  
</details>


> **Warning**
> Esta função **PODE** diminuir a qualidade do vídeo e caberá ao próprio usuário aplicar seu critério aceitando ou não o resultado apresentado.

> **Note**
> Esta função **NÃO** altera o arquivo original. 


### Otimizar tamanho

Você já deve saber que o tamanho dos arquivos a serem enviados ao Pje estão sujeitos a um limite superior. O propósito desta função é otimizar o(s) arquivo(s) de vídeo MP4 no sentido de diminuir o seu tamanho ao mesmo temo que tenta deixar seu conteúdo "visualmente aceitável". 

Na demonstração abaixo um arquivo de aproximadamente 204MB ( _Audiência.mp4_ ) é otimizado gerando outro arquivo final "visualmente aceitável" e com o tamanho 10 vezes menor (20MB). 

<details>
<summary>Ver demonstração</summary>

Em breve...

</details>

> **Warning**
> Esta função atua alterando alguns parâmetros de renderização do vídeo em um esforço de equilibrar qualidade vs tamanho, ou seja, diferentemente das funções anteriores **haverá perda de qualidade do vídeo** e caberá ao próprio usuário aplicar seu critério aceitando ou não o resultado apresentado. 

> **Note**
> Esta função **NÃO** altera o arquivo original. 


## Funcionalidades de Segurança

Assinar documentos no PJe pode ser uma operação repetitiva. Nesta versão PRO o usuário também tem a oportunidade de decidir entre três estratégias de informação de senha conforme figura que segue:

> Segurança
  ![Opções](https://github.com/l3onardo-oliv3ira/signer4j-pje/blob/main/screen/security.png)


> **Note**
A estratégia de solicitação de senha pode ser alterada facilmente e a qualquer momento de forma que o recurso será aplicado 
imediatamente sem que para isso seja exigido o reinício do assinador.

### Sempre solicitar senha

É a alternativa mais segura, porém a menos produtiva. Segura porque qualquer operação que envolva o uso da chave privada do
certificado só prosseguirá com a digitação explicita da senha no teclado. Menos produtiva porque em dias em que houver um 
volume considerável de documentos a serem assinados, a senha será informada para cada documento (nos casos de assinatura 
em lote, a senha será informada a cada lote, ou seja, em dois lotes de 20 documentos, a senha será solicitada 2 (duas) 
vezes ao invés de 2*20=40 vezes).

### Solicitar senha uma vez

É a alternativa menos segura, porém a mais produtiva. Mais produtiva porque não haverá gasto de tempo com a digitação de 
senhas no teclado e menos segura porque o acesso físico à máquina por terceiros de má fé possibilitará a este realizar a 
assinatura mesmo desconhecendo a senha do legítimo usuário. Neste caso caberá ao dono do certificado ficar atento quanto ao
uso indevido do seu dispositivo por pessoas que fizerem uso do seu computador previamente autenticado em sua conta.

### Apenas confirmar o uso do dispositivo

É a alternativa intermediária entre produtividade e segurança. O comportamento do assinador será o mesmo que a
opção  _“Sempre solicitar senha”_ , porém ao invés da senha ser digitada explicitamente no teclado, o usuário apenas 
confirmará com um único clique o aviso dado pelo assinador informando que o dispositivo está sendo utilizado conforme tela
que segue:

> Segurança
  ![Confirmação](https://github.com/l3onardo-oliv3ira/signer4j-pje/blob/main/screen/confirm-use.png)

Como na opção anterior, cabe ao usuário também ficar atento quanto ao uso indevido do seu dispositivo em ausências temporárias
do seu equipamento.


### Ciclos de autenticação

Como medidas adicionais de segurança o PjeOffice PRO adota os chamados ciclos de autenticação. Um ciclo de autenticação é uma sequência de eventos que provocam o fechamento ou abertura de uma sessão de autenticação no token do usuário tal que:

- Na entrada do sistema Pje, toda tentativa de autenticação exigirá a informação da senha, **mesmo que**  a opção  _"Solicitar apenas uma vez"_  esteja habilitada e já tenha sido informada anteriormente. A praticidade de informação única da senha **só se aplicará após a entrada já previamente autenticada com a senha exigida**, ou seja, uma tentativa de autenticação na plataforma Pje marca o fim de um ciclo existente e início de um novo ciclo no qual as funcionalidades de segurança estarão sujeitas.


- Um logoff ou bloqueio explícito da máquina pelo usuário ou por ociosidade da estação (política de rede) colocará o assinador em modo de 
hibernação, finalizando a sessão do token e marcando o fim do ciclo de autenticação atual (se houver). O assinador vai retomar o seu trabalho 
quando do desbloqueio / login / fim da ociosidade. O propósito deste mecanismo é dificultar a exploração de eventual falha
de segurança **minimizando o tempo de exposição da sessão aberta do token do usuário a artefatos maliciosos**, além da possibilidade de integração
com políticas de segurança da rede aplicadas ao logoff automático de estações ou similares.


