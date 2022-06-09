# PjeOffice PRO
Uma alternativa ao assinador [PjeOffice](http://www.pje.jus.br/wiki/index.php/PJeOffice). Esta **NÃO** é a implementação oficial do CNJ - trata-se de um exercício prático de programação de um assinador de documentos com as mesmas características daquele mantido pelo CNJ mas com funcionalidades adicionais de uso e segurança comumentes demandadas por usuários do Pje.

## Funcionalidades Adicionais

### Integração com Windows Explorer
Tarefas repetitivas precisam ser práticas para minimizar o estresse e dedicar tempo/energia em atividades mais relevantes. O PjeOffice PRO atalha algumas tarefas comuns dos usuários através do menu de contexto do Windows e dispensa alguns recursos de ferramentas externas para manuseio de arquivos do Pje, especialmente PDF's e vídeos (mp4). Neste contexto o PJeOffice PRO foi escrito para ganhar mais "funções de escritório" e otimizado para conseguir lidar com arquivos PDF's de tamanhos significativamente incomuns (da ordem de gigabytes) sem sujeitar (ou minimizar) os usuários a erros de falta de memória ou criação "descontrolada" de arquivos temporários em suas máquinas pois, ainda que existam ferramentas online para lidar com estes problemas, o uso delas pode se tornar improdutivo por exigir que sejam feitos upload's e posterior download's dos arquivos manuseados, além de eventualmente sujeitar o usuário a algum tipo de cotas para uso ou limitação de banda para envio/recebimento de documentos consideravelmente grandes.

### Tratamento de arquivos PDF

> Contexto
  ![Contexto PDF](https://github.com/l3onardo-oliv3ira/signer4j-pje/blob/main/screen/context-pdf.png)

#### Assinar e salvar na mesma pasta

A forma mais rápida e simples de assinar um documento,  _assinar e salvar na mesma pasta_  minimiza a quantidade de cliques para acesso ao documento assinado.

<video autoplay loop muted markdown="0">
    <source src=".\screen\assinar-e-salvar-na-mesma-pasta.mp4" type="video/mp4" markdown="1">
</video>


#### Assinar e salvar em nova pasta

A forma mais rápida e simples de assinar uma **coleção de documentos**,  _assinar e salvar em nova pasta_  minimiza a quantidade de cliques e agrupa a coleção de documentos assinados em uma nova pasta nomeada com o dia e horário da assinatura. Isto evita dificuldades em encontrar os documentos assinados em meios a uma quantidade significativa de outros documentos. 

<video autoplay loop muted markdown="0">
    <source src=".\screen\assinar-e-salvar-na-em_nova-pasta.mp4" type="video/mp4" markdown="1">
</video>

#### Assinar e salvar em pasta específica

Se você já sabe previamente onde a coleção de documentos assinados precisarão ser armazenados, esta é a sua melhor opção para manter-se organizado. Esta função permite que você informe explicitamente o local onde os arquivos assinados serão gravados.

<video autoplay loop muted markdown="0">
    <source src=".\screen\assinar-e-salvar-em-pasta-especifica.mp4" type="video/mp4" markdown="1">
</video>

#### Gerar 1 PDF a cada 'n' MB

Você já deve saber que o tamanho dos arquivos a serem enviados ao Pje estão sujeitos a um limite superior. O propósito desta função é dividir o(s) arquivo(s) PDF's em uma coleção de outros arquivos de modo que individualmente cada um tenha seu tamanho máximo mais próximo de 'n' MB (você informa explicitamente o valor de 'n'). No exemplo abaixo um arquivo de aproximadamente 29MB ( _Processo.pdf_ ) foi dividido em outros 3 volumes com tamanhos não superiores a 15MB. 

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-1-pdf-a-cada-n-mb.mp4" type="video/mp4" markdown="1">
</video>

Os arquivos de volumes finais marcam em seus nomes a página do arquivo original onde iniciou a divisão que permitiu limitar seu tamanho a 'n'MB (no exemplo, páginas 1, 265 e 644).

**Vale destacar que este recurso NÃO se confunde com funções de compactação/otimização de PDF's comumente encontradas em ferramentas especializadas para este fim**. 

O PjeOffice PRO faz uso apenas da compactação padrão de PDF's juntamente com uma estimativa da contribuição da página no tamanho total do arquivo para calcular quais páginas marcam a divisão do arquivo original de modo e submeter o tamanho dos volumes ao limite superior desejado ('n'MB). Em virtude da diversidade de formatações, versões, fontes, imagens, etc que podem ser incorporados em arquivos PDF's, **podem ocorrer exceções** nas quais a tolerância desejada não seja alcançada embora o PjeOffice PRO tenha feito o seu melhor esforço pra assegurar que o tamanho informado não seja ultrapassado. Neste caso específico, ainda que ultrapasse o valor informado, a solução deixará o tamanho dos volumes o mais próximo possível do desejado. Alternativamente você pode tentar diminuir explicitamente o valor de 'n' de modo a possibilitar ao PjeOffice PRO o reequilibrio dos pesos das páginas ao resubmeter os volumes a um novo limite superior.


#### Gerar 1 PDF a cada 10MB (Pje)

Opera como a função anterior porém predefine 'n' igual a 10MB, minimizando a quantidade de cliques para o caso comum.


#### Gerar 1 PDF a cada 'n' páginas

Divide o(s) arquivo(s) PDF's em uma coleção de outros arquivos (volumes) de modo que individualmente cada um tenha seu total de páginas igual a 'n' (você informa explicitamente o valor de 'n'). No exemplo abaixo arquivos são dividido em outros 3 volumes com 5 páginas cada. Os arquivos de volumes finais marcam em seus nomes a página do arquivo original onde iniciou a divisão que permitiu limitar seu volume a 'n' páginas (no exemplo, páginas 1, 6 e 11).

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-1-pdf-a-cada-n-paginas.mp4" type="video/mp4" markdown="1">
</video>

#### Gerar 1 PDF por página

Opera como a função anterior porém predefine 'n' igual a 1, minimizando a quantidade de cliques para o caso unitário.

#### Gerar 1 PDF com as páginas ÍMPARES/PARES

Há cenários em que um volume considerável de folhas são digitalizada mas a configuração do scanner conduziu à digitalização das folhas em modo frente e verso. No caso infeliz do volume ter sido composto por folhas com informações em apenas um dos lados, o PDF gerado pela digitalização possuirá 50% das páginas com conteúdo vazio (páginas em branco). Para evitar uma nova digitalização ou reconfiguração do scanner, esta função permite que a partir do documento digitalizado seja gerado rapidamente um novo documento formado apenas pelas páginas ímpares/pares, conforme o caso.

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-1-pdf-com-as-paginas-impares.mp4" type="video/mp4" markdown="1">
</video>


#### Gerar PDF's com páginas específicas

Se o seu desejo é dividir um arquivo PDF em outros arquivos individuais, esta função é sua melhor alternativa. Utilizando um texto descritivo em formato comum de impressão, esta função permite desmembrar o arquivo em partes do seu interesse. 

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-pdfs-com-paginas-especificas.mp4" type="video/mp4" markdown="1">
</video>

No exemplo o arquivo  _Processo.pdf_  composto por 648 páginas é dividido em 3 volumes (separados por ponto e vírgula): O primeiro com apenas a primeira página, o segundo com as páginas 10ª até a 20ª e o terceiro com as páginas 200ª até a última página (* é um sinônimo para  _última página_  ). Os arquivos de volumes finais marcam em seus nomes a página do arquivo original onde iniciou a divisão limitadas às páginas do seu desejo.

#### Unir PDF's selecionados

A forma mais rápida de simples de unir dois ou mais documentos PDF's em um único arquivo. A função  _unir PDF's selecionados_  permite definir a ordem de mesclagem dos documentos em um arquivo final. No exemplo que segue, partes de um livro são unidos de modo a gerar um único documento (  _LIVRO COMPLETO.pdf_  ).


<video autoplay loop muted markdown="0">
    <source src=".\screen\unir-pdfs-selecionados.mp4" type="video/mp4" markdown="1">
</video>


### Tratamento de arquivos de vídeo (MP4)

> Contexto
  ![Contexto MP4](https://github.com/l3onardo-oliv3ira/signer4j-pje/blob/main/screen/context-mp4.png)


#### Gerar 1 vídeo a cada 'n' MB

Você já deve saber que o tamanho dos arquivos a serem enviados ao Pje estão sujeitos a um limite superior. O propósito desta função é dividir o(s) arquivo(s) de vídeo MP4 em uma coleção de outros arquivos de modo que individualmente cada um tenha seu tamanho máximo mais próximo de 'n' MB (você informa explicitamente o valor de 'n'). No exemplo abaixo um arquivo de aproximadamente 1GB ( _Audiência.mp4_ ) é dividido em vários volumes com tamanhos não superiores a 100MB. 

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-1-video-a-cada-n-mb.mp4" type="video/mp4" markdown="1">
</video>

Note que os arquivos de vídeo finais marcam em seus nomes os intervalos de tempo (relativo ao arquivo original) que iniciou a divisão e que permitiram limitar seus tamanhos a 'n'MB. Importante destacar que os arquivos gerados são mantidos com a mesma qualidade do arquivo original, ou seja, esta função NÃO deve ser entendida como formas de otimização e/ou compressão de vídeos.

#### Gerar 1 vídeo a cada 90MB (Pje)

Opera como a função anterior porém prédefine 'n' igual a 90, minimizando a quantidade de cliques para o caso comum.

#### Gerar 1 vídeo a cada 'n' minutos

A forma mais rápida de simples de dividir um vídeo em fatias fixas de tempo. No exemplo, um vídeo com mais de 2 horas é dividido em vídeos menores com duração não maior que 30 minutos:

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-1-video-a-cada-n-minutos.mp4" type="video/mp4" markdown="1">
</video>

Note que os arquivos de vídeo finais marcam em seus nomes os intervalos de tempo (relativo ao arquivo original) que iniciou a divisão e que permitiram limitar suas durações a 'n' minutos. Importante destacar que os arquivos gerados são mantidos com a mesma qualidade do arquivo original, ou seja, esta função NÃO deve ser entendida como formas de otimização e/ou compressão de vídeos.

#### Gerar cortes específicos

Se o seu desejo é fazer recortes personalizados, esta função lhe permite informar os trechos do vídeo que serão do seu interesse. No exemplo que segue, dois cortes são realizados: um marcando o início da audiência e outro a sentença.

<video autoplay loop muted markdown="0">
    <source src=".\screen\gerar-cortes-específicos.mp4" type="video/mp4" markdown="1">
</video>


#### Converter para WEBM

Em desenvolvimento...

#### Extrair audio OGG

Em desenvolvimento...




