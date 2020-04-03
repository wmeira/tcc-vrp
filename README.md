# Final Project - Vehicle Routing Problem

Trabalho de Conclusão de Curso - Engenharia de Computação - UTFPR - 2013

*Final Project - Computer Engineering - UTFPR - 2013*

**Título**: Problema de Roteamento de Veículos com Entregas e Coletas Mistas e Janelas de Tempo: Aplicação em uma Empresa da Região Metropolitana de Curitiba

*Title: Vehicle Routing Problem with Mixed Pickup and Delivery and Time Windows: Application in a Company in the Metropolitan Region of Curitiba*

Orientador (*Supervisor*): Prof. Dr. Leandro Magatão

## Abstract

*The Operational Research (OR) is a branch of applied mathematics that has the purpose of modeling real problems in various areas of knowledge. One of the applications with emphasis in OR is the Vehicle Routing Problem (VRP). The objective of the work was to develop a solution approach for the vehicle routing problem of a haulier from the metropolitan region of Curitiba. The methodology takes into account constraints and characteristics of real scenarios for development and validation of the mathematical model. In particular, the studied problem is characterized as a variant of the VRP, the VRPMPDTW (Vehicle Routing Problem with Mixed Pickup and Delivery and Time Windows). In order to model and solve scenarios of the company, it is used a technique originated from OR, the Mixed Integer Linear Programming (MILP), associated with pre-processing and post-processing algorithms. Additionally, it was developed a human-computer interface in **Java** to manage scenarios and analyse results. The solver used for mathematical modeling was the **IBM ILOG CPLEX Optimization Studio 12.5 (updated to 12.8)**. Valid results were obtained in non-prohibitive computational time (seconds to few minutes) for typical scenarios of the company. Future contributions may focus on reducing the computational time for scenarios with similar or greater numbers of vehicles and services, since the response time tends to grow exponentially as these variables increase.*

**Keywords**: Operational Research (OR). Vehicle Routing Problem (VRP). VRPMPDTW. MILP. Transport Logistic.

---
The final project text may be found in the following repository (available only in Portuguese): http://repositorio.roca.utfpr.edu.br/jspui/handle/1/2205


## Running the project

- Requirements are Java +1.6 and IBM ILOG CPLEX Optimization Studio 12.8+
- Due to license purposes, the oplall.jar was removed from the repository. So, you need to copy your `oplall.jar` found in `{your.cplex.folder}/opl/lib/oplall.jar` to the `/lib` of this project. Consider copying the `oplall.jar` to the `/tcc-vrp128_lib` folder as well in order to use the already compiled `tcc-vrp128.jar` (or `tcc-vrp128.exe`) directly.

- The tcc-vrp screens are found in `/software_screens` folder.






