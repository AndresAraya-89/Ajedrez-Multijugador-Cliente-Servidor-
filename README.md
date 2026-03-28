Proyecto académico (Programación II - 2023)

Paso 1: Configuración de Roles y Conexión
Al iniciar la aplicación, se abrirán dos ventanas idénticas del juego. El primer paso crucial consiste en asignar los roles de jugador para establecer la conexión de red entre los clientes. Ambas ventanas muestran en la parte superior los botones de selección: 'Jugador 1' y 'Jugador 2'.
Para iniciar la conexión, debe seguir estos pasos en orden:
1.	En la primera ventana (por ejemplo, la de la izquierda), haga clic en el botón 'Jugador 1'.
2.	En la segunda ventana (derecha), haga clic en el botón 'Jugador 2'.
Esta acción vinculará los sockets de cada instancia y asignará las piezas correspondientes.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/7d3bd53d-5d5c-46b5-8eea-05263ccbdb2e" />

 
Paso 2: Realizando el Primer Movimiento (Jugador 1)
El Jugador 1, que controla las piezas blancas (arriba en la imagen), debe hacer clic en una de sus piezas (ej. un peón). La pieza seleccionada se resaltará visualmente en su pantalla. Luego, haga clic en una casilla de destino válida. El movimiento se sincronizará inmediatamente en la ventana del Jugador 2 a través del socket.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/b6cc632d-ecd1-41e8-8304-b2f1858c1071" />

 
Paso 3: Sincronización en Tiempo Real (Jugador 2)
El Jugador 2, que controla las piezas negras (abajo), verá el movimiento del Jugador 1 en su pantalla en tiempo real. Se le notificará que es su turno. Ahora, el Jugador 2 puede mover una de sus piezas.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/7f7e4eb0-a7eb-47c2-9ffd-a96b4f54b8c8" />


Paso 4: Flujo de Juego y Reglas
Continúe el juego de esta manera, alternando turnos. El motor de juego validará cada movimiento según las reglas estándar del ajedrez, asegurando una experiencia de juego justa y reglamentaria.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/4cd41423-cb07-4d2c-8797-bae9c893fc1b" />
 
