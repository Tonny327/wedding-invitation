# wedding-invitation

Приложение приглашения на свадьбу: страница с формой RSVP и админка для просмотра гостей.

## Деплой на Render.com

### Что уже сделано в проекте

1. **Поддержка `DATABASE_URL`** — приложение читает переменную окружения `DATABASE_URL` (формат Render/Heroku) и подключается к PostgreSQL. Локально по-прежнему можно использовать `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` в `application.properties`.

2. **Порт** — используется переменная `PORT`, которую задаёт Render (`server.port=${PORT:8080}`).

3. **Файл `render.yaml`** — Blueprint для развёртывания Web Service (Docker) и PostgreSQL одной кнопкой.

### Шаги деплоя

1. **Репозиторий в Git**  
   Закоммитьте проект и запушьте в GitHub, GitLab или Bitbucket.

2. **Регистрация на Render**  
   Зайдите на [render.com](https://render.com/) и войдите через аккаунт Git.

3. **Создание Blueprint**  
   - В Dashboard: **New → Blueprint**.  
   - Выберите репозиторий и ветку с проектом.  
   - Render подхватит `render.yaml` из корня репозитория.  
   - Нажмите **Apply** — будут созданы база PostgreSQL (free) и Web Service из Dockerfile.

4. **Подключение БД к сервису**  
   Если переменная `DATABASE_URL` не подставилась автоматически:  
   - Откройте созданную базу **wedding-db** → вкладка **Connect**.  
   - Скопируйте **Internal Database URL**.  
   - Откройте сервис **wedding-invitation** → **Environment** → добавьте переменную `DATABASE_URL` и вставьте этот URL.  
   - Сохраните и дождитесь перезапуска сервиса.

5. **Адрес приложения**  
   После деплоя приложение будет доступно по адресу вида:  
   `https://wedding-invitation-xxxx.onrender.com`

### Важно

- **Free tier**: сервис может «засыпать» после неактивности; первый запрос после простоя может идти дольше.  
- Регион в `render.yaml` задан как `frankfurt`; при желании можно сменить на `oregon` и т.д.  
- Для продакшена имеет смысл отключить `spring.jpa.show-sql=true` (например, через профиль `production`).

### Локальный запуск

```bash
# PostgreSQL должен быть запущен (например, через docker-compose)
./mvnw spring-boot:run
```

Либо с переменными для локальной БД:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/wedding_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```
