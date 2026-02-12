# Contexto del Proyecto: alv-master-tool

Este archivo sirve como memoria persistente para el asistente AI (Antigravity). Mantiene el estado actual del proyecto, decisiones arquitectónicas y un registro de las sesiones de trabajo.

## 1. Información General del Proyecto
- **Nombre**: alv-master-tool
- **Tecnologías**: Java 11, JavaFX 13, Maven
- **Estructura**:
  - `src/main/java/com/alv/mastertools/`
    - `App.java`: Punto de entrada de la aplicación.
    - `controllers/`: Controladores de JavaFX.
    - `models/`: Modelos de datos.
    - `persistence/`: Capa de persistencia (Implementación reciente).
    - `services/`: Lógica de negocio (TrackerService, etc.).

## 2. Estado Actual (Resumen de Contexto)
- **Últimos cambios importantes**:
  - Implementación completa de Gestión de Usuarios y Credenciales.
  - Sistema de Exportación y Limpieza de datos (CSV).
  - Generación de Instalador/Ejecutable portable (.exe) con `jpackage`.
  - Mejoras de usabilidad (Scrolls, Filtros de Fecha).
  - **Nuevo Módulo: Explorador Jerárquico (Miller Columns)** con Notas Integradas.
- **Estado**: Funcional y empaquetable. Versión 0.0.1 lista.

## 3. Registro de Sesiones
*Aquí se agregarán los resúmenes de cada sesión al ejecutar el comando "GUARDAR SESSION".*

### Sesión Inicial (Creación de data.md)
- Se ha analizado la estructura del proyecto maven.
- Se ha creado este archivo `data.md` para gestión de contexto.
- Se ha identificado la estructura de paquetes (`controllers`, `models`, `persistence`, `services`).
### Sesión: Refactorización UI y Temas (Nordic Night/Snow)
- **Implementación de Temas**:
  - Se reescribió `styles.css` utilizando variables CSS para definir colores semánticos.
  - Se creó un tema claro (`.light-theme`) "Nordic Snow" y se mantuvo el oscuro por defecto "Nordic Night".
  - Se agregó un botón de cambio de tema global en la barra superior de `primary.fxml`.
  - Se implementó la lógica en `PrimaryController` para alternar la clase raíz.

- **Rediseño de Interfaz (Look & Feel)**:
  - **Login**: Diseño tipo tarjeta, centrado, con nuevos estilos de input y botones.
  - **Tracker Config**: Reorganización de inputs, separación visual y jerarquía de botones (Primario vs Secundario).
  - **General**: Unificación de bordes redondeados, sombras suaves y tipografía.

- **Correcciones y Mejoras**:
  - `TrackerService`/`FileDataProvider`: Se corrigió la lectura/escritura de propiedades booleanas (`isActive`, `autoStartOnLogin`).
  - `styles.css`: Corrección de `border-radius` vs `background-radius` en botones transparentes.
  - `tracker_config.fxml`: Corrección de imports faltantes (`Region`) y lógica de estilos condicionales en el controlador.

### Sesión: Gestión de Usuarios, Datos y Despliegue
- **Gestión de Credenciales**:
  - Implementado almacenamiento seguro de credenciales en `settings.properties` dentro de `.alv-master`.
  - Nueva vista `user_config.fxml` y controlador para cambiar usuario/contraseña.
  - Actualización del Login para validar contra credenciales persistentes.
- **Gestión de Datos**:
  - Implementada funcionalidad de **Exportar a CSV** mediante `FileChooser`.
  - Implementada funcionalidad de **Borrar Historial** con confirmación.
  - Visualización de la ruta de almacenamiento en la configuración.
- **Despliegue (Installer)**:
  - Configuración de `maven-javafx-plugin` para soporte de `jlink`.
  - Creación del script `generate_exe.bat` para automatizar la creación de la imagen y el ejecutable.
  - Generación de aplicación portable (`app-image`) compatible con Windows sin requerir Java instalado.
- **Mejoras UI/UX**:
  - **Filtros de Fecha**: Agregados `DatePicker` (Desde/Hasta) en la vista de seguimiento para filtrar el historial.
  - **Responsividad**: Se envolvieron las vistas `user_config` y `tracker_config` en `ScrollPane` para asegurar visibilidad en pantallas pequeñas.
  - Restauración de etiquetas de estado y corrección de imports en FXML.

### Sesión: Explorador Jerárquico y Notas (Miller Columns)
- **Implementación de Navegación Jerárquica**:
  - Se creó el controlador `HierarchicalViewController` y la vista `hierarchical_view.fxml`.
  - Diseño estilo **Miller Columns** (Acordeón Horizontal) para navegación profunda de temas y subtemas.
  - Código de colores por nivel de profundidad para mejorar la orientación visual.
- **Sistema de Contenido y Notas**:
  - **Separación de Áreas**: Se dividió la vista en dos zonas claras:
    1.  **Navegación (Izquierda)**: Columnas dinámicas que se expanden hacia la derecha.
    2.  **Contenido (Derecha/Fondo)**: Panel "elástico" (`HBox.hgrow="ALWAYS"`) que muestra la información del tema seleccionado.
  - **Notas Adhesivas**: Implementación visual de notas tipo "Sticky Note" dentro del área de contenido.
  - **Edición**: Capacidad de agregar nuevas notas y nuevos subtemas dinámicamente desde la UI.
- **Integración**:
  - Se agregó la opción "EXPLORADOR" al menú principal en `primary.fxml`.
  - Ajuste de layout para manejo correcto del espacio disponible (Scroll horizontal + Contenido fluido).

### Sesión: Maximización de Ventana tras Login
- **Mejora UI/UX**:
  - Se modificó `LoginController.java` para llamar a `App.maximizeWindow()` después de una autenticación exitosa.
  - Se agregó el método estático `maximizeWindow()` en `App.java` para exponer la capacidad de maximizar el `Stage` actual.

### Sesión: Notas Post-It (Draggable & Resizable)
- **Mejora en Notas**:
  - Se transformó el sistema de notas para soportar posicionamiento libre y redimensionado.
  - **Estructura de Datos**: Se actualizó `Item` para usar `List<NoteData>` en lugar de `List<String>`, almacenando posición (x, y) y tamaño (width, height).
  - **Interacción**:
    - **Draggable**: Las notas pueden moverse libremente por el área de contenido mediante arrastrar y soltar desde la barra superior.
    - **Resizable**: Se agregó un control en la esquina inferior derecha para cambiar el tamaño de la nota.
  - **UI**: Cambio de contenedor de `VBox` a `Pane` para permitir posicionamiento absoluto. Estilo visual mejorado con bordes y sombras tipo Post-It.

### Sesión: Refinamiento de Notas y UX
- **Correcciones y Ajustes en Notas**:
  - **Interacción**: Se solucionó un conflicto de eventos (`e.consume()`) que causaba movimiento errático al arrastrar notas dentro del `ScrollPane`.
  - **Accesibilidad**: Se eliminaron restricciones de tamaño mínimo (ahora permite hasta 30x30px), asegurando siempre una barra de título de 20px clickeable para mover la nota.
  - **Área de Trabajo**: Se delimitó visualmente el área de notas (2000x2000) con bordes discontinuos y se forzó la aparición de scrollbars para evitar notas ocultas.
- **Sincronización de Navegación**:
  - Se corrigió el bug donde al regresar a un nivel superior en el acordeón, el contenido no se actualizaba. Ahora `restorePanel` sincroniza correctamente el área de contenido.
- **Configuración**:
  - Se cambió `App.IS_DEV_MODE` a `false` para simular entorno de producción.

### Sesión: Implementación de Borrado de Notas
- **Funcionalidad de Borrado**:
  - **Borrado Individual**: Se mejoró la visibilidad y funcionalidad del botón "X" en las notas. Se solucionó el conflicto con el evento de arrastre (drag) consumiendo el evento `MousePressed`.
  - **Borrado Masivo**: Se agregó un botón "Borrar Todas" en el área de input, incluyendo un diálogo de confirmación para evitar borrados accidentales.
  - **Borrado de Temas**: Se implementó la capacidad de eliminar temas y subtemas desde la navegación. Cada elemento de la lista ahora tiene un botón "X" que permite remover la sección completa (incluyendo hijos y notas) tras confirmación.

### Sesión: Persistencia de Datos
- **Almacenamiento Local**:
  - Se implementó la persistencia completa de temas, subtemas y notas utilizando Java Serialization.
  - Los datos se guardan automáticamente en `topics.dat` dentro del directorio `.alv-master` del usuario.
  - Se eliminaron los datos de prueba (`mockData`) y ahora la aplicación inicia cargando el estado guardado o creando una estructura vacía si es la primera vez.
  - El guardado se activa automáticamente tras cualquier modificación (agregar/borrar temas, agregar/editar/borrar/mover notas).

