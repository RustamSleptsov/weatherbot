Для запуска приложения необходимо:
-создать телеграм бота;
-подключить тариф "тестовый" в разделе API Яндекс.Погода
-Добавить переменные среды:
telegram.token - Токен телеграм бота
yandex-weather.api-key - Ключ для API Яндекс.Погода
Добавить параметры переменных в application.yml в разделе location
(в моем проекте указаны данные города Олекминск)

Ссылка на Swagger
http://localhost:8015/swagger-ui/index.html#/

Сборка:
1. Запустите БД из docker-compose-local.yaml. Порт _27017_ доступен локально
2. Запустите приложение IDEA Spring Boot.

Приложение будет доступно на порту _8015_.

Ссылка на метрики
http://localhost:8015/actuator

P.S.
Приложение нацелено на демонстрацию навыков написания кода и
использования различных технологий.