# wedding-invitation

Приложение приглашения на свадьбу: страница с формой RSVP и админка для просмотра гостей.

## Деплой на Render.com

### Проблемы при деплое — итог

У вас было две вещи:

1. **«A Blueprint file was found, but there was an issue»** — Render не принимал Blueprint (render.yaml). Решение: развернуть **вручную** (без Blueprint): отдельно создать PostgreSQL и Web Service.
2. **«Connection to localhost:5432 refused»** при запуске на Render — приложение не видело переменную **DATABASE_URL** и подключалось к localhost. Решение: **обязательно** добавить в Web Service переменную окружения **DATABASE_URL** (Internal Database URL из вашей базы).

Ниже — пошаговый деплой на Render (ручной вариант, который точно работает).

---

### Деплой на Render по шагам (ручной, без Blueprint)

1. **Создать базу PostgreSQL**  
   В Render: **New → PostgreSQL**.  
   - Name: например `wedding-db`  
   - Plan: **Free**  
   Создать и дождаться статуса **Available**.

2. **Создать Web Service**  
   **New → Web Service**.  
   - Подключите репозиторий (GitHub/GitLab) и ветку (например `main`).  
   - **Name:** `wedding-invitation`  
   - **Region:** любой (например Oregon).  
   - **Runtime:** **Docker** (Render возьмёт Dockerfile из корня репо).  
   - **Instance Type:** Free.

3. **Обязательно: задать подключение к БД** (один из двух способов).

   **Способ A — одна переменная (рекомендуется)**  
   - База **wedding-db** → вкладка **Connect** → скопируйте **Internal Database URL**.  
   - Web Service → **Environment** → **Add Environment Variable**:  
     - **Key:** `DATABASE_URL`  
     - **Value:** вставьте скопированный URL.  

   **Способ B — три переменные (Spring Boot)**  
   - В **Connect** возьмите **External Database URL** или разберите хост/порт/имя БД/пользователя/пароль.  
   - В **Environment** добавьте:  
     - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://хост:5432/имя_бд` (из URL)  
     - `SPRING_DATASOURCE_USERNAME` = пользователь  
     - `SPRING_DATASOURCE_PASSWORD` = пароль  

   Сохраните (**Save Changes**).

4. **Создать сервис / задеплоить**  
   Нажмите **Create Web Service** (или дождитесь автоматического деплоя). Render соберёт образ по Dockerfile и запустит приложение. После успешного деплоя приложение будет доступно по адресу вида `https://wedding-invitation-xxxx.onrender.com`.

Если **DATABASE_URL** не добавлен, приложение снова упадёт с «Connection to localhost:5432 refused». Добавление этой переменной — обязательный шаг.

---

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

---

## Локальная разработка с Docker

### Когда контейнер должен быть запущен

**Да.** Подключаться к базе и запускать приложение можно только когда контейнеры запущены. База живёт внутри контейнера `db`; пока он не запущен, доступа к ней нет.

---

### Пошаговая инструкция

#### 1. Запустить контейнеры

В корне проекта (где лежит `docker-compose.yml`):

```bash
docker compose up -d
```

- `-d` — в фоне. Без `-d` логи будут в терминале.
- Поднимутся: PostgreSQL (`db`) на порту **5433**, приложение (`app`) на порту **8081**.

Проверить, что всё работает:

```bash
docker compose ps
```

Должны быть в состоянии `running` оба сервиса: `wedding-db` и `wedding-app`.

---

#### 2. Подключиться к базе через psql

**Важно:** в `docker-compose` PostgreSQL с хоста доступен по порту **5433**, а не 5432.

**Способ A — с вашего компьютера (если установлен psql):**

```bash
psql -h localhost -p 5433 -U postgres -d wedding_db
```

Пароль по умолчанию: `postgres`.

**Способ B — изнутри контейнера (psql не нужен на компьютере):**

```bash
docker compose exec db psql -U postgres -d wedding_db
```

Пароль не спросит (уже внутри контейнера).

Внутри psql полезно:
- `\dt` — список таблиц
- `\d guests` — структура таблицы `guests`
- `SELECT * FROM guests;` — данные
- `\q` — выход

---

#### 3. Запускать приложение без Docker (только база в Docker)

Если хотите запускать приложение через `./mvnw spring-boot:run`, а базу оставить в Docker:

1. Запустите только базу: `docker compose up -d db`
2. Задайте переменные (порт **5433**):

```bash
export DB_URL=jdbc:postgresql://localhost:5433/wedding_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

Приложение будет на http://localhost:8080 (или другой порт из настроек).

---

#### 4. Остановить контейнеры

```bash
docker compose down
```

Данные не удалятся (том `postgres_data` сохраняется). При следующем `docker compose up -d` база будет с теми же данными.

Удалить и данные:

```bash
docker compose down -v
```

---

### Кратко

| Действие              | Команда |
|-----------------------|--------|
| Запустить всё         | `docker compose up -d` |
| Подключиться к БД (хост) | `psql -h localhost -p 5433 -U postgres -d wedding_db` |
| Подключиться к БД (в контейнере) | `docker compose exec db psql -U postgres -d wedding_db` |
| Остановить            | `docker compose down` |
| Приложение в браузере | http://localhost:8081 (если подняли весь `docker compose`) |
