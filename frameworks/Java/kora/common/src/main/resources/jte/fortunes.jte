@import ru.tinkoff.kora.techempower.common.Fortune

@param List<Fortune> fortunes

!<!DOCTYPE html>
<html>
<head><title>Fortunes</title></head>
<body>
<table>
<tr>
<th>id</th><th>message</th>
</tr>
@for(var fortune : fortunes)
    <tr><td>${fortune.id()}}</td><td>${fortune.message()}}</td></tr>
@endfor
</table>
</body>
</html>






<head>
    @if(page.getDescription() != null)
        <meta name="description" content="${page.getDescription()}">
    @endif
    <title>${page.getTitle()}</title>
</head>
<body>
    <h1>${page.getTitle()}</h1>
    ${content}
</body>
