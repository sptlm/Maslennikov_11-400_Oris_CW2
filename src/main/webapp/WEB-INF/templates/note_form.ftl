<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${formTitle}</title>
    <style>
        body { font-family: sans-serif; max-width: 800px; margin: 40px auto; }
        label { display: block; margin-top: 16px; }
        input[type=text], textarea { width: 100%; box-sizing: border-box; padding: 8px; }
        textarea { min-height: 220px; }
        .actions { margin-top: 16px; }
    </style>
</head>
<body>
<h1>${formTitle}</h1>
<form method="post" action="${formAction}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <label>Заголовок
        <input type="text" name="title" value="${(noteForm.title?html)!}" required/>
    </label>

    <label>Содержимое
        <textarea name="content" required>${(noteForm.content?html)!}</textarea>
    </label>

    <label>
        <input type="checkbox" name="public" <#if noteForm["public"]>checked</#if>/>
        Публичная заметка
    </label>

    <div class="actions">
        <button type="submit">Сохранить</button>
        <a href="/notes">Назад к моим заметкам</a>
    </div>
</form>
</body>
</html>
