
Этот проект - тестовое задание для LightDigital

Сделал согласно ТЗ. Написал тесты для удобства проверки
Для запуска - настройте подключение к БД в src/main/resources/application.yaml

Эндпоинты и примеры запросов

Общие
POST /api/auth/login - залогинится
POST /api/auth/logout - разлогинится (удалить токен обновления)
PUT /api/auth/refresh_token - обновить токен

Пользователь
GET /api/statement/get - получить заявки
GET /api/statement/get/id - получить заявку
POST /api/statement/edit - создать заявку
PUT /api/statement/edit - изменить черновик
PUT /api/statement/send/id - отправить оператору

Оператор
GET /api/statement/get - получить заявки
GET /api/statement/get/id - получить заявку по id
GET /api/statement//get/by_user/id - получить отправленные заявки пользователя
PUT /api/statement/status/accept/id - принять заявку
PUT /api/statement/status/reject/id - отклонить заявку

Администратор
GET /api/statement/get - получить заявки
GET /api/statement/get/id - получить заявку по id
GET /api/statement/get/by_user/id - получить не черновые заявки пользователя
GET /api/admin/get_users - список пользователей
PUT /api/admin/make_operator/id - сделать пользователя оператором
PUT /api/admin/make_usual/id - понищить оператора