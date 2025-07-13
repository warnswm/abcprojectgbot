# Telegram Bot REST API

REST API для отправки сообщений в Telegram бот с авторизацией пользователей.

Telegram бот: [@abcbottestprojectbot](https://t.me/abcbottestprojectbot)

## Быстрый запуск

Требования: Docker

```bash
# Запустить
docker-compose up -d

# Проверить
curl http://localhost:8080/actuator/health
```

## API endpoints

### Регистрация
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "test",
    "password": "123456", 
    "name": "Test User"
}
```

### Авторизация  
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "test",
    "password": "123456"
}
```

Возвращает JWT токен.

### Генерация Telegram токена
```http
POST /api/telegram/generate-token
Authorization: Bearer JWT_TOKEN
```

### Получение текущего токена
```http
GET /api/telegram/current-token
Authorization: Bearer JWT_TOKEN
```

### Отвязка токена
```http
POST /api/telegram/unlink
Authorization: Bearer JWT_TOKEN
```

### Отправка сообщения
```http
POST /api/messages/send
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
    "content": "Hello world"
}
```

### Получение сообщений
```http
GET /api/messages/my
Authorization: Bearer JWT_TOKEN
```

## Тестирование

### Windows PowerShell
```powershell
# Регистрация
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body '{"username":"test","password":"123456","name":"Test"}' -ContentType "application/json"
$token = $response.token

# Генерация Telegram токена
$headers = @{"Authorization" = "Bearer $token"}
$tgResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/telegram/generate-token" -Method Post -Headers $headers
Write-Host "Telegram token: $($tgResponse.token)"

# Отправка сообщения
Invoke-RestMethod -Uri "http://localhost:8080/api/messages/send" -Method Post -Body '{"content":"Test message"}' -ContentType "application/json" -Headers $headers

# Отвязка токена
Invoke-RestMethod -Uri "http://localhost:8080/api/telegram/unlink" -Method Post -Headers $headers
```

### Linux/Mac
```bash
# Регистрация
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","name":"Test"}')

TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# Генерация Telegram токена
TG_RESPONSE=$(curl -s -X POST http://localhost:8080/api/telegram/generate-token \
  -H "Authorization: Bearer $TOKEN")

TG_TOKEN=$(echo $TG_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Telegram token: $TG_TOKEN"

# Отправка сообщения
curl -X POST http://localhost:8080/api/messages/send \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Test message"}'

# Отвязка токена
curl -X POST http://localhost:8080/api/telegram/unlink \
  -H "Authorization: Bearer $TOKEN"
```

## Привязка к Telegram

1. Найдите бота [@abcbottestprojectbot](https://t.me/abcbottestprojectbot)
2. Отправьте ему токен из `/api/telegram/generate-token`
3. Бот ответит "Токен успешно привязан!"
4. Сообщения через API будут дублироваться в Telegram

### Команды бота:
- `/start` - показать справку
- `/unlink` - отвязать токен от чата

**Отвязка:** 
- Через API: `/api/telegram/unlink`
- Через Telegram: `/unlink` в чате с ботом

После отвязки сообщения перестанут поступать в Telegram.

## Проблемы

### Приложение не запускается
```bash
docker ps
docker-compose logs
docker-compose restart
```

### Проверка статуса
```bash
curl http://localhost:8080/actuator/health
```

### Перезапуск
```bash
docker-compose down
docker-compose up -d
```

## Альтернативный запуск

### Только БД в Docker
```bash
docker-compose up -d postgres
./gradlew bootRun
```

### Без Docker
Нужен PostgreSQL с базой `telegram_bot_db`
```bash
./gradlew bootRun
```

## Технические детали

- Java 17 + Spring Boot
- PostgreSQL
