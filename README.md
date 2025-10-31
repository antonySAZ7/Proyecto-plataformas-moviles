# Proyecto-plataformas-moviles
Repositorio del proyecto de plataformas moviles




Descripcion
ProyectoPlatsADJ es una aplicacion de gestion de tareas que permite a los usuarios organizar sus actividades diarias, visualizar un calendario y crear nuevas tareas con diferentes niveles de prioridad.

Caracteristicas principales:
Visualizacion de tareas del dia (Home)
Calendario mensual con seleccion de fechas
Creacion de nuevas tareas con detalles y prioridades
Lista completa de todas las tareas
Interfaz moderna con Material Design 3
NOTA IMPORTANTE - DATOS FALSOS
Para el Entregable 5, esta aplicacion utiliza datos falsos (hardcoded) para demostrar la navegacion entre pantallas.

No hay integracion real con base de datos (Room esta configurado pero no implementado aun)
No hay ViewModels - Los estados se manejan con remember y mutableStateOf
Los datos son simulados - Las tareas mostradas son ejemplos estaticos
Objetivo del entregable: Demostrar navegacion type-safe y configuracion de librerias
Esto es intencional para cumplir con los requisitos del entregable que solicitan:

Navegacion funcional entre pantallas 
Configuracion de librerias externas 
README documentando servicios 
APK funcional al 100% 
La implementacion completa con base de datos real y arquitectura MVVM se realizara en entregas posteriores.

Arquitectura
El proyecto sigue una estructura modular preparada para:

MVVM (Model-View-ViewModel) - A implementar en futuras entregas
Clean Architecture con separacion de capas
Single Activity con navegacion Compose


Servicios y Librerias Externas
1. Navegacion Type-Safe 
Libreria: androidx.navigation:navigation-compose:2.8.3
Plugin: org.jetbrains.kotlin.plugin.serialization
Proposito: Navegacion entre pantallas con seguridad de tipos usando @Serializable
Uso en el proyecto:
Conecta las 4 pantallas principales (Home, Calendar, TasksList, NewTask)
Usa objetos serializables en lugar de rutas string: @Serializable object HomeDestination
Evita errores de runtime con rutas incorrectas
Implementado en AppNavHost.kt
Por que es importante: La navegacion type-safe previene errores comunes como typos en rutas y permite pasar datos entre pantallas de forma segura con verificacion en tiempo de compilacion
2. Retrofit (Configurado - No implementado)
Libreria: com.squareup.retrofit2:retrofit:2.11.0
Dependencias adicionales:
converter-gson:2.11.0 (serializacion JSON)
okhttp:4.12.0 (cliente HTTP)
logging-interceptor:4.12.0 (debug de requests)
Proposito: Cliente HTTP para consumir APIs REST
Uso futuro:
Sincronizar tareas con un backend
Autenticacion de usuarios
Obtener datos remotos
Estado actual: Libreria agregada pero sin endpoints configurados 
3. Room Database  (Configurado - No implementado)
Libreria: androidx.room:room-runtime:2.6.1
Dependencias adicionales:
room-ktx:2.6.1 (soporte de Coroutines)
room-compiler:2.6.1 (procesador KSP)
Proposito: Base de datos SQLite local para persistencia de datos
Uso futuro:
Guardar tareas localmente en el dispositivo
Cache de datos offline
No requiere conexion a internet
Estado actual: Libreria configurada, tablas y DAOs pendientes de implementacion
Por que es importante: Permite que las tareas persistan entre sesiones de la app, incluso sin internet
4. Kotlinx Serialization
Libreria: org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3
Proposito: Serializacion y deserializacion de datos en Kotlin
Uso en el proyecto:
Necesario para la navegacion type-safe con @Serializable
Serializar/deserializar objetos JSON
Estado actual: Activo, usado en las destinations de navegacion
5. Kotlinx DateTime  (Configurado - Uso limitado)
Libreria: org.jetbrains.kotlinx:kotlinx-datetime:0.6.1
Proposito: Manejo moderno de fechas y tiempos en Kotlin multiplataforma
Uso futuro:
Formatear fechas en el calendario
Calcular fechas de vencimiento de tareas
Determinar que tareas son "para hoy"
Ordenar tareas por fecha
Estado actual: Configurado pero con datos de fecha simulados (strings)
6. Kotlinx Coroutines  (Configurado - No implementado)
Libreria: org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1
Proposito: Programacion asincrona en Kotlin
Uso futuro:
Operaciones de base de datos sin bloquear UI
Llamadas a APIs de forma asincrona
Manejo de tareas en background
Estado actual: Dependencia agregada, se usara cuando se implementen ViewModels
7. Coil  (Configurado - No implementado)
Libreria: io.coil-kt:coil-compose:2.7.0
Proposito: Carga de imagenes asincrona optimizada para Compose
Uso futuro:
Cargar avatares de usuario
Iconos personalizados para categorias de tareas
Imagenes adjuntas a tareas
Estado actual: Opcional, no usado actualmente
8. Accompanist Permissions  (Configurado - No implementado)
Libreria: com.google.accompanist:accompanist-permissions:0.36.0
Proposito: Manejo simplificado de permisos en Android con Compose
Uso futuro:
Permisos de notificaciones para recordatorios
Permisos de camara para adjuntar fotos a tareas
Estado actual: Opcional, no usado actualmente

Tecnologias Utilizadas
Categoria	Tecnologia	Estado
Lenguaje	Kotlin 2.0.0	
UI Framework	Jetpack Compose	
Arquitectura	MVVM + Clean Architecture	 En progreso
Navegacion	Navigation Compose (Type-Safe)	 Implementado
Serializacion	Kotlinx Serialization	 Implementado
Base de Datos	Room Database	 Configurado
HTTP Client	Retrofit 2	 Configurado
Async	Kotlinx Coroutines	 Configurado
Manejo de Fechas	Kotlinx DateTime	 Configurado
Carga de Imagenes	Coil	 Configurado
Leyenda:

 Implementado y funcional
 Configurado, pendiente de implementacion
 En progreso
 Instalacion
Requisitos previos:
Android Studio Hedgehog (2023.1.1) o superior
Kotlin 2.0.0+
Gradle 8.0+
Android SDK 27+ (Minimo: Android 8.1 Oreo)

Descripcion: 
1. Splash Screen
Pantalla de carga inicial con logo y fondo `#F8F0E4`. Transición automática a Welcome tras 2 segundos.
3. Onboarding Screen
Pantalla de bienvenida con imagen de fondo, texto motivacional y botones para Registrarme o Iniciar Sesión.
4. Login Screen
Formulario de inicio de sesión con correo, contraseña y enlace a registro.
5. Register Screen
Formulario completo de registro: nombre, apellido, correo, contraseña (con confirmación).
6. Home Screen 
Muestra las tareas programadas para hoy (datos simulados)
Indicadores de prioridad por colores:
Rojo: Prioridad alta (1)
 Amarillo: Prioridad media (2)
 Verde: Prioridad baja (3)
Estados implementados: Loading, Error, Empty, Content
Datos de ejemplo: 3 tareas hardcoded
7. Calendar Screen 
Vista de calendario mensual (grid placeholder)
Seleccion de fechas simulada
Muestra la fecha "22 de octubre 2025"
Estados implementados: Loading, Error, Empty, Content
Nota: El calendario es un placeholder visual
8. Tasks List Screen 
Lista completa de todas las tareas (datos simulados)
Boton flotante (+) para agregar nuevas tareas
Navega a NewTask Screen al presionar el FAB
Organizacion por fecha (simulada)
Estados implementados: Loading, Error, Empty, Content
Datos de ejemplo: 5 tareas hardcoded
9. New Task Screen 
Formulario para crear tareas nuevas
Campos: Nombre, Detalles, Fecha de finalizacion, Importancia
Nota: Los datos NO se guardan realmente, solo simula el proceso
Al presionar "Agregar tarea" regresa a la pantalla anterior
Estados implementados: Idle, Loading, Error
Navegacion Bottom Bar (Agregada para demostracion)
Una barra de navegacion inferior fue agregada temporalmente para facilitar la demostracion del flujo entre pantallas:




