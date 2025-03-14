# Ticket Booking Service
REST-сервис для онлайн-бронирования билетов на мероприятия

## Описание проекта
Этот сервис позволяет пользователям регистрироваться, бронировать билеты на мероприятия, а администраторам — управлять событиями.

## Основные возможности
- Регистрация и аутентификация: пользователи создают аккаунты и получают JWT-токены с ролями (ROLE_USER / ROLE_ADMIN).
- Управление мероприятиями:
  - Все пользователи могут просматривать список мероприятий.
  - Администраторы (ROLE_ADMIN) могут добавлять, изменять и удалять мероприятия.
- Бронирование билетов:
  - Только пользователи (ROLE_USER) могут бронировать билеты.
  - Транзакционная логика предотвращает конфликты при одновременных бронированиях.
- Логирование уведомлений:
  - После успешного бронирования запускается асинхронная задача, эмулирующая отправку уведомления.
  - Логи уведомлений сохраняются в БД (notifications_log).
- Кеширование:
  - Список мероприятий кэшируется.
  - Кеш сбрасывается при изменении данных.
- Swagger (Springdoc OpenAPI):
  - Позволяет тестировать API без фронтенда.
  - Документирует все эндпоинты.
 
## Стек технологий
| Компонент | Технология | 
|----------------|:---------:|
| Язык программирования | Java 17 |
| Фреймворк | Spring Boot 3 | 
| Безопасность | Spring Security + JWT |
| База данных | PostgreSQL | 
| ORM | Spring Data JPA (Hibernate) |
| Кеширование | Spring Cache | 
| Документация API | Springdoc OpenAPI (Swagger) |
| Сборка и запуск | Maven / Docker | 

## Архитектурные решения

### Безопасность 
- Реализована ролевая модель:
  - ROLE_USER — просмотр мероприятий и бронирование билетов.
  - ROLE_ADMIN — управление мероприятиями.
- Аутентификация через JWT-токены.
- Пароли хранятся в базе в захешированном виде (BCrypt).
- Эндпоинты защищены с помощью Spring Security.
### Транзакции
- Используется Spring @Transactional для предотвращения гонок данных.
- Настроен уровень изоляции SERIALIZABLE при бронировании билетов.
### Кеширование 
- Запросы на получение списка мероприятий кешируются в Spring Cache.
- Кеш автоматически сбрасывается при изменении событий.
### Асинхронность
- После бронирования асинхронно логируется отправка уведомления.
- Уведомления записываются в БД в таблицу notifications_log.

## Инструкция по развертыванию
### Клонирование репозитория
```
git clone https://github.com/your-repo/ticket-booking-service.git
```
### Создание локального файла конфигурации
Файл application-local.yml не хранится в репозитории по соображениям безопасности.
Его нужно создать вручную в директории src/main/resources/

Пример application-local.yml:
```
spring:
    datasource:
        url: jdbc:postgresql://localhost:5434/your_db
        username: your_username
        password: your_password
    flyway:
        url: jdbc:postgresql://localhost:5434/your_db
        user: your_username
        password: your_password
jwt:
    secret: your_secret
```
