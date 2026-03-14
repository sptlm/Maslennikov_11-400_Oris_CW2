<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
    <style>
        body { font-family: sans-serif; max-width: 400px; margin: 60px auto; }
        label { display: block; margin-top: 12px; }
        input[type=text], input[type=password] {
            width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box;
        }
        button { margin-top: 16px; padding: 8px 20px; cursor: pointer; }
        .error { color: red; margin-top: 8px; }
    </style>
</head>
<body>
<h2>Регистрация</h2>

<#if error??>
    <p class="error">${error}</p>
</#if>

<form method="post" action="/register">
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}"/>

    <label>Имя пользователя
        <input type="text" name="username" required autofocus/>
    </label>

    <label>Пароль
        <input type="password" name="password" required minlength="6"/>
    </label>

    <button type="submit">Зарегистрироваться</button>
</form>

<p><a href="/login">Уже есть аккаунт? Войти</a></p>
</body>
</html>
