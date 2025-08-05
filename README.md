David Zaragoza Garcia 
isczaragoza@gmail.com

Android Developer | Software Arquitect

Desafio técnico para el proceso de selección para la vacante Senior Android Developer en la empresa Ualá.

El proyecto se realizó con un enfoque nativo para el sistema operativo Android.

Con el lenguaje de programación Kotlin

Para abordar el diseño de la aplicación utilicé los siguientes elementos:

- Clean Architecture (Se definen las siguientes capas: UI, Infrastructure, Domain, Data)
- DDD
- MVVM patrón de arquitectura
- Patrón Repository
- Modularización por feature y layer

Sobre proceso de la definición de arquitectura tomé la decision de implentar la modularización por feature - layer
para respetar los principios de cohesion y bajo acoplamiento.
Y separación de responsabilidades.
Una vez realizada la modularización implementé los principios de la arquitectura limpia
a cada capa.
La capa de UI implementa el patrón arquitectonico MVVM
La capa de datos tiene implementado el patron repositorio.
La capa de dominio respeta principios DDD

Seguí los principios S.O.L.I.D.

Definiciones de cada capa:

Features: 
Código relacionado a la vista de cada funcionalidad, viewmodels, estados.

Infrastructure: 
Código relacionado a implementaciones especificas del Framework de Android,
implementaciones especificas de clientes HTTP, motores de Base De Datos, ORM, otros tipos
de persistencias, servicios.

Data: 
Código relacionado a la forma en que la aplicación maneja la información respecto
a funtes locales, fuentes remotas, sincronización, el trato que se le da a la información
y lógica de datos.

Domain: Código relacionado al negocio, reglas de negocio, lógica de negocio, interfaces 
de servicios, interfaces de repositorios, interfaces de managers, modelos y casos de uso.

Diágrama de alto nivel de la estructura del proyecto:

___________________________________________________________________
|                          Features                               |
|                 CityList, CityMap, CityDetail                   |
|                                                                 |
|_________________________________________________________________|
                              |
                              |
                              |
                              |
                              |
                              V
___________________________________________________________________
|                          Infrastructure                         |
|         Framework/HTTP/Database/DesignSystem/UiTheme)           |
|                                                                 |
|_________________________________________________________________|
                              |
                              |
                              |
                              |
                              |
                              |
                              V
___________________________________________________________________
|                           Data                                  |
|     RepositoriesImpl/DataSources/DTOs/@Entities/Mappers         |
|                                                                 |
|_________________________________________________________________|
                              |
                              |
                              |
                              |
                              |
                              V
___________________________________________________________________
|                          Domain                                 |
|            UseCases/Models/Repositories/Interfaces              |
|                                                                 |
|_________________________________________________________________|

Tecnologias clave:
En esta sección quiero mencionar algunas de las técnologias que implementé
para el desarrollo.

- Peticiones de red: 
Retrofit

- Persistencia local:
RoomDB

- Inyección de dependencias:
Dagger Hilt

- Serialización y conversión:
KotlinX Serializer
Gson

Concepto de negocio:
Aplicación (producto) que descarga una lista de ciudades de internet
y se muestran en pantalla.

Reglas de negocio:
Se ordenan por nombre y pais, se muestran en pantalla ordenadas,
se deben poder filtrar por nombre completo o por fracciones de texto
a través de un campo de busqueda, cada ciudad se puede marcar como
favorita y esa marca debe persistir a través del tiempo y sesiones.
Cada ciudad se debe mostar con un marcador en un mapa en una pantalla extra.

Lenguaje de negocio:
Ciudad, Mapa, Detalles, Locación, Latitud, Longitud...

Reto técnico:
El reto técnico me gustaría mencionar que recae sobre la cantidad de 
información que va a ser descargada desde internet, al inicio el dato
que se tenía era que se iba a descargar un archivo de 200 000 registros
esto mencionado en la propia documentación proporcionada.

Limitación técnica:
No todos los dispositivos tienen la capacidad de manejar en tiempo de ejecución
y memoria registros tan grandes.

Problema de rendimiento:
Derivado de la limitación técnica, eso puede causar problemas de rendimiento
visibles al usuario como lag, mensajes como ANR o el peor escenario desbordamiento
de memoria causando que la aplicación tenga un fallo fatal cerrandose.

Desafio de optimización:
Ya que se conocen los detalles de producto y desarrollo, me gusta definir la estrategia
para abordar el desarrollo de manera optima y que cumpla satifactoriamente con el
requerimiento.

Solución:
Descargar datos de manera progresiva e insertar gradualmente la información en la 
base de datos local, llevando un registro del progreso.

Reto de escalabilidad:
Que pueda manejar registros de información aun mas grandes que los del reto proporiconado.

Implementaciones Clave:

Proceso de sincronización Red/Base de datos:

Para el escenario de la aplicación intento descargar el JSON de 200k aproximadamente de una forma optima,
para eso probé multiples enfoques como Json.decodeFromString, pero era susceptible a problemas de
memoria ya que se guardan los registros en un String y eso no es muy poco optimo. 
También probé enfoques de usar estructuras de datos con mejor rendimiento como HashMap, 
pero debido a las multiples reglas de negocio incluso usando esas estructuras operar con ellas es 
lento y susceptible a errores.
Al final me decidí por un proceso de Streaming basado en Retrofit, que aunque requirió mayor desarrollo
fue una solución funcional, optima y escalable.
El proceso en Streaming permite descargar grandes volumenes de información de manera progresiva para 
poder procesar desde DTO al tipo de objeto necesario para tu capa de datos/persistencia sin guardar nada en 
memoria.
Dando detalle del proceso, se leen los bytes, se parsean y se insertan en base de datos progresivamente.
Con esta solución se puede escalar a volumenes de datos muy grandes sin presentar ningun problema, 
podríamos decir que se adapta a casi cualquier nuevo requerimiento de datos (si se respeta el formato JSON)

Proceso de ordenamiento y filtrado:
El proceso de ordenamiento lo delegué a la capa de datos, creando en el DAO de Retrofit la consulta (SELECT)
para ordenar por nombre, pais y finalmente id en forma ascendente y la condicion (LIKE) para filtrar por string.
Por qué tomé esta decisión? Para evitar cargar toda la consulta en un HashMap o Array y recaer de nuevo
en los problemas de memoria o manejo de estructuras demasiado cargadas de información, incluso limitando
el SELECT o implementando el páginado, ordenear sobre las estructuras de datos no es una opción muy optima.
Además el filtrado es un segundo proceso de iteración/busqueda sobre la estructura.

Para este enfoque de delegar la responsabilidad al DAO, exploré varios enfoques, entre ellos los siguientes:
 - La primera posible solución fue implementar un sistema FTS4 para optimizar todo el proceso de 
   ordenamiento y filtrado, en teoria esta es la opción mas conveniente por rendimiento, pero por
   los requerimientos del producto en el filtrado de datos, donde explicitamente se solicita que
   se compare por substrings "a", "ab", "alb", "me", etc, no por coincidencias dentro del string(nombre),
   esta solución no es compatible, ya que tokeniza y busca todas las coincidencias dentro del string,
   devoliendo por ejemplo para el string "me" coincidencias tales como: "america", "mexico", "amecameca", etc...
   Descartando esta solución por temas de diseño.
 - Al final la solución elegida fue usar Entidades normales, crear los indices correspondientes para
   optimizar la busqueda y declarando los operadores de ordenamiento.

Extras:
- Para mostrar la información en pantalla decidí mostrar una barra de carga que muestra en tiempo real
el proceso de descarga/almacenamiento.
- Tambien para mostrar la lista de ciudades implementé una consulta que me notificara los cambios
para simular una vista tipo stream, que va mostrando como aparecen los elementos.
- Para mantener la información actualizada y que no estuviera atada al ciclo de vida de un
componente particular implenté un WorkManager que permite que la descarga persista aunque
se cierre la aplicación o la aplicación tenga cambios de configuración o rotaciones de pantalla.



<img width="348" height="692" alt="Captura de pantalla 2025-08-04 a la(s) 10 36 13 p m" src="https://github.com/user-attachments/assets/e18b44b0-e02c-48d0-a2f5-84f477d8ea55" />
<img width="344" height="692" alt="Captura de pantalla 2025-08-04 a la(s) 10 37 57 p m" src="https://github.com/user-attachments/assets/66fd5055-1ebe-4bcc-8e03-50a523c571be" />
<img width="353" height="702" alt="Captura de pantalla 2025-08-04 a la(s) 10 38 10 p m" src="https://github.com/user-attachments/assets/6a656a68-24bc-4465-bd5b-c9aca8df6f92" />
<img width="328" height="691" alt="Captura de pantalla 2025-08-04 a la(s) 10 38 54 p m" src="https://github.com/user-attachments/assets/6c4d8404-84c9-4cb8-b2ec-7eb2e3764157" />
<img width="338" height="697" alt="Captura de pantalla 2025-08-04 a la(s) 10 39 30 p m" src="https://github.com/user-attachments/assets/ccdee1de-0042-40a8-bca6-19a6fbe81405" />
<img width="345" height="693" alt="Captura de pantalla 2025-08-04 a la(s) 10 39 46 p m" src="https://github.com/user-attachments/assets/0ff473e5-506e-4228-b5a6-85dd3047f914" />
<img width="327" height="691" alt="Captura de pantalla 2025-08-04 a la(s) 10 48 35 p m" src="https://github.com/user-attachments/assets/76380b7d-e53c-42d3-907c-89b52b87229a" />
<img width="338" height="695" alt="Captura de pantalla 2025-08-04 a la(s) 10 40 39 p m" src="https://github.com/user-attachments/assets/f0b6bda2-82d0-4a80-b655-202835ea32c9" />
<img width="330" height="691" alt="Captura de pantalla 2025-08-04 a la(s) 10 51 17 p m" src="https://github.com/user-attachments/assets/6b30f2fd-8eeb-439b-9497-f30d93717da9" />



