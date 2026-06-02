# High Performance Report Generator

Geração de relatórios CSV de grande volume utilizando JDBC Streaming, ResultSet Forward Only e processamento incremental para reduzir consumo de memória e melhorar performance.

## Problema

Sistemas corporativos frequentemente precisam exportar centenas de milhares ou milhões de registros para relatórios.

Uma implementação tradicional costuma carregar todos os registros em memória antes de gerar o arquivo:

```java
List<ItemDTO> items = repository.findAllItems(...);
```

Essa abordagem funciona para pequenos volumes, mas pode causar:

* Alto consumo de memória
* Garbage Collection excessivo
* Risco de OutOfMemoryError
* Maior tempo de resposta

## Solução

Este projeto utiliza JDBC Streaming para processar registros de forma incremental.

Fluxo:

```text
PostgreSQL
    ↓
ResultSet Streaming
    ↓
Processamento linha a linha
    ↓
BufferedWriter
    ↓
CSV
```

Nenhuma lista contendo todos os registros é mantida em memória.

## Tecnologias

* Java 21
* Spring Boot 3
* PostgreSQL
* JDBC
* Docker
* Maven

## Estratégias Comparadas

### 1. Abordagem Tradicional

```java
List<ItemDTO> items = repository.findAllItems(...);
```

Fluxo:

```text
Banco
 ↓
Carrega tudo na memória
 ↓
Gera CSV
```

### 2. JDBC Streaming

```java
PreparedStatement
ResultSet.TYPE_FORWARD_ONLY
setFetchSize(1000)
```

Fluxo:

```text
Banco
 ↓
Lê 1000 registros por vez
 ↓
Escreve diretamente no CSV
 ↓
Descarta objetos já processados
```

## Otimizações Utilizadas

### Streaming JDBC

```java
connection.prepareStatement(
    sql,
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_READ_ONLY
);
```

### Fetch Size

```java
ps.setFetchSize(1000);
```

### Escrita Incremental

```java
BufferedWriter
```

### Download Streaming

```java
response.getOutputStream()
```

### Índices

```sql
CREATE INDEX idx_items_created_at
ON items(created_at);

CREATE INDEX idx_items_created_at_name
ON items(created_at, name);

CREATE INDEX idx_items_supplier_id
ON items(supplier_id);
```

## Benchmark

Dataset utilizado:

```text
500.000 registros
```

### Abordagem Tradicional

Logs:

```text
Memory before query: 20 MB
Memory after query: 683 MB

Traditional report finished

Records: 500000
Time: 5494 ms
Memory: 776 MB
```

Resultado:

| Métrica                 | Valor    |
| ----------------------- | -------- |
| Registros               | 500.000  |
| Tempo                   | 5.494 ms |
| Memória Final           | 776 MB   |
| Carrega tudo em memória | Sim      |

---

### JDBC Streaming

Logs:

```text
Processed: 475000 | Memory: 63 MB
Processed: 480000 | Memory: 30 MB
Processed: 485000 | Memory: 43 MB
Processed: 490000 | Memory: 55 MB
Processed: 495000 | Memory: 23 MB
Processed: 500000 | Memory: 35 MB

Streaming report finished

Records: 500000
Time: 3611 ms
Memory: 35 MB
```

Resultado:

| Métrica                 | Valor    |
| ----------------------- | -------- |
| Registros               | 500.000  |
| Tempo                   | 3.611 ms |
| Memória Final           | 35 MB    |
| Carrega tudo em memória | Não      |

## Comparativo

| Métrica        | Tradicional | Streaming |
| -------------- | ----------- | --------- |
| Registros      | 500.000     | 500.000   |
| Tempo          | 5.494 ms    | 3.611 ms  |
| Memória        | 776 MB      | 35 MB     |
| Escalabilidade | Limitada    | Alta      |
| Risco de OOM   | Alto        | Baixo     |

## Resultados

Comparado à abordagem tradicional:

* Aproximadamente 95% menos memória utilizada
* Aproximadamente 34% mais rápido
* Consumo de memória praticamente constante
* Melhor escalabilidade para grandes volumes

## Trade-offs

### Vantagens

* Baixo consumo de memória
* Melhor desempenho em grandes volumes
* Menor pressão no Garbage Collector
* Escalabilidade superior

### Desvantagens

* Implementação mais complexa
* Dependência de JDBC
* Menor abstração comparado ao JPA

## Conclusão

Para exportações de grande volume, JDBC Streaming demonstrou ser significativamente mais eficiente que a abordagem tradicional baseada em carregamento completo dos dados.

O experimento com 500.000 registros mostrou que é possível reduzir o consumo de memória de 776 MB para aproximadamente 35 MB, além de obter melhor tempo de execução, tornando a solução adequada para sistemas que precisam gerar relatórios em escala.
