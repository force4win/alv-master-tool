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
