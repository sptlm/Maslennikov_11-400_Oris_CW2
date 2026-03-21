<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Главная страница</title>
    <style>
        body { font-family: sans-serif; max-width: 900px; margin: 40px auto; }
        .card { border: 1px solid #ccc; border-radius: 10px; padding: 20px; margin-top: 20px; }
        .links { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 16px; }
        .link-btn, button { display: inline-block; padding: 10px 16px; border: 1px solid #999; border-radius: 8px; text-decoration: none; color: #000; background: #f8f8f8; }
        .muted { color: #666; }
        form { display: inline; }
    </style>
</head>
<body>
<h1>Главная страница</h1>

<div class="card">
    <#if isAuthenticated>
        <p>Вы вошли как <strong>${username?html}</strong>.</p>
    <#else>
        <p>Вы не авторизованы.</p>
    </#if>
    <p class="muted">Здесь собраны основные переходы по приложению.</p>
</div>

<div class="card">
    <h2>Навигация</h2>
    <div class="links">
        <a class="link-btn" href="/">Главная</a>
        <a class="link-btn" href="/notes/public">Публичные заметки</a>

        <#if isUser || isAdmin>
            <a class="link-btn" href="/hello">Hello</a>
            <a class="link-btn" href="/notes">Мои заметки</a>
        </#if>

        <#if isAuthenticated>
            <form method="post" action="/logout">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit">Выйти</button>
            </form>
        <#else>
            <a class="link-btn" href="/login">Логин</a>
            <a class="link-btn" href="/register">Регистрация</a>
        </#if>

        <#if isAdmin>
            <a class="link-btn" href="/admin/notes">Админка заметок</a>
        </#if>
    </div>
</div>
</body>
</html>