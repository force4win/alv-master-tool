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
  - Implementación de una capa de persistencia (Patrón Factory).
  - Corrección de errores en `TrackerService` (métodos duplicados, resolución de `App` class).
  - Trabajo en la carga de vistas FXML (`reminder_popup.fxml`).
- **Tareas en curso/pendientes**:
  - Consolidar la funcionalidad del `TrackerService`.
  - Asegurar la correcta integración de la capa de persistencia.
  - Verificación del flujo de la interfaz gráfica (Popups, recordatorios).

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
