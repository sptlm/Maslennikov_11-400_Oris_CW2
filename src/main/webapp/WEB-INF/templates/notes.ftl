<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Мои заметки</title>
    <style>
        body { font-family: sans-serif; max-width: 900px; margin: 40px auto; }
        .actions, .note { margin-top: 16px; }
        .note { border: 1px solid #ccc; padding: 16px; border-radius: 8px; }
        textarea { width: 100%; min-height: 160px; }
        .meta { color: #666; font-size: 14px; }
        .content { white-space: pre-line; }
        form.inline { display: inline; }
        a.button, button { display: inline-block; padding: 8px 16px; margin-right: 8px; }
    </style>
</head>
<body>
<h1>Мои заметки</h1>
<div class="actions">
    <a class="button" href="/notes/create">Создать заметку</a>
    <a class="button" href="/notes/public">Публичные заметки</a>
</div>

<#if notes?has_content>
    <#list notes as note>
        <div class="note">
            <h2>${note.title?html}</h2>
            <p class="meta">Создано: ${note.createdAtDisplay} | Статус: <#if note["public"]>Публичная<#else>Приватная</#if></p>
            <p class="content">${note.content?html}</p>
            <a class="button" href="/notes/${note.id}/edit">Редактировать</a>
            <form class="inline" method="post" action="/notes/${note.id}/delete">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit">Удалить</button>
            </form>
        </div>
    </#list>
<#else>
    <p>У вас пока нет заметок.</p>
</#if>

<p><a href="/">На главную</a></p>
</body>
</html>
