<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Публичные заметки</title>
    <style>
        body { font-family: sans-serif; max-width: 900px; margin: 40px auto; }
        .note { border: 1px solid #ccc; padding: 16px; border-radius: 8px; margin-top: 16px; }
        .meta { color: #666; font-size: 14px; }
        .content { white-space: pre-line; }
        input[type=text] { width: 100%; box-sizing: border-box; padding: 8px; }
    </style>
</head>
<body>
<h1>Публичные заметки</h1>
<form method="get" action="/notes/public">
    <label>Поиск
        <input type="text" name="query" value="${query!}" placeholder="Поиск по заголовку и содержимому"/>
    </label>
    <button type="submit">Найти</button>
</form>

<#if notes?has_content>
    <#list notes as note>
        <div class="note">
            <h2>${note.title?html}</h2>
            <p class="meta">Автор: ${note.author.username?html} | Создано: ${note.createdAtDisplay}</p>
            <p class="content">${note.content?html}</p>
        </div>
    </#list>
<#else>
    <p>Публичных заметок пока нет.</p>
</#if>

<p><a href="/">На главную</a></p>
</body>
</html>
