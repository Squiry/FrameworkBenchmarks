@import ru.tinkoff.kora.techempower.common.Fortune
@import java.util.List

@param List<Fortune> fortunes
<!DOCTYPE html>
<html>
<head><title>Fortunes</title></head>
<body>
<table>
<tr>
<th>id</th><th>message</th>
</tr>
@for(var fortune : fortunes)
    <tr><td>${fortune.id()}</td><td>${fortune.message()}</td></tr>
@endfor
</table>
</body>
</html>
