# DADProyecto

## Primer entregable: Gestion de peticiones con servlets

Este entregable tiene como objetivo implementar un servlet que gestione peticiones GET y POST para un tipo de datos definido. Se espera que ambos métodos devuelvan una respuesta en formato JSON. En particular, el método doPOST deberá recibir un JSON en el cuerpo de la solicitud que contenga la instancia del tipo de datos que se desea añadir. Los datos se almacenarán temporalmente en memoria, utilizando estructuras como Map o List, sin necesidad de conexión a una base de datos.

## Segundo entregable: Implementacion de endpoints con vert.x 

En este entregable, se implementarán los endpoints necesarios para la gestión de la API Rest del proyecto utilizando Vert.x. Se incluirán métodos de consulta (GET) e inserción (POST) para los tipos de datos identificados, como Sensor y Actuador. Los endpoints GET permitirán obtener el listado de valores de un sensor y el último valor reportado. Al igual que en el entregable anterior, los datos se almacenarán en memoria usando Map o List. Se valorará la implementación de funcionalidades adicionales.

## Tercer entregable: Conexion de base de datos 

Este entregable se centrará en reemplazar los repositorios en memoria (Map o List) por una conexión a una base de datos. La nueva implementación permitirá gestionar la información necesaria para la API Rest de manera persistente, mejorando así la capacidad de almacenamiento y recuperación de datos.

## Cuarto entregable: Codigo Firmware

Se desarrollará el código firmware necesario para realizar peticiones a los endpoints definidos en el entregable anterior. Este firmware permitirá obtener datos de los sensores e insertarlos en la base de datos mediante llamadas al método POST correspondiente. Este componente es crucial para la interacción efectiva entre el hardware y el servidor.

## Quinto entregable: Funcionalidad MQTT

El último entregable consiste en implementar la funcionalidad MQTT para permitir el intercambio de mensajes entre el servidor y la placa. Esta funcionalidad permitirá que el servidor envíe mensajes a la placa indicando el nuevo estado de los actuadores cuando se reciba un valor actualizado de un sensor. Además, la placa deberá realizar llamadas al método POST correspondiente en la API Rest para almacenar el nuevo estado del actuador. Esta característica mejorará la comunicación bidireccional y la sincronización entre los dispositivos y el servidor.
