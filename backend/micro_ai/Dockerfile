# Используем официальный минимальный Python образ
FROM python:3.13-slim

# Устанавливаем переменные окружения
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Устанавливаем рабочую директорию
WORKDIR /app

# Устанавливаем системные зависимости
RUN apt-get update && apt-get install -y \
    build-essential \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Устанавливаем зависимости Python
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копируем весь проект в контейнер
COPY . .

# Собираем статику
RUN python manage.py collectstatic --noinput

# Открываем порт для Gunicorn
EXPOSE 8000

# Запуск Gunicorn сервера
CMD ["gunicorn", "micro_ai.wsgi:application", "--bind", "0.0.0.0:8000"]
