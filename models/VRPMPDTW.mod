/*********************************************
 * Universidade Tecnológica Federal do Paraná
 * Trabalho de Conclusão de Curso
 * Engenharia de Computação
 *
 * Aluno: William Hitoshi Tsunoda Meira
 * Orientador: Prof. Dr. Leandro Magatão
 * Data de Criação: 20/08/2013
 *********************************************/
 
int LimiteTempo = ...;					//Limite de tempo para executar o modelo
float TempoExecucao = 0;				//Tempo de execução do modelo em segundos
int status = 0;							//Estado da solução do modelo

int 	NumeroNos = ...; 				//número de Nós [#]
range 	Nos = 1..NumeroNos;				//conjunto contendo todos os nós da rede
int 	NumeroVeiculos = ...; 			//número de veículos (rotas distintas) [#]
range 	Veiculos = 1..NumeroVeiculos;	//conjunto contendo todos os veículos da rede
int 	TempoViagem[Nos][Nos] = ...; 	//tempo de viagem entre cada um dos nós [min]
int 	TempoServico[Nos] = ...; 		//tempo de serviço [min]

float 	CapPesoMax[Veiculos]=...; 		//capacidade máxima do veículo - peso [kg]
float 	QPesoColeta[Nos]=...; 			//quantidade de mercadoria a coletar [kg]
float 	QPesoEntrega[Nos]=...; 			//quantidade de mercadoria a entregar - demanda [kg]

float 	CapVolMax[Veiculos]=...; 		//capacidade máxima do veículo - volume [m³]
float 	QVolColeta[Nos]=...; 			//quantidade de mercadoria a coletar [m³]
float 	QVolEntrega[Nos]=...; 			//quantidade de mercadoria a entregar - demanda [m³]

int 	MaximoJanelasTempo = ...; 		//número máximo de janelas [#]
range 	NumJT = 1..MaximoJanelasTempo;	//conjunto com todas as janelas possíveis dos clientes 
float 	InicioJT[Nos][NumJT] = ...;		//tempos de início de cada janela em cada nó [min]
float 	FimJT[Nos][NumJT] = ...;		//tempos de final  de cada janela em cada nó [min]

// Cálculo dos valores de 'M' para formulações Big-M [#]
float MQ = 3*(sum (i in Nos)  (QPesoColeta[i] + QPesoEntrega[i]));
float MV = 3*(sum (i in Nos) (QVolColeta[i] + QVolEntrega[i]));
float MJ = 3*(max (i in Nos, jt in NumJT) FimJT[i][jt]);

// Variáveis de Decisão
dvar boolean 	rota[Nos][Nos][Veiculos];			//binária que indica se a rota entre n e n' é percorrida pelo veículo.
dvar int+ 		tempoChegada[Nos][Veiculos];		//instante de tempo de chegada do veículo ao nó i.

dvar float+ 	quantPeso[Nos][Nos][Veiculos];		//carga presente no veículo v quando sai do nó n para o nó n2  [kg].
dvar float+ 	quantVolume[Nos][Nos][Veiculos];	//volume presente no veículo v quando sai do nó n para o nó n2 [m³].

dvar boolean 	emJanela[Nos][NumJT];				//binária para detectar em qual janela está a chegada no nó. 

dvar float+		relaxJanela[Nos][Veiculos];			//total de estouro de janela de tempo no decorrer das rotas.
dvar float+ 	relaxPeso [Nos][Nos][Veiculos];		//excesso de carga presente no veículo v quando sai do nó n para o nó n2  [kg].
dvar float+ 	relaxVolume[Nos][Nos][Veiculos];	//excesso de volume presente no veículo v quando sai do nó n para o nó n2  [m³].

dvar float+ objetivo;

//--------------------------------------------------------------------------
// Pré-processamento 
//--------------------------------------------------------------------------
float TempoInicial;
execute load_ExecTime{	
	var Tempo = new Date();
    TempoInicial=Tempo;
	
    cplex.tilim = LimiteTempo; 		//Definindo o limite de tempo para executar o modelo.
	cplex.MIPEmphasis = 1; 			//Definindo a ênfase do processo de busca (1: factibilidade).
};

// Função Objetivo
// Minimizar o tempo total que os veículos levam para realizar as suas rotas.
minimize
	objetivo + 
	10 * MJ * (sum(i,j in Nos, v in Veiculos) (relaxPeso[i][j][v]+relaxVolume[i][j][v])) + 
	100 * MJ * (sum(i,j in Nos, v in Veiculos) (relaxVolume[i][j][v])) + 
	MJ * (sum(i in Nos, v in Veiculos) relaxJanela[i][v]);	
	
subject to {  

objetivo == sum(v in Veiculos) tempoChegada[1][v];
	
// (1)	O veículo inicia viagem no nó 1, depósito. 
forall(v in Veiculos)
	sum(j in Nos) rota[1][j][v] == 1;

// (2)	O veículo termina a rota no nó 1, depósito.
forall(v in Veiculos)
	sum(i in Nos) rota[i][1][v] == 1;
	
// (3)	O veículo chega só uma vez a um cliente.
//	 	Todos os clientes devem ser atendidos.	
forall(j in Nos: j>1)
	sum(i in Nos: i!=j, v in Veiculos) rota[i][j][v] == 1;

// (4)	Se um veículo chega a um cliente tem de sair desse cliente para outro nó
// 		Garante que cada cliente j é visitado somente uma vez por um veículo v 
// 		proveniente da localização i , podendo i representar um outro cliente ou o depósito.
forall(j in Nos, v in Veiculos)
	(sum(i in Nos:i!=j)rota[i][j][v] - 
	sum(k in Nos:k!=j)rota[j][k][v] ) == 0;	
	
// (5)	Cálculo do tempo de chegada a cada nó
// 		Um serviço num nó só pode ser realizado depois do nó visitado imediatamente 
// 		antes ter sido servido e o veículo se ter deslocado até ao presente nó.
forall (v in Veiculos, i in Nos, j in Nos : j!=i && i==1)
	tempoChegada[j][v] >= (TempoServico[i] + TempoViagem[i][j]) - MJ*(1- rota[i][j][v]);

forall (v in Veiculos, i in Nos, j in Nos : j!=i && i>1)
	tempoChegada[j][v] >= (tempoChegada[i][v] + TempoServico[i] + 
					TempoViagem[i][j]) - MJ*(1-rota[i][j][v]);


// (6)	O tempo de chegada só deve assumir valor diferente de zero
// 		se existir rota ativa entre i e j de um veículo v.
forall (v in Veiculos, j in Nos)
		tempoChegada[j][v] <= MJ * sum(i in Nos : j!=i) rota[i][j][v];

// (7), (8) e (9)
//		O veículo deve chegar a um cliente dentro de uma janela temporal válida, se existir a rota.
forall(v in Veiculos, j in Nos, jt in NumJT)
	 tempoChegada[j][v] >= InicioJT[j][jt] - 
	 				(2 - emJanela[j][jt] - sum(i in Nos:j!=i)rota[i][j][v])*MJ - relaxJanela[j][v];

forall(v in Veiculos, j in Nos, jt in NumJT)
	 tempoChegada[j][v] <= FimJT[j][jt] +  relaxJanela[j][v] + (1-emJanela[j][jt])*MJ;

forall(j in Nos)
	 sum (jt in NumJT) emJanela[j][jt] >=1;

// (10) e (11) No momento que o veículo v sai do depósito para realizar as entregas,  
// 		transporta a carga a entregar a N fornecedores que constituem a rota associada 
// 		ao referido veículo. Esta carga não ultrapassa a capacidade do veículo.
forall(v in Veiculos)
	sum(j in Nos:j>1) quantPeso[1][j][v] == sum (i,j in Nos: i!=j) rota[i][j][v]*QPesoEntrega[j];

forall(i,j in Nos, v in Veiculos)
	quantPeso[i][j][v] <= rota[i][j][v]*CapPesoMax[v] + relaxPeso[i][j][v];

// (12)	Quando o veículo v retorna ao depósito, transporta a carga carregada nas localizações  
// 		do conjunto de fornecedores visitados. Esta carga não excede a capacidade do veículo em questão.
forall(v in Veiculos)
	sum(i in Nos:i>1) quantPeso[i][1][v] == sum (i,j in Nos:j!=i) rota[i][j][v]*QPesoColeta[j]
					 + (sum(j in Nos:j>1) quantPeso[1][j][v] - sum(i,j in Nos: j!=i) rota[i][j][v]*QPesoEntrega[j]);

//(13) Balanço de massa dos nós deve ser respeitado.
forall(i,j,k in Nos, v in Veiculos: (i!=j)&&(j!=k)&&(j>1) )//&&(k>1)
	quantPeso[j][k][v] <= quantPeso[i][j][v] - QPesoEntrega[j] + QPesoColeta[j] + (1 - rota[i][j][v])*MQ;


// (14) e (15) No momento que o veículo v sai do depósito para realizar as entregas,  
// 		transporta a carga a entregar a N fornecedores que constituem a rota associada 
// 		ao referido veículo. Esta carga não ultrapassa o volume máximo do veículo.
forall(v in Veiculos)
	sum(j in Nos:j>1) quantVolume[1][j][v] == sum(i,j in Nos:j!=i) rota[i][j][v]*QVolEntrega[j];

forall(i,j in Nos, v in Veiculos)
	quantVolume[i][j][v] <= rota[i][j][v]*CapVolMax[v] + relaxVolume[i][j][v];

// (16)	Quando o veículo v retorna ao depósito, transporta a carga carregada nas localizações  
// 		do conjunto de fornecedores visitados. Esta carga não excede o volume máximo do veículo em questão.
forall(v in Veiculos)
	sum(i in Nos:i>1) quantVolume[i][1][v] == sum(i,j in Nos:j!=i) rota[i][j][v]*QVolColeta[j] + 
					 (sum(j in Nos:j>1) quantVolume[1][j][v] - sum(i,j in Nos: j!=i) rota[i][j][v]*QVolEntrega[j]);
	
//	(17) Balanço de volume dos nós deve ser respeitado.
forall(i,j,k in Nos, v in Veiculos: (i!=j)&&(j!=k)&&(j>1))
	quantVolume[j][k][v] <= quantVolume[i][j][v] - QVolEntrega[j] + QVolColeta[j] + (1-rota[i][j][v])*MV;


//---------------------------------------------------------
// (xx) Inequação auxiliar para zerar os valores das variáveis  de rotas que estabelecem ciclos ao mesmo nó, 
//		a exceção do nó de origem, onde veículos não utilizados podem fazer rotas 'dummy'.	
forall(i in Nos, v in Veiculos: i>1)
	rota[i][i][v]==0;
	
} // Fim do Modelo


/*---------------------------------------------------*/
//Pós-processamento
/*---------------------------------------------------*/
execute{
   	status = cplex.getCplexStatus();
 	var TempoFinal = new Date();
        TempoExecucao = (TempoFinal - TempoInicial)/1000;
};